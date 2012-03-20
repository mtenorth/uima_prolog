package net.htmlparser.jericho;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class MicrosoftConditionalCommentTagTypesTest {
	// See http://en.wikipedia.org/wiki/Conditional_comment for an explanation of each construct.

	// Content of downlevel-hidden conditional comment is only recognised by IE as other browsers recognise it as a valid HTML comment
	@Test public void testDownlevelHidden() throws Exception {
		String sourceText="<!--[if IE]><p>content</p><![endif]-->";

		MicrosoftConditionalCommentTagTypes.register();
		Source source=new Source(sourceText);
		List<Tag> tags=source.getAllTags();
		assertEquals(4,tags.size());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_HIDDEN_IF,tags.get(0).getTagType());
		assertSame(StartTagType.NORMAL,tags.get(1).getTagType());
		assertSame(EndTagType.NORMAL,tags.get(2).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_HIDDEN_ENDIF,tags.get(3).getTagType());

		MicrosoftConditionalCommentTagTypes.deregister();
		source=new Source(sourceText);
		tags=source.getAllTags();
		assertEquals(1,tags.size());
		assertSame(StartTagType.COMMENT,tags.get(0).getTagType());
	}

	// Content of non-validating downlevel-revealed conditional comment is recognised by non-IE browsers as they ignore the invalid tags surrounding it.
	@Test public void testNonValidatingDownlevelRevealed() throws Exception {
		String sourceText="<![if !IE]><p>content</p><![endif]>";

		MicrosoftConditionalCommentTagTypes.register();
		Source source=new Source(sourceText);
		List<Tag> tags=source.getAllTags();
		assertEquals(4,tags.size());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_REVEALED_IF,tags.get(0).getTagType());
		assertSame(StartTagType.NORMAL,tags.get(1).getTagType());
		assertSame(EndTagType.NORMAL,tags.get(2).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_REVEALED_ENDIF,tags.get(3).getTagType());

		MicrosoftConditionalCommentTagTypes.deregister();
		source=new Source(sourceText);
		tags=source.getAllTags();
		assertEquals(2,tags.size());
		assertSame(StartTagType.UNREGISTERED,source.getTagAt(0).getTagType());
		assertSame(StartTagType.NORMAL,tags.get(0).getTagType());
		assertSame(EndTagType.NORMAL,tags.get(1).getTagType());
	}

	// Content of validating downlevel-revealed conditional comment is recognised by non-IE browsers as the conditional comment tags are enclosed in valid HTML comments.
	// Note that the syntactical constructs used are actually downlevel-hidden conditional comments.
	@Test public void testValidatingDownlevelRevealed() throws Exception {
		String sourceText="<!--[if !(IE 5)]><!--><p>content</p><!--<![endif]-->";

		MicrosoftConditionalCommentTagTypes.register();
		Source source=new Source(sourceText);
		List<Tag> tags=source.getAllTags();
		assertEquals(4,tags.size());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_REVEALED_VALIDATING_IF,tags.get(0).getTagType());
		assertSame(StartTagType.NORMAL,tags.get(1).getTagType());
		assertSame(EndTagType.NORMAL,tags.get(2).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_REVEALED_VALIDATING_ENDIF,tags.get(3).getTagType());

		MicrosoftConditionalCommentTagTypes.deregister();
		source=new Source(sourceText);
		tags=source.getAllTags();
		assertEquals(4,tags.size());
		assertSame(StartTagType.COMMENT,tags.get(0).getTagType());
		assertSame(StartTagType.NORMAL,tags.get(1).getTagType());
		assertSame(EndTagType.NORMAL,tags.get(2).getTagType());
		assertSame(StartTagType.COMMENT,tags.get(3).getTagType());

		// Make sure a downlevel hidden conditional comment followed by a validating downlevel-revealed conditional comment is parsed correctly,
		// noting that they both have the same start delimiter.
		MicrosoftConditionalCommentTagTypes.register();
		source=new Source("<!--[if IE]>content1<![endif]-->...<!--[if !(IE 5)]><!-->content2<!--<![endif]-->");
		tags=source.getAllTags();
		assertEquals(4,tags.size());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_HIDDEN_IF,tags.get(0).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_HIDDEN_ENDIF,tags.get(1).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_REVEALED_VALIDATING_IF,tags.get(2).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_REVEALED_VALIDATING_ENDIF,tags.get(3).getTagType());
		MicrosoftConditionalCommentTagTypes.deregister();
	}

	// The validating downlevel-revealed simplified conditional comment can be used if the condition always evaluates to false in IE.
	@Test public void testValidatingDownlevelRevealedSimplified() throws Exception {
		String sourceText="<!--[if !IE]>--><p>content</p><!--<![endif]-->";

		MicrosoftConditionalCommentTagTypes.register();
		Source source=new Source(sourceText);
		List<Tag> tags=source.getAllTags();
		assertEquals(4,tags.size());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_REVEALED_VALIDATING_SIMPLIFIED_IF,tags.get(0).getTagType());
		assertSame(StartTagType.NORMAL,tags.get(1).getTagType());
		assertSame(EndTagType.NORMAL,tags.get(2).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_REVEALED_VALIDATING_ENDIF,tags.get(3).getTagType());

		MicrosoftConditionalCommentTagTypes.deregister();
		source=new Source(sourceText);
		tags=source.getAllTags();
		assertEquals(4,tags.size());
		assertSame(StartTagType.COMMENT,tags.get(0).getTagType());
		assertSame(StartTagType.NORMAL,tags.get(1).getTagType());
		assertSame(EndTagType.NORMAL,tags.get(2).getTagType());
		assertSame(StartTagType.COMMENT,tags.get(3).getTagType());

		// Make sure a downlevel hidden conditional comment followed by a validating downlevel-revealed simplified conditional comment is parsed correctly,
		// noting that they both have the same start delimiter.
		MicrosoftConditionalCommentTagTypes.register();
		source=new Source("<!--[if IE]>content1<![endif]-->...<!--[if !IE]>-->content2<!--<![endif]-->");
		tags=source.getAllTags();
		assertEquals(4,tags.size());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_HIDDEN_IF,tags.get(0).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_HIDDEN_ENDIF,tags.get(1).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_REVEALED_VALIDATING_SIMPLIFIED_IF,tags.get(2).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_REVEALED_VALIDATING_ENDIF,tags.get(3).getTagType());
		source=new Source("<!--[if IE]><p>This demonstrates <![if IE 7]>the use of<![endif]> <b>nested</b> downlevel-hidden conditional comments.</p><![endif]--> <!--[if !(IE 5)]><!--><p>This demonstrates <!--[if true]><!-->the use of<!--<![endif]--> <b>nested</b> downlevel-revealed conditional comments.</p><!--<![endif]-->");
		tags=source.getAllTags();
		assertEquals(16,tags.size());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_HIDDEN_IF,tags.get(0).getTagType());
		assertSame(StartTagType.NORMAL,tags.get(1).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_REVEALED_IF,tags.get(2).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_REVEALED_ENDIF,tags.get(3).getTagType());
		assertSame(StartTagType.NORMAL,tags.get(4).getTagType());
		assertSame(EndTagType.NORMAL,tags.get(5).getTagType());
		assertSame(EndTagType.NORMAL,tags.get(6).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_HIDDEN_ENDIF,tags.get(7).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_REVEALED_VALIDATING_IF,tags.get(8).getTagType());
		assertSame(StartTagType.NORMAL,tags.get(9).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_REVEALED_VALIDATING_IF,tags.get(10).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_REVEALED_VALIDATING_ENDIF,tags.get(11).getTagType());
		assertSame(StartTagType.NORMAL,tags.get(12).getTagType());
		assertSame(EndTagType.NORMAL,tags.get(13).getTagType());
		assertSame(EndTagType.NORMAL,tags.get(14).getTagType());
		assertSame(MicrosoftConditionalCommentTagTypes.DOWNLEVEL_REVEALED_VALIDATING_ENDIF,tags.get(15).getTagType());
		MicrosoftConditionalCommentTagTypes.deregister();
	}

}
