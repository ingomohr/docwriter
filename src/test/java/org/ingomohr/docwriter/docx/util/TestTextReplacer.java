package org.ingomohr.docwriter.docx.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.utils.SingleTraversalUtilVisitorCallback;
import org.docx4j.utils.TraversalUtilVisitor;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.ingomohr.docwriter.docx.SimpleDocxProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTextReplacer {

	private TextReplacer objUT;

	@BeforeEach
	void prep() {
		objUT = new TextReplacer();
	}

	@Test
	void replace_CalledForWorkPackage_ReplaceWasCalledForMainDocumentAndCorrectParameters() {

		final AtomicReference<String> actualTextToReplace = new AtomicReference<String>();
		final AtomicReference<String> actualReplacment = new AtomicReference<String>();
		final AtomicReference<Object> actualPart = new AtomicReference<Object>();

		objUT = new TextReplacer() {

			@Override
			public void replace(Object model, String textToReplace, String replacement) {
				actualPart.set(model);
				actualTextToReplace.set(textToReplace);
				actualReplacment.set(replacement);
			}

		};

		WordprocessingMLPackage pack = mock(WordprocessingMLPackage.class);
		MainDocumentPart part = mock(MainDocumentPart.class);
		when(pack.getMainDocumentPart()).thenReturn(part);

		objUT.replace(pack, "a", "b");

		assertEquals("a", actualTextToReplace.get());
		assertEquals("b", actualReplacment.get());
		assertSame(part, actualPart.get());
	}

	@Test
	void replace_CalledForWorkPackage_DocContainsMatchWithinOneTextElement_MatchWasReplaced() {

		WordprocessingMLPackage document = mkSampleDoc();

		objUT = new TextReplacer();
		objUT.replace(document, "is", "are");

		assertEquals("Headline 1Hello, there are something", getTextContentFromMainPart(document));
	}

	@Test
	void replace_CalledForWorkPackage_DocContainsMatchOverMultipleSiblingTextElements_MatchWasReplaced() {

		WordprocessingMLPackage document = mkSampleDoc();

		objUT = new TextReplacer();
		objUT.replace(document, "something", "XX");

		assertEquals("Headline 1Hello, there is XX", getTextContentFromMainPart(document));
	}

	@Test
	void replace_CalledForWorkPackage_DocContainsMultiMatches_MatchesWereReplaced() {

		WordprocessingMLPackage document = mkSampleDoc();

		objUT = new TextReplacer();
		objUT.replace(document, "e", "a");

		assertEquals("Haadlina 1Hallo, thara is somathing", getTextContentFromMainPart(document));
	}

	private WordprocessingMLPackage mkSampleDoc() {

		/*
		 * Sample Doc:
		 * 
		 * Headline 1 // headline 1
		 * 
		 * <Hello, there><is>< some>< text> // Text elements. that last two of which
		 * belong to the same parent element
		 */

		SimpleDocxProcessor processor = new SimpleDocxProcessor();
		processor.createDocument();
		processor.addHeadlineH1("Headline 1");
		processor.addMarkdown("Hello, there **is** some");

		WordprocessingMLPackage document = appendTextElementAfterLastTextElementNamedSome(processor);

		return document;
	}

	private WordprocessingMLPackage appendTextElementAfterLastTextElementNamedSome(SimpleDocxProcessor processor) {
		final AtomicReference<Text> textSome = new AtomicReference<Text>();
		final AtomicReference<Object> textParent = new AtomicReference<Object>();

		SingleTraversalUtilVisitorCallback callBack = new SingleTraversalUtilVisitorCallback(
				new TraversalUtilVisitor<Text>() {

					@Override
					public void apply(Text element, Object parent, List<Object> siblings) {
						if (" some".equals(element.getValue())) {
							textParent.set(parent);
							textSome.set(element);
						}
					}
				});
		WordprocessingMLPackage document = processor.getDocument();
		callBack.walkJAXBElements(document.getMainDocumentPart());

		R r = (R) textParent.get();
		Text newText = new Text();
		newText.setValue("thing");
		r.getContent().add(newText);
		newText.setParent(r);
		return document;
	}

	private String getTextContentFromMainPart(WordprocessingMLPackage doc) {
		List<Text> texts = new DocxDataInspector().getAllElements(doc.getMainDocumentPart(), Text.class);
		String collected = texts.stream().map(t -> t.getValue()).collect(Collectors.joining());
		return collected;
	}

}
