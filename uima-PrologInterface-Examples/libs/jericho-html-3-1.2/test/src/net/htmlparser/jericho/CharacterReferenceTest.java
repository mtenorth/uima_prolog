package net.htmlparser.jericho;

import org.junit.Test;
import static org.junit.Assert.*;

public class CharacterReferenceTest {

	@Test public void testDecode() {
		assertEquals("b&b",CharacterReference.decode("b&amp;b")); // decode character entity reference with codepoint <= U+00FF
		assertEquals("b&b",CharacterReference.decode("b&#38;b")); // decode decimal numeric character reference with codepoint <= U+00FF
		assertEquals("b&b",CharacterReference.decode("b&#x26;b")); // decode hexadecimal numeric character reference with codepoint <= U+00FF
		assertEquals("x\u20acx",CharacterReference.decode("x&euro;x")); // decode character entity reference with codepoint > U+00FF
		assertEquals("x\u20acx",CharacterReference.decode("x&#8364;x")); // decode decimal numeric character reference with codepoint > U+00FF
		assertEquals("x\u20acx",CharacterReference.decode("x&#x20ac;x")); // decode hexadecimal numeric character reference with codepoint > U+00FF
	}
	
	@Test public void testDecodeUnterminated() {

		assertEquals("b&ampb",CharacterReference.decode("b&ampb")); // DO NOT decode unterminated character entity reference followed by an alphabetic character
		assertEquals("b& b",CharacterReference.decode("b&amp b")); // decode unterminated character entity reference followed by an NON-alphabetic character
		assertEquals("b&b",CharacterReference.decode("b&#38b")); // decode unterminated decimal numeric character reference followed by an alphabetic character
		assertEquals("b&x",CharacterReference.decode("b&#x26x",true)); // decode hexadecimal numeric character reference followed by an alphabetic character (only if insideAttributeValue=true with default configuration)

		// DEFAULT CONFIGURATION (Config.CurrentCompatibilityMode=Config.CompatibilityMode.IE):

		assertEquals("x& x",CharacterReference.decode("x&amp x",false)); // decode unterminated character entity reference with codepoint <= U+00FF with insideAttributeValue=false
		assertEquals("x& x",CharacterReference.decode("x&#38 x",false)); // decode unterminated decimal numeric character reference with codepoint <= U+00FF with insideAttributeValue=false
		assertEquals("x&#x26 x",CharacterReference.decode("x&#x26 x",false)); // DO NOT decode unterminated hexadecimal numeric character reference with codepoint <= U+00FF with insideAttributeValue=false

		assertEquals("x&euro x",CharacterReference.decode("x&euro x",false)); // DO NOT decode unterminated character entity reference with codepoint > U+00FF with insideAttributeValue=false
		assertEquals("x\u20ac x",CharacterReference.decode("x&#8364 x",false)); // decode unterminated decimal numeric character reference with codepoint > U+00FF with insideAttributeValue=false
		assertEquals("x&#x20ac x",CharacterReference.decode("x&#x20ac x",false)); // DO NOT decode unterminated hexadecimal numeric character reference with codepoint > U+00FF with insideAttributeValue=false

		assertEquals("x& x",CharacterReference.decode("x&amp x",true)); // decode unterminated character entity reference with codepoint <= U+00FF with insideAttributeValue=false
		assertEquals("x& x",CharacterReference.decode("x&#38 x",true)); // decode unterminated decimal numeric character reference with codepoint <= U+00FF with insideAttributeValue=false
		assertEquals("x& x",CharacterReference.decode("x&#x26 x",true)); // decode unterminated hexadecimal numeric character reference with codepoint <= U+00FF with insideAttributeValue=false

		assertEquals("x&euro x",CharacterReference.decode("x&euro x",true)); // DO NOT decode unterminated character entity reference with codepoint > U+00FF with insideAttributeValue=false
		assertEquals("x\u20ac x",CharacterReference.decode("x&#8364 x",true)); // decode unterminated decimal numeric character reference with codepoint > U+00FF with insideAttributeValue=false
		assertEquals("x\u20ac x",CharacterReference.decode("x&#x20ac x",true)); // decode unterminated hexadecimal numeric character reference with codepoint > U+00FF with insideAttributeValue=false
		
	}

	@Test public void testDecodeAttribute() {
		// demonstrates rules for decoding inside attribute value with default configuration (Config.CompatibilityMode.IE):
		// - unterminated &euro is not decoded as it has codepoint >= U+00FF
		// - unterminated &lt is not decoded as it is followed by an alphabetic character
		// - unterminated &gt is decoded as it is has codepoint < U+00FF and is not followed by an alphabetic character
		Source source=new Source("<a href=\"test?a=1&amp;b=2&c=3&euro=4&d=&ltx&gt&e=5\">test</a>");
		StartTag startTag=source.getFirstStartTag(HTMLElementName.A);
		String href=startTag.getAttributeValue("href");
		assertEquals("test?a=1&b=2&c=3&euro=4&d=&ltx>&e=5",href);
	}
	
}
