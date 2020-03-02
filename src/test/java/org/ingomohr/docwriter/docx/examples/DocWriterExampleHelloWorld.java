package org.ingomohr.docwriter.docx.examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.ingomohr.docwriter.AbstractDocWriter;
import org.ingomohr.docwriter.DocWriterException;
import org.ingomohr.docwriter.docx.AbstractRuleBasedDocxWriter;
import org.ingomohr.docwriter.docx.rules.DocumentRule;
import org.ingomohr.docwriter.docx.rules.MarkdownAppenderRule;

/**
 * Example to write a DOCX file saying "Hello World".
 * 
 * @author Ingo Mohr
 */
public class DocWriterExampleHelloWorld {

	public static void main(String[] args) {

		Path out = Paths.get("/users/ingomohr/Desktop/docwriter-helloworld.docx");

		AbstractDocWriter writer = new AbstractRuleBasedDocxWriter() {

			@Override
			protected List<DocumentRule> initRules() {

				try {
					String sample = readMdSample();
					return Arrays.asList(new MarkdownAppenderRule(() -> sample));
				} catch (IOException e) {
					e.printStackTrace();
					return Collections.emptyList();
				}
			}
		};

		try {
			writer.write(null, out);
		} catch (DocWriterException e) {
			e.printStackTrace();
		}

	}

	private static String readMdSample() throws IOException {
		Path in = Paths.get("src/test/resources/org/ingomohr/docwriter/docx/examples/sample.md");
		List<String> lines = Files.readAllLines(in);
		return String.join("\n", lines);
	}

}
