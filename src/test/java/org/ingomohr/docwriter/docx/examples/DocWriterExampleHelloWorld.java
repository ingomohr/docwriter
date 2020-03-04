package org.ingomohr.docwriter.docx.examples;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.ingomohr.docwriter.DocWriterException;
import org.ingomohr.docwriter.docx.AbstractRuleBasedDocxWriter;
import org.ingomohr.docwriter.docx.rules.DocumentRule;
import org.ingomohr.docwriter.docx.rules.MarkdownAppenderRule;

/**
 * This example just writes a new docx file saying "Hello, my <b>dear</b>
 * <i>world</i>!".
 * 
 * @author Ingo Mohr
 */
public class DocWriterExampleHelloWorld {

	public static void main(String[] args) {

		AbstractRuleBasedDocxWriter writer = new AbstractRuleBasedDocxWriter() {

			@Override
			protected List<DocumentRule> initRules() {
				return Arrays.asList(new MarkdownAppenderRule(() -> "Hello, my **dear** _world_!"));
			}
		};

		Path out = Paths.get("/users/ingomohr/Desktop/docwriter-helloworld.docx");

		try {
			writer.write(null, out);
		} catch (DocWriterException e) {
			e.printStackTrace();
		}

	}

}
