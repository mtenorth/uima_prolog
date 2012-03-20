package net.htmlparser.jericho;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class RendererTest {

	@Test public void testBasics() throws Exception {
		assertEquals("text",
		      render("text"));
		assertEquals("This is a long sentence that should be broken into multiple lines at word \nboundaries, with each line no longer than the default 76 character maximum \nline length.",
		      render("This is a long sentence that should be broken into multiple lines at word boundaries, with each line no longer than the default 76 character maximum  line length."));
		// non-renderable elements are removed:
 		assertEquals("text",
		      render("te<applet>xyz</applet>xt"));
	}

	@Test public void testWhiteSpace() throws Exception {
		// collapse white space and trailing white space trimmed:
		assertEquals(" basic test",
		      render(" basic   test "));
		// convert nbsp to normal space:
		assertEquals("basic  test",
		      render("basic&nbsp; test"));
		// do not convert nbsp:
		assertEquals("basic\u00A0 test",
		    renderer("basic&nbsp; test").setConvertNonBreakingSpaces(false).toString());
		// whitespace trimmed at block boundaries:
		assertEquals("basic test\nx",
		      render("<div> basic test </div> x"));
		// whitespace trimmed at block boundaries even if there are intervening inline elements:
		assertEquals("basic test",
		      render("<div><span> basic test </span></div>"));
		// whitespace not trimmed at inline element boundaries, and none introduced:
		assertEquals("x basic testy",
		      render("<div>x<span> basic test</span>y</div>"));
		// render method used in these unit test automatically sets new line to "\n":
		assertEquals("basic\ntest",
		      render("basic<br>test"));
		// default Renderer always uses "\r\n" as new line:
		assertEquals("basic\r\ntest",
		      new Renderer(new Source("basic<br>test")).toString());
	}

	@Test public void testVerticalMargins() throws Exception {
		// no vertical margins around <div>, only line break:
		assertEquals("a\nb\nc",
		      render("a<div>b</div>c"));
		// only one line break around multiple successive block elements:
		assertEquals("a\nb\nc",
		      render("a<div> <div> b </div> </div>c"));
		// one line vertical margin above and below paragraphs:
		assertEquals("a\n\nb\n\nc",
		      render("a <p> b </p> c"));
		// vertical margins at start and end of text are not rendered:
		assertEquals("text",
		      render("<p> text </p>"));
		// <br> at start of text is rendered, unlike vertical margins:
		assertEquals("\nx",
		      render("<br>x"));
		// still one line vertical margin between paragraphs:
		assertEquals("a\n\nb\n\nc",
		      render("<p> a </p><p> b </p> c"));
		// divs do not affect vertical margins but ensure a line break:
		assertEquals("x\na\n\nb\n\nc",
		      render("x <div> a <p> b </p></div> c"));
		// two line vertical margin above heading blocks, and one line below:
		assertEquals("a\n\n\nb\n\nc",
		      render("<p> a </p><h1> b </h1> c"));
		// vertical margins at start and end of text are not rendered:
		assertEquals("Heading\n\nparagraph",
		      render("<h1>Heading</h1><p>paragraph</p>"));
		// include top margins at start (two lines for heading blocks):
		assertEquals("\n\nHeading\n\nparagraph",
		    renderer("<h1>Heading</h1><p>paragraph</p>").setIncludeFirstElementTopMargin(true).toString());
	}

	@Test public void testCSS() throws Exception {
		// no recognised styles
		assertEquals("a\nb\nc",
		      render("a<div style='font-size: 10pt; font-family: arial'>b</div>c"));
		// explicit top margin mixed with other styles:
		assertEquals("a\n\nb\nc",
		      render("a<div style='font-size: 10pt; margin-top: 1em ; font-family: arial'>b</div>c"));
		// shorthand margins:
		assertEquals("a\n\n   b\n\n\nc",
		      render("a<div style=' margin: 1em 0 2em 3em'>b</div>c"));
		assertEquals("a\n\n   b\n\n\nc",
		      render("a<div style=' margin: 1em 3em 2em'>b</div>c"));
		assertEquals("a\n\n   b\n\nc",
		      render("a<div style=' margin: 1em 3em'>b</div>c"));
		assertEquals("a\n\n b\n\nc",
		      render("a<div style=' margin: 1em'>b</div>c"));
		// explicit margins override shorthand margins:
		assertEquals("a\n b\n\nc",
		      render("a<div style='margin-top: 0; margin: 1em'>b</div>c"));
		// set top margin to 0:
		assertEquals("a\nb\n\nc",
		      render("a<p style='margin-top: 0'>b</p>c"));
		// normal blockquote has top and bottom margins of 1 and left margin of 4:
		assertEquals("a\n\n    b\n\nc",
		      render("a<blockquote>b</blockquote>c"));
		// override margins with css:
		assertEquals("a\nb\nc",
		      render("a<blockquote style='margin: 0'>b</blockquote>c"));
		// bottom margin:
		assertEquals("a\nb\n\nc",
		      render("a<div style=' margin-bottom : 1em '>b</div>c"));
		// left margin:
		assertEquals("x\n   a\n   b\ny",
		      render("x<div style='margin-left: 1cm'>a<br>b</div>y"));
		// top padding:
		assertEquals("a\n\nb\nc",
		      render("a<div style='padding-top: 1em'>b</div>c"));
		// top padding and top margin added together:
		assertEquals("a\n\n\nb\nc",
		      render("a<div style='padding-top: 1em; margin-top: 1em'>b</div>c"));
		// percentage margins and other non-explicit lengths ignored
		assertEquals("a\nb\nc",
		      render("a<div style='padding-top: auto; margin-top: 100%'>b</div>c"));
	}

	@Test public void testInvalidCSS() throws Exception {
		assertEquals("a\nb\nc",render("a<div style='margin'>b</div>c"));
		assertEquals("a\nb\nc",render("a<div style='margin-'>b</div>c"));
		assertEquals("a\nb\nc",render("a<div style='margin-bla'>b</div>c"));
		assertEquals("a\nb\nc",render("a<div style='margin-top'>b</div>c"));
		assertEquals("a\nb\nc",render("a<div style='margin-top:'>b</div>c"));
		assertEquals("a\nb\nc",render("a<div style='margin-top:bla'>b</div>c"));
		assertEquals("a\nb\nc",render("a<div style='margin-top:;'>b</div>c"));
	}

	@Test public void testCSSUnits() throws Exception {
		// em (= 1 line/character)
		assertEquals("a\n\nb\nc",
		      render("a<div style='margin-top: 1em'>b</div>c"));
		assertEquals("a\n\n\nb\nc",
		      render("a<div style='margin-top: 2em'>b</div>c"));
		// ex (= 1 line/character)
		assertEquals("a\n\n\nb\nc",
		      render("a<div style='margin-top: 2ex'>b</div>c"));
		// px (= 0.125 line/character)
		assertEquals("a\n\nb\nc",
		      render("a<div style='margin-top: 10px'>b</div>c"));
		assertEquals("a\n\n\n\nb\nc", // 20px = 2.5 lines, round up to 3.
		      render("a<div style='margin-top: 20px'>b</div>c"));
		// in (= 8 lines/characters)
		assertEquals("a\n\n\n\n\nb\nc",
		      render("a<div style='margin-top: 0.5in'>b</div>c"));
		// cm (= 3 lines/characters)
		assertEquals("a\n\n\n\nb\nc",
		      render("a<div style='margin-top: 1cm'>b</div>c"));
		// mm (= 0.3 line/character)
		assertEquals("a\n\n\n\nb\nc",
		      render("a<div style='margin-top: 10mm'>b</div>c"));
		// pt (= 0.1 line/character)
		assertEquals("a\n\n\nb\nc",
		      render("a<div style='margin-top: 20pt'>b</div>c"));
		// pc (= 1.2 line/character)
		assertEquals("a\n\n\nb\nc",
		      render("a<div style='margin-top: 2pc'>b</div>c"));
	}

	@Test public void testBR() throws Exception {
		// <br> between text adds a single new line:
		assertEquals("x\ny",
		      render("x<br>y"));
		// Two <br> elements adds two new lines:
		assertEquals("x\n\ny",
		      render("x<br><br>y"));
		// <br> at start and end of text is rendered, unlike vertical margins:
		assertEquals("\nx\n",
		      render("<br>x<br>"));
		// perform rest of tests in indented block:
		assertEquals("    x\n    y",
		      render("<blockquote>x<br>y</blockquote>"));
		// ignore white space around <br>:
		assertEquals("    x\n    y",
		      render("<blockquote>x <br> y</blockquote>"));
		// <br> after block element adds an extra new line:
		assertEquals("    x\n\n    y",
		      render("<blockquote><div>x</div><br>y</blockquote>"));
		// <br> before block element is ignored :
		assertEquals("    x\n    y",
		      render("<blockquote>x<br><div>y</div></blockquote>"));
		// <br> at start of block element adds an extra new line:
		assertEquals("    x\n\n    y",
		      render("<blockquote>x<div><br>y</div></blockquote>"));
		// <br> before end of block element is ignored:
		assertEquals("    x\n    y\n    z",
		      render("<blockquote>x<div>y<br></div>z</blockquote>"));
		assertEquals("    x\n    y\n\n    z",
		      render("<blockquote>x<div>y<br></div><br>z</blockquote>"));
		// <br> between two block elements adds an extra new line:
		assertEquals("    x\n\n    y",
		      render("<blockquote><div>x</div><br><div>y</div></blockquote>"));
		assertEquals("    x\n\n\n    y",
		      render("<blockquote><p>x</p><br><p>y</p></blockquote>"));
		assertEquals("    x\n\n\n\n    y",
		      render("<blockquote><p>x</p><br><h1>y</h1></blockquote>"));
		// <br> at start of indenting block adds an extra new line:
		assertEquals("    x\n\n\n        y",
		      render("<blockquote>x<blockquote><br>y</blockquote></blockquote>"));
		assertEquals("    x\n        * \n          y",
		      render("<blockquote>x<ul><li><br>y</ul></blockquote>"));
	}

	@Test public void testCustomisedBlockProperties() throws Exception {
		assertEquals("a\n\nb\n\nc",render("a<p>b</p>c"));
		int originalDefaultTopMargin=Renderer.getDefaultTopMargin(HTMLElementName.P);
		int originalDefaultBottomMargin=Renderer.getDefaultBottomMargin(HTMLElementName.P);
		boolean originalDefaultIndent=Renderer.isDefaultIndent(HTMLElementName.P);
		Renderer.setDefaultTopMargin(HTMLElementName.P,0);
		Renderer.setDefaultBottomMargin(HTMLElementName.P,2);
		Renderer.setDefaultIndent(HTMLElementName.P,true);
		assertEquals("a\n    b\n\n\nc",render("a<p>b</p>c"));
		Renderer.setDefaultTopMargin(HTMLElementName.P,originalDefaultTopMargin);
		Renderer.setDefaultBottomMargin(HTMLElementName.P,originalDefaultBottomMargin);
		Renderer.setDefaultIndent(HTMLElementName.P,originalDefaultIndent);
		assertEquals("a\n\nb\n\nc",render("a<p>b</p>c"));
	}

	@Test public void testBlockIndents() throws Exception {
		// definition list indents <dd>:
		assertEquals("a\nb\n    c\nd",
		      render("a<dl><dt>b</dt><dd>c</dd></dl>d"));
		// <blockquote> has an indent as well as a one line vertical margin top and bottom:
		assertEquals("a\n\n    b\n\nc",
		      render("a<blockquote>b</blockquote>c"));
		// <blockquote> as first element:
		assertEquals("    b",
		      render("<blockquote>b</blockquote>"));
		// customize the number of spaces to use in the indent:
		assertEquals("a\n\n        b\n\nc",
		    renderer("a<blockquote>b</blockquote>c").setBlockIndentSize(8).toString());
		// nested indents:
		assertEquals("x\n\n    a\n\n        b\n\n    c\n\ny",
		      render("x<blockquote>a<blockquote>b</blockquote>c</blockquote>y"));
		// nested indents mixing elements:
		assertEquals("x\n\n    a\n    b\n        c\n    d\n\ny",
		      render("x<blockquote>a<dl><dt>b</dt><dd>c</dd></dl>d</blockquote>y"));
	}

	@Test public void testPRE() throws Exception {
		// one line vertical margin:
		assertEquals("x\n\na\n\ny",
		      render("x<pre>a</pre>y"));
		// keep white space:
		assertEquals("x\n\na  b\nc  \n\ny",
		      render("x<pre>a  b\nc  </pre>y"));
		assertEquals("x\n\na  b \n c  \n\ny",
		      render("x<pre>a  b \n c  </pre>y"));
		// still evaluate inline elements inside <pre>:
		assertEquals("x\n\na*b*c\n\ny",
		    renderer("x<pre>a<b>b</b>c</pre>y").setDecorateFontStyles(true).toString());
		// inside indented block:
		assertEquals("    x\n\n    a  b\n    c  \n\n    y",
		      render("<blockquote>x<pre>a  b\nc  </pre>y</blockquote>"));
	}

	@Test public void testLists() throws Exception {
		// basic unordered list:
		assertEquals("x\n    * a\n    * b\n    * c\n    * d\n    * e\n    * f\n    * g\n    * h\n    * i\n    * j\n    * k\n    * l\ny",
		      render("x<ul><li>a<li>b<li>c<li>d<li>e<li>f<li>g<li>h<li>i<li>j<li>k<li>l</ul>y"));
		// list as first element:
		assertEquals("    * a\n    * b\n    * c",
		      render("<ul><li>a<li>b<li>c</ul>"));
		// ignore white space around list elements:
		assertEquals("    * a\n    * b\n    * c",
		      render("<ul><li>a<li> b <li>c</ul>"));
		// basic ordered list:
		assertEquals("x\n   1. a\n   2. b\n   3. c\n   4. d\n   5. e\n   6. f\n   7. g\n   8. h\n   9. i\n  10. j\n  11. k\n  12. l\ny",
		      render("x<ol><li>a<li>b<li>c<li>d<li>e<li>f<li>g<li>h<li>i<li>j<li>k<li>l</ol>y"));
		// customise list bullets:
		assertEquals("x\n    - a\n    - b\n    - c\ny",
		    renderer("x<ul><li>a<li>b<li>c</ul>y").setListBullets(new char[]{'-'}).toString());
		// customise list indent size:
		assertEquals("x\n  * a\n  * b\n  * c\ny",
		    renderer("x<ul><li>a<li>b<li>c</ul>y").setListIndentSize(4).toString());
		// wrap long lines:
		assertEquals("x\n    * a\n    * This is a long sentence that should be broken into multiple lines at\n      word boundaries, with each line no longer than the default 76\n      character maximum line length.\n    * c\ny",
		      render("x<ul><li>a<li>This is a long sentence that should be broken into multiple lines at word boundaries, with each line no longer than the default 76 character maximum line length.<li>c</ul>y"));
		// mixed nested lists:
		assertEquals("x\n    * a\n          o b\n          o c\n                + d\n                + e\n    * f\n    * g\n         1. h\n         2. i\n               1. j\n         3. k\n    * l\ny",
		      render("x<ul><li>a<ul><li>b<li>c<ul><li>d<li>e</ul></ul><li>f<li>g<ol><li>h<li>i<ol><li>j</ol><li>k</ol><li>l</ul>y"));
		// list item containing only a sublist:
		assertEquals("x\n    * a\n    * \n          o b\n    * c\ny",
		      render("x<ul><li>a<li><ul><li>b</ul><li>c</ul>y"));
		// list item containing <div> element:
		assertEquals("x\n    * a\n    * b\n    * c\ny",
		      render("x<ul><li>a<li><div>b</div><li>c</ul>y"));
		// list item containing <p> element (renders the same as IE, with top <p> vertical magin ignored but bottom vertical margin retained):
		assertEquals("x\n    * a\n    * b\n\n    * c\ny",
		      render("x<ul><li>a<li><p>b</p><li>c</ul>y"));
		// list item containing an element that adds to the indent still ignores top vertical margin (renders the same as IE):
		assertEquals("x\n    * a\n    *     b\n          c\n\n    * d\ny",
		      render("x<ul><li>a<li><blockquote><div> b</div><div>c</div></blockquote><li>d</ul>y"));
	}

	@Test public void testTable() throws Exception {
		// basic table with default table cell separator (space followed by tab):
		assertEquals("x\nA \tB \tC\na \tb \tc\ny",
		      render("x<table><tr><th>A<th>B<th>C<tr><td>a<td>b<td>c</table>y"));
		// custom table cell separator:
		assertEquals("x\nA\tB\tC\na\tb\tc\ny",
		    renderer("x<table><tr><th>A<th>B<th>C<tr><td>a<td>b<td>c</table>y").setTableCellSeparator("\t").toString());
	}

	@Test public void testA() throws Exception {
		assertEquals("My Link <http://mysite.com/>",
		      render("<a href=\"http://mysite.com/\">My Link</a>"));
		// line break before URL if it would otherwise be longer than line length:
		assertEquals("My link with a long label that goes close to the end of the line\n<http://mysite.com/>",
		      render("<a href=\"http://mysite.com/\">My link with a long label that goes close to the end of the line</a>"));
		assertEquals("My Link",
		    renderer("<a href=\"http://mysite.com/\">My Link</a>").setIncludeHyperlinkURLs(false).toString());
		Renderer customRenderer=new Renderer(new Source("<a href=\"http://mysite.com/\">My Link</a>")) {
			public String renderHyperlinkURL(StartTag startTag) {
				String href=startTag.getAttributeValue("href");
				if (href==null || href.equals("#") || href.startsWith("javascript:")) return null;
				return href;
			}
		};
		assertEquals("My Link http://mysite.com/",customRenderer.toString());
	}

	@Test public void testAlternateText() throws Exception {
		assertEquals("x [a picture] y",
		      render("x <img src\"picture.png\" alt=\"a picture\" /> y"));
		assertEquals("x[a picture]y",
		      render("x<img src\"picture.png\" alt=\"a picture\" />y"));
		assertEquals("x[ a  picture\n]y",
		      render("x<img src\"picture.png\" alt=\" a  picture\n\" />y"));
		assertEquals("x\n[a picture]\ny",
		      render("<div>x</div><img src\"picture.png\" alt=\"a picture\" /><div>y</div>"));
		assertEquals("An example of a line where the alternate text wraps: [this is alternate \ntext that wraps]",
		      render("An example of a line where the alternate text wraps: <img src\"picture.png\" alt=\"this is alternate text that wraps\" />"));
		assertEquals("x y",
		    renderer("x <img src\"picture.png\" alt=\"a picture\" /> y").setIncludeAlternateText(false).toString());
		Renderer customRenderer=new Renderer(new Source("x <img src\"picture.png\" alt=\"a picture\" /> y")) {
			public String renderAlternateText(StartTag startTag) {
				if (startTag.getName()==HTMLElementName.AREA) return null;
				String alt=startTag.getAttributeValue("alt");
				if (alt==null || alt.length()==0) return null;
				return '\u00ab'+alt+'\u00bb';
			}
		};
		assertEquals("x \u00aba picture\u00bb y",customRenderer.toString());
	}

	@Test public void testHR() throws Exception {
		// dashes extend to 4 characters before the end of the line:
		assertEquals("x\n------------------------------------------------------------------------\ny",
		      render("x<hr>y"));
		// take indent into account:
		assertEquals("x\n    * ------------------------------------------------------------------\n\n          --------------------------------------------------------------\n\ny",
		      render("x<ul><li><hr><blockquote><hr></blockquote></ul>y"));
	}
	
	@Test public void testFontStyles() throws Exception {
		// default behaviour is not to decorate words:
		assertEquals("a bold word",
		      render("a <b>bold</b> word"));
		// decorate <b> and <strong> text with *:
		assertEquals("a *bold* and *strong* word",
		    renderer("a <b>bold</b> and <strong>strong</strong> word").setDecorateFontStyles(true).toString());
		// decorate <i> and <em> with /:
		assertEquals("/italic/ and /emphasised/ text",
		    renderer("<i>italic</i> and <em>emphasised</em> text").setDecorateFontStyles(true).toString());
		// decorate <u> with _:
		assertEquals("_underlined_ text",
		    renderer("<u>underlined</u> text").setDecorateFontStyles(true).toString());
		// decorate <code> with |:
		assertEquals("|code| text",
		    renderer("<code>code</code> text").setDecorateFontStyles(true).toString());
	}

	private String render(String sourceText) {
		return renderer(sourceText).toString();
	}

	private Renderer renderer(String sourceText) {
		return new Renderer(new Source(sourceText)).setNewLine("\n");
	}
}
