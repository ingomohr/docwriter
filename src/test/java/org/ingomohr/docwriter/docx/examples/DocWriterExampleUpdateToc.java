package org.ingomohr.docwriter.docx.examples;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.ingomohr.docwriter.DocWriterException;
import org.ingomohr.docwriter.docx.AbstractRuleBasedDocxWriter;
import org.ingomohr.docwriter.docx.rules.DocumentRule;
import org.ingomohr.docwriter.docx.rules.MarkdownAppenderRule;
import org.ingomohr.docwriter.docx.rules.TocInsertionRule;
import org.ingomohr.docwriter.docx.rules.TocUpdateRule;

/**
 * This example updates an existing table-of-contents (ToC) in a document.
 */
public class DocWriterExampleUpdateToc {

	public static void main(String[] args) {

		Path in = createInputFileWithAToC();
		Path out = Paths.get(System.getProperty("user.home") + "/Desktop/docwriter-toc-updated.docx");

		/*
		 * This writer only has one rule: to update the ToC in the IN-put document file.
		 */
		AbstractRuleBasedDocxWriter writer = new AbstractRuleBasedDocxWriter() {

			@Override
			protected List<DocumentRule> initRules() {
				return Arrays.asList(new TocUpdateRule());
			}
		};

		try {
			writer.write(in, out);
		} catch (DocWriterException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This creates a docx file with an empty ToC and some content.
	 */
	private static Path createInputFileWithAToC() {

		DocumentRule toc = new TocInsertionRule();

		DocumentRule h1 = new MarkdownAppenderRule(() -> "# Headline 1");
		DocumentRule h2 = new MarkdownAppenderRule(() -> "## Headline 2");
		DocumentRule txt = new MarkdownAppenderRule(() -> "Hello, this is a text.");
		DocumentRule h3 = new MarkdownAppenderRule(() -> "### Headline 3");

		AbstractRuleBasedDocxWriter writer = new AbstractRuleBasedDocxWriter() {

			@Override
			protected List<DocumentRule> initRules() {
				return Arrays.asList(toc, h1, h2, txt, h3);
			}
		};

		Path out = Paths.get(System.getProperty("user.home") + "/Desktop/docwriter-toc-update.docx");

		try {
			writer.write(out);
		} catch (DocWriterException e) {
			e.printStackTrace();
		}

		return out;
	}

}
