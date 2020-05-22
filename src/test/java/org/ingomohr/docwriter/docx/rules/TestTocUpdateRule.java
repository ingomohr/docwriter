package org.ingomohr.docwriter.docx.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.toc.TocException;
import org.docx4j.wml.SdtBlock;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.ingomohr.docwriter.docx.util.DocxDataInspector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vladsch.flexmark.docx.converter.DocxRenderer;

class TestTocUpdateRule {

	private TocUpdateRule objUT;

	@BeforeEach
	void prep() {
		objUT = new TocUpdateRule();
	}

	@Test
	void appliesTo() {
		assertEquals(false, objUT.appliesTo(null));
		assertEquals(false, objUT.appliesTo("wrong-type"));
		assertEquals(true, objUT.appliesTo(mock(WordprocessingMLPackage.class)));
	}

	@Test
	void apply_Placeholder_HappyDay() {
		final WordprocessingMLPackage doc = DocxRenderer.getDefaultTemplate();

		new TocInsertionRule().apply(doc);
		new MarkdownAppenderRule(() -> "# H1").apply(doc);
		new MarkdownAppenderRule(() -> "## H2").apply(doc);
		new MarkdownAppenderRule(() -> "### H3").apply(doc);

		List<Object> contents = new DocxDataInspector().getContents(doc);
		final int size1 = contents.size();
		final SdtBlock toc1 = (SdtBlock) contents.get(0);

		// execute
		new TocUpdateRule().apply(doc);

		// verify
		contents = new DocxDataInspector().getContents(doc);
		final int size2 = contents.size();
		final SdtBlock toc2 = (SdtBlock) contents.get(0);

		assertEquals(size1, size2);
		assertSame(toc1, toc2);

	}

	@Test
	void apply_Placeholder_NotFound() {
		final WordprocessingMLPackage doc = DocxRenderer.getDefaultTemplate();

		new MarkdownAppenderRule(() -> "# H1").apply(doc);
		new MarkdownAppenderRule(() -> "## H2").apply(doc);
		new MarkdownAppenderRule(() -> "### H3").apply(doc);

		final RuntimeException ex = assertThrows(RuntimeException.class, () -> new TocUpdateRule().apply(doc));
		final TocException cause = (TocException) ex.getCause();
		MatcherAssert.assertThat(cause.getMessage(), CoreMatchers.containsString("No ToC content control found"));
	}

}
