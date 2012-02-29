package net.htmlparser.jericho;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class SegmentGetStyleURISegmentsTest {
	private static final String sourceUrlString="file:test/data/SegmentTest.html";

	@Test public void test() throws Exception {
		Source source=new Source(new URL(sourceUrlString));
		List<Segment> styleURISegments=source.getStyleURISegments();
		assertEquals(3,styleURISegments.size());
		assertEquals("images/background.png",styleURISegments.get(0).toString());
		assertEquals("outerdiv.htc",styleURISegments.get(1).toString());
		assertEquals("images/background-image.png",styleURISegments.get(2).toString());
		
		Element outerDivElement=source.getElementById("OuterDiv");
		Segment styleAttributeValueSegment=outerDivElement.getAttributes().get("style").getValueSegment();
		styleURISegments=styleAttributeValueSegment.getStyleURISegments();
		assertEquals(2,styleURISegments.size());
		assertEquals("images/background.png",styleURISegments.get(0).toString());
		assertEquals("outerdiv.htc",styleURISegments.get(1).toString());
	}
}
