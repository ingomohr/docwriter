package org.ingomohr.docwriter.docx.examples;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.ingomohr.docwriter.DocWriterException;
import org.ingomohr.docwriter.docx.SimpleMarkdownDocxWriter;

/**
 * Example for {@link SimpleMarkdownDocxWriter}.
 * 
 * @author Ingo Mohr
 */
public class DocWriterExampleSimpleMarkdownDocx {

	public static void main(String[] args) {

		SimpleMarkdownDocxWriter writer = new SimpleMarkdownDocxWriter();
		writer.setMarkDownContent("Hello **World**!");

		Path out = Paths.get("/users/ingomohr/Desktop/docwriter-test-simpleMarkDownDocxWriter.docx");
		try {
			writer.write(out);
			System.out.println("Done");
		} catch (DocWriterException e) {
			e.printStackTrace();
		}
	}

}
