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

/**
 * This example inserts a table-of-contents (ToC).
 */
public class DocWriterExampleInsertToc {

	public static void main(String[] args) {

		// These rules are just there to create some content including a
		// placeholder for a ToC.
		DocumentRule tocPlaceholder = new MarkdownAppenderRule(() -> "$(toc)");
		DocumentRule h1 = new MarkdownAppenderRule(() -> "# Headline 1");
		DocumentRule h2 = new MarkdownAppenderRule(() -> "## Headline 2");
		DocumentRule txt = new MarkdownAppenderRule(() -> "Hello, this is a text.");
		DocumentRule h3 = new MarkdownAppenderRule(() -> "### Headline 3");

		// This rule will replace the $(toc) placeholder over the headlines.
		DocumentRule tocInsertionRule = new TocInsertionRule("$(toc)");

		AbstractRuleBasedDocxWriter writer = new AbstractRuleBasedDocxWriter() {

			@Override
			protected List<DocumentRule> initRules() {
				return Arrays.asList(tocPlaceholder, h1, h2, txt, h3, tocInsertionRule);
			}
		};

		Path out = Paths.get(System.getProperty("user.home") + "/Desktop/docwriter-toc-insertion.docx");

		try {
			writer.write(out);
		} catch (DocWriterException e) {
			e.printStackTrace();
		}

	}

}
