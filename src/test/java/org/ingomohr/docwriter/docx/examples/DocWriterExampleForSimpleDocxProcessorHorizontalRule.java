package org.ingomohr.docwriter.docx.examples;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.ingomohr.docwriter.docx.SimpleDocxProcessor;

/**
 * Example for adding horizontal rules via markdown.
 */
public class DocWriterExampleForSimpleDocxProcessorHorizontalRule {

	public static void main(String[] args) {

		Path out = Paths.get(System.getProperty("user.home") + "/Desktop/doc-example-with-horizontal-rule.docx");

		try {
			new MyDocWriter().write(out);
			System.out.println("Wrote docx to " + out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class MyDocWriter extends SimpleDocxProcessor {

		public void write(Path pTargetPath) throws IOException {

			createDocument();

			// @formatter:off
            String md = "# 1.  First"
            		+ "\n content"
            		+ "\n\n"
            		+ "\n # 2. Second"
            		+ "\n content"
            		+ "\n\n"
            		+ "\n***"
            		+ "\n"
            		+ "**Date :**"
            		+ "\n\n"
            		+ "04.11.2022"
            		+ "\n"
            		+ "***";
            // @formatter:on
			addMarkdown(md);

			addHeadlineH2("More Markdown");
			addMarkdown("Another horizontal rule:");
			addMarkdown("***");
			saveDocumentToPath(pTargetPath);
		}
	}

}