package org.ingomohr.docwriter.docx.examples;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.ingomohr.docwriter.DocWriterException;
import org.ingomohr.docwriter.docx.AbstractRuleBasedDocxWriter;
import org.ingomohr.docwriter.docx.rules.DocumentRule;
import org.ingomohr.docwriter.docx.rules.RegexReplacementRule;

/**
 * Example for regex replacement.
 * <p>
 * Replaces:
 * <ul>
 * <li>all text tokens of format "$(" to "$["</li>
 * <li>all text tokens of format ")" to "]"</li>
 * </ul>
 * </p>
 */
public class DocWriterExampleRegexReplacement {

	public static void main(String[] args) {

		AbstractRuleBasedDocxWriter writer = new AbstractRuleBasedDocxWriter() {

			@Override
			protected List<DocumentRule> initRules() {
				DocumentRule rule1 = new RegexReplacementRule("\\$\\(", () -> "$[");
				DocumentRule rule2 = new RegexReplacementRule("\\)", () -> "]");
				return Arrays.asList(rule1, rule2);
			}
		};

		Path in = Paths.get("src/test/resources/org/ingomohr/docwriter/docx/examples/sample-with-placeholder.docx");
		Path out = Paths.get(System.getProperty("user.home") + "/Desktop/docwriter-example-regexReplacement.docx");

		try {
			writer.write(in, out);
		} catch (DocWriterException e) {
			e.printStackTrace();
		}

	}

}