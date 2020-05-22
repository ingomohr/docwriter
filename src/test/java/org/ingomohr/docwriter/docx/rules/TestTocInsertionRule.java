package org.ingomohr.docwriter.docx.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.P;
import org.docx4j.wml.SdtBlock;
import org.ingomohr.docwriter.docx.util.DocxDataInspector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vladsch.flexmark.docx.converter.DocxRenderer;

class TestTocInsertionRule {

	private TocInsertionRule objUT;

	@BeforeEach
	void prep() {
		objUT = new TocInsertionRule();
	}

	@Test
	void defaultConstructor() {
		assertEquals(null, objUT.getTocPlaceholder());
	}

	@Test
	void constructor() {
		TocInsertionRule objUT = new TocInsertionRule("$(toc1)");
		assertEquals("$(toc1)", objUT.getTocPlaceholder());
	}

	@Test
	void settings() {
		assertEquals("Table of Contents", objUT.getHeadingText());
		assertEquals("TOC \\o \"1-3\" \\n 1-3 \\h \\z \\u", objUT.getTocSwitches());
		assertEquals(true, objUT.isSkippingPageNumbering());
	}

	@Test
	void appliesTo() {
		assertEquals(false, objUT.appliesTo(null));
		assertEquals(false, objUT.appliesTo("wrong-type"));
		assertEquals(true, objUT.appliesTo(mock(WordprocessingMLPackage.class)));
	}

	@Test
	void apply_NoPlaceholder() {

		final WordprocessingMLPackage doc = DocxRenderer.getDefaultTemplate();

		List<Object> contents = new DocxDataInspector().getContents(doc);
		int size1 = contents.size();

		objUT.apply(doc);

		contents = new DocxDataInspector().getContents(doc);
		assertEquals(size1 + 1, contents.size());
		assertTrue(contents.get(size1) instanceof SdtBlock);
	}

	@Test
	void apply_Placeholder_HappyDay() {
		final WordprocessingMLPackage doc = DocxRenderer.getDefaultTemplate();

		new MarkdownAppenderRule(() -> "$(toc)").apply(doc);
		new MarkdownAppenderRule(() -> "# H1").apply(doc);
		new MarkdownAppenderRule(() -> "## H2").apply(doc);
		new MarkdownAppenderRule(() -> "### H3").apply(doc);

		List<Object> contents = new DocxDataInspector().getContents(doc);
		int size1 = contents.size();
		assertTrue(contents.get(0) instanceof P);

		objUT = new TocInsertionRule("$(toc)");
		objUT.apply(doc);

		contents = new DocxDataInspector().getContents(doc);
		assertEquals(size1, contents.size());
		assertTrue(contents.get(0) instanceof SdtBlock);
	}

	@Test
	void apply_Placeholder_NotFound() {
		final WordprocessingMLPackage doc = DocxRenderer.getDefaultTemplate();

		new MarkdownAppenderRule(() -> "$(toc)").apply(doc);
		new MarkdownAppenderRule(() -> "# H1").apply(doc);
		new MarkdownAppenderRule(() -> "## H2").apply(doc);
		new MarkdownAppenderRule(() -> "### H3").apply(doc);

		List<Object> contents = new DocxDataInspector().getContents(doc);
		int size1 = contents.size();
		assertTrue(contents.get(0) instanceof P);

		objUT = new TocInsertionRule("$(unknown)");
		objUT.apply(doc);

		contents = new DocxDataInspector().getContents(doc);
		assertEquals(size1 + 1, contents.size());
		assertTrue(contents.get(0) instanceof P);
		assertTrue(contents.get(size1) instanceof SdtBlock);
	}

}
