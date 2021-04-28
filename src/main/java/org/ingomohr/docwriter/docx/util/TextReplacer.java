package org.ingomohr.docwriter.docx.util;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.utils.SingleTraversalUtilVisitorCallback;
import org.docx4j.utils.TraversalUtilVisitor;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Text;

/**
 * Replaces all occurrences of the given text in a given DOX4J document node.
 * 
 * @author Ingo Mohr
 * @since 2.1
 */
public class TextReplacer {

	private final List<TokenRecording> recordings;

	public TextReplacer() {
		recordings = new ArrayList<TextReplacer.TokenRecording>();
	}

	/**
	 * Replaces all occurrences of the given text with the given replacement.
	 * 
	 * @param model         the model to modify. Cannot be <code>null</code>.
	 * @param textToReplace the text to replace. Cannot be <code>null</code>.
	 * @param replacement   the replacement to replace with. Cannot be
	 *                      <code>null</code>.
	 */
	public void replace(WordprocessingMLPackage model, String textToReplace, String replacement) {
		replace(model.getMainDocumentPart(), textToReplace, replacement);
	}

	/**
	 * Replaces all occurrences of the given text in the given model with the given
	 * replacement.
	 * <p>
	 * The model can be a {@link MainDocumentPart} or any other DOX4J type that
	 * supports traversing.
	 * </p>
	 * 
	 * @param model         the model to modify. Cannot be <code>null</code>.
	 * @param textToReplace the text to replace. Cannot be <code>null</code>.
	 * @param replacement   the replacement to replace with. Cannot be
	 *                      <code>null</code>.
	 */
	public void replace(Object model, String textToReplace, String replacement) {

		requireNonNull(model);
		requireNonNull(textToReplace);
		requireNonNull(replacement);

		synchronized (recordings) {

			SingleTraversalUtilVisitorCallback callBack = new SingleTraversalUtilVisitorCallback(
					new TraversalUtilVisitor<Text>() {

						@Override
						public void apply(Text element, Object parent, List<Object> siblings) {
							recordToken(element, parent, siblings);
						}
					});
			callBack.walkJAXBElements(model);

			if (recordings != null) {
				recordings.forEach(r -> r.process(textToReplace, replacement));
			}
			
			recordings.clear();
		}
	}

	private void recordToken(Text element, Object parent, List<Object> siblings) {

		/*
		 * The siblings list computation seems to have a defect:
		 * 
		 * Detected a sibling of a Text whose container JAXBElement was contained in the
		 * siblings list instead the Text element.
		 * 
		 * That's why we're just using the element and the parent to compute
		 * multi-Text-element tokens.
		 */

		TokenRecording currentRecording = !recordings.isEmpty() ? recordings.get(recordings.size() - 1) : null;
		boolean buildNewRecording = false;
		if (currentRecording != null) {
			if (currentRecording.parent == parent) {
				currentRecording.texts.add(element);
			} else {
				buildNewRecording = true;
			}
		} else {
			buildNewRecording = true;
		}

		if (buildNewRecording) {
			currentRecording = new TokenRecording();
			recordings.add(currentRecording);
			currentRecording.parent = parent;
			currentRecording.texts = new ArrayList<Text>();
			currentRecording.texts.add(element);
		}
	}

	private static class TokenRecording {

		private Object parent;

		private List<Text> texts;

		public void process(String textToReplace, String replacement) {
			String tokenText = texts.stream().map(t -> t.getValue()).collect(Collectors.joining());

			while (tokenText.contains(textToReplace)) {
				tokenText = tokenText.replace(textToReplace, replacement);
			}

			texts.forEach(t -> t.setParent(null));

			Text newText = new Text();
			newText.setValue(tokenText);
			newText.setParent(parent);
			((ContentAccessor) parent).getContent().clear();
			((ContentAccessor) parent).getContent().add(newText);
		}

	}

}
