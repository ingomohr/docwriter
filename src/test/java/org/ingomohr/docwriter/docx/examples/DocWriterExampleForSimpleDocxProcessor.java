package org.ingomohr.docwriter.docx.examples;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.ingomohr.docwriter.docx.SimpleDocxProcessor;

/**
 * This example specifies a writer based on a {@link SimpleDocxProcessor} to
 * create a new docx document with a toc (table of contents).
 * 
 * @author Ingo Mohr
 */
public class DocWriterExampleForSimpleDocxProcessor {

	public static void main(String[] args) {

		Path out = Paths.get(System.getProperty("user.home") + "/Desktop/example-with-toc.docx");

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
			addToc();
			addHeadlineH1("Headline 1");
			addHeadlineH2("Headline 2");
			addMarkdown("Hello **world**!");
			addPageBreak();

			addHeadlineH1("Headline on next page");
			addMarkdown("This is some more text.");

			updateToc();

			saveDocumentToPath(pTargetPath);
		}
	}

}
