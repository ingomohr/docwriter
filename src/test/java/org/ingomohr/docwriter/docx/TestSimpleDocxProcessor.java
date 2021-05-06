package org.ingomohr.docwriter.docx;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.toc.TocFinder;
import org.docx4j.wml.Br;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.SdtBlock;
import org.docx4j.wml.Text;
import org.hamcrest.CoreMatchers;
import org.ingomohr.docwriter.docx.rules.MarkdownAppenderRule;
import org.ingomohr.docwriter.docx.util.DocxDataInspector;
import org.ingomohr.docwriter.docx.util.TextReplacer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import com.vladsch.flexmark.docx.converter.DocxRenderer;

class TestSimpleDocxProcessor {

	private SimpleDocxProcessor objUT;

	@BeforeEach
	void prep() {
		objUT = new SimpleDocxProcessor();
	}

	@Test
	void getDocument_NoDocumentSetOrCreated_ReturnsNull() {
		assertNull(objUT.getDocument());
	}

	@Test
	void getDocument_DocumentSet_ReturnsSetDocument() {
		WordprocessingMLPackage doc = mock(WordprocessingMLPackage.class);
		objUT.setDocument(doc);
		assertSame(doc, objUT.getDocument());
	}

	@Test
	void createDocument_DefaultDocumentWasCreated() {
		WordprocessingMLPackage doc = mock(WordprocessingMLPackage.class);

		try (MockedStatic<DocxRenderer> renderer = mockStatic(DocxRenderer.class)) {
			renderer.when(() -> DocxRenderer.getDefaultTemplate()).thenReturn(doc);

			objUT.createDocument();

			assertSame(doc, objUT.getDocument());

			renderer.verify(() -> DocxRenderer.getDefaultTemplate());
			renderer.verifyNoMoreInteractions();
		}
	}

	@Test
	void loadDocument_ByPath_LoadDocumentByFileWasCalled() throws Exception {

		File file = mock(File.class);
		Path path = mock(Path.class);
		when(path.toFile()).thenReturn(file);

		final AtomicReference<File> passedFile = new AtomicReference<File>();

		objUT = new SimpleDocxProcessor() {

			@Override
			public void loadDocument(File file) throws IOException {
				passedFile.set(file);
			}

		};

		objUT.loadDocument(path);

		assertSame(file, passedFile.get());
	}

	@Test
	void loadDocument_ByFileButFileDoesntContainAWordDoc_ExceptionIsThrown() throws Exception {

		Path path = Paths.get(System.getProperty("java.io.tmpdir") + "/sample.docx");
		File file = path.toFile();
		file.createNewFile();
		file.deleteOnExit();

		IOException ex = assertThrows(IOException.class, () -> objUT.loadDocument(file));
		assertThat(ex.getMessage(), CoreMatchers.containsString("Error reading"));
	}

	@Test
	void loadDocument_ByFileHappyDay_DocIsAccessible() throws Exception {

		Path path = Paths.get(System.getProperty("java.io.tmpdir") + "/sample.docx");
		File file = path.toFile();
		DocxRenderer.getDefaultTemplate().save(file);

		file.deleteOnExit();

		objUT.loadDocument(file);

		assertNotNull(objUT.getDocument());
	}

	@Test
	void addToc_TocIsAccessible() {
		objUT.createDocument();

		assertNull(getTocFromObjUtDocument());

		objUT.addToc();

		assertNotNull(getTocFromObjUtDocument());
	}

	@Test
	void updateToc_NoTocWasCreatedBefore_StillHasNoToc() {

		/*
		 * This behaviour was specified this way to keep consistency with the
		 * TocUpdateRule.
		 */

		objUT.createDocument();
		objUT.updateToc();

		assertNull(getTocFromObjUtDocument());
	}

	@Test
	void replaceText_TextReplacerWasCalled() {

		TextReplacer replacer = mock(TextReplacer.class);
		WordprocessingMLPackage doc = mock(WordprocessingMLPackage.class);

		objUT = new SimpleDocxProcessor() {

			@Override
			protected TextReplacer createTextReplacer() {
				return replacer;
			}
		};
		objUT.setDocument(doc);

		objUT.replaceText("a", "b");

		verify(replacer).replace(doc, "a", "b");
		verifyNoMoreInteractions(replacer);
	}

	@Test
	void replaceText_TextWasReplaced() {
		objUT.createDocument();

		objUT.addHeadlineH1("Dear $(name)!");
		objUT.addMarkdown("My dear $(name), $(name, this is a nice day, isn't it?");

		objUT.replaceText("$(name)", "Sir John");

		assertContainsTextElementInMainPart(objUT, "Dear Sir John!");
		assertContainsTextElementInMainPart(objUT, "My dear Sir John, $(name, this is a nice day, isn't it?");
	}

	@Test
	void addMarkDown_MarkDownAppenderRuleWasCalled() {

		MarkdownAppenderRule rule = mock(MarkdownAppenderRule.class);

		objUT = new SimpleDocxProcessor() {

			@Override
			protected MarkdownAppenderRule createMarkdownAppenderRule() {
				return rule;
			}
		};

		objUT.createDocument();

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Supplier<String>> captor = ArgumentCaptor.forClass(Supplier.class);

		objUT.addMarkdown("HelloX");

		verify(rule).setValueSupplier(captor.capture());

		assertEquals("HelloX", captor.getValue().get());
	}

	@Test
	void addMarkDown_MarkDownWasAddedToDocument() {
		objUT.createDocument();
		objUT.addMarkdown("Hey Joe");

		assertContainsTextElementInMainPart(objUT, "Hey Joe");
	}

	@Test
	void addHeadline_HeadlineWasAdded() {
		final AtomicReference<String> addedMarkdown = new AtomicReference<String>();

		objUT = new SimpleDocxProcessor() {

			@Override
			public void addMarkdown(String markDownContent) {
				addedMarkdown.set(markDownContent);
			}

		};

		objUT.addHeadline("#", "Hello");
		assertEquals("# Hello", addedMarkdown.get());

		objUT.addHeadlineH1("X");
		assertEquals("# X", addedMarkdown.get());

		objUT.addHeadlineH2("X");
		assertEquals("## X", addedMarkdown.get());

		objUT.addHeadlineH3("X");
		assertEquals("### X", addedMarkdown.get());

		objUT.addHeadlineH4("X");
		assertEquals("#### X", addedMarkdown.get());

		objUT.addHeadlineH5("X");
		assertEquals("##### X", addedMarkdown.get());

		objUT.addHeadlineH6("X");
		assertEquals("###### X", addedMarkdown.get());
	}

	@Test
	void addPageBreak_PageBreakWasAdded() {
		objUT.createDocument();

		objUT.addPageBreak();

		List<Br> brs = new DocxDataInspector().getAllElements(objUT.getDocument().getMainDocumentPart(), Br.class);
		assertEquals(1, brs.stream().filter(br -> STBrType.PAGE == br.getType()).count());
	}

	@Test
	void assertedGetDocument_DocumentIsAccessible_ReturnsDocument() {
		objUT.createDocument();

		assertNotNull(objUT.assertedGetDocument());
	}

	@Test
	void assertedGetDocument_DocumentIsNotAccessible_ThrowsException() {

		NullPointerException ex = assertThrows(NullPointerException.class, () -> objUT.assertedGetDocument());
		assertThat(ex.getMessage(),
				CoreMatchers.containsString("No document found. See createDocument() and loadDocument()"));
	}

	@Test
	void saveDocument_ToPath_DocumentWasSaved() throws Exception {
		objUT.createDocument();
		objUT.addMarkdown("Hey Joe by path");

		Path path = Paths.get(System.getProperty("java.io.tmpdir") + "/saveDocumentByPath.docx");
		path.toFile().deleteOnExit();

		objUT.saveDocumentToPath(path);

		SimpleDocxProcessor newProcessor = new SimpleDocxProcessor();
		newProcessor.loadDocument(path);

		assertContainsTextElementInMainPart(newProcessor, "Hey Joe by path");
	}

	@Test
	void saveDocument_ToFile_DocumentWasSaved() throws Exception {
		objUT.createDocument();
		objUT.addMarkdown("Hey Joe by file");

		Path path = Paths.get(System.getProperty("java.io.tmpdir") + "/saveDocumentByFile.docx");
		File file = path.toFile();
		file.deleteOnExit();

		objUT.saveDocumentToPath(file);

		SimpleDocxProcessor newProcessor = new SimpleDocxProcessor();
		newProcessor.loadDocument(file);

		assertContainsTextElementInMainPart(newProcessor, "Hey Joe by file");
	}

	private SdtBlock getTocFromObjUtDocument() {
		TocFinder tocFinder = new TocFinder();
		tocFinder.walkJAXBElements(objUT.getDocument().getMainDocumentPart());
		return tocFinder.getTocSDT();
	}

	private void assertContainsTextElementInMainPart(SimpleDocxProcessor processor, String text) {
		requireNonNull(text);

		List<Text> texts = new DocxDataInspector().getAllElements(processor.getDocument().getMainDocumentPart(),
				Text.class);
		assertEquals(1, texts.stream().filter(txt -> text.equals(txt.getValue())).count());
	}

}
