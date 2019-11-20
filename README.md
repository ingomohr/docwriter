[![Build Status](https://travis-ci.com/ingomohr/docwriter.svg?branch=master)](https://travis-ci.com/ingomohr/docwriter)
[![License](https://img.shields.io/badge/License-Apache%202.0-yellow.svg)](https://opensource.org/licenses/Apache-2.0)
[![GitHub release](https://img.shields.io/github/release/ingomohr/docwriter.svg)](https://GitHub.com/ingomohr/docwriter/releases/)
# docwriter
DocWriter is a Java API to write docx documents...

- based (or not based) on template files
- with option to replace placeholders on the document
- with option to append content as markdown to the document

# First Example: Hello World
This is a simple writer to create a docx file that simple says "Hello _World_!".

```
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.ingomohr.docwriter.DocWriterException;
import org.ingomohr.docwriter.docx.AbstractRuleBasedDocxWriter;
import org.ingomohr.docwriter.docx.rules.DocumentRule;
import org.ingomohr.docwriter.docx.rules.MarkdownAppenderRule;

public class SimpleWriter extends AbstractRuleBasedDocxWriter {
	
	public static void main(String[] args) {

		Path out = Paths.get("/Users/myusername/Desktop/my-doc.docx");
		try {
			// We don't want to read a template docx.
			// So, we just pass null as input
			new SimpleWriter().write(null, out);
		} catch (DocWriterException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected List<DocumentRule> initRules() {
		return Arrays.asList(new MarkdownAppenderRule(() -> "Hello _World_!"));
	}

}
```

# Second Example: Replacing Placeholders
This example reads a template file and in it replaces all occurrences of ``$(name)``and ``$(lastName)``.

```
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.ingomohr.docwriter.DocWriterException;
import org.ingomohr.docwriter.docx.AbstractRuleBasedDocxWriter;
import org.ingomohr.docwriter.docx.rules.DocumentRule;
import org.ingomohr.docwriter.docx.rules.TextReplacementRule;

public class PlaceholderReplacingWriter extends AbstractRuleBasedDocxWriter {

	public static void main(String[] args) {

		final Path in = Paths.get("/Users/myusername/Desktop/my-doc-template.docx");
		final Path out = Paths.get("/Users/myusername/Desktop/my-doc-replaced.docx");
		try {
			new PlaceholderReplacingWriter().write(in, out);
		} catch (DocWriterException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected List<DocumentRule> initRules() {
		DocumentRule ruleName = new TextReplacementRule("name", () -> "John");
		DocumentRule ruleLastName = new TextReplacementRule("lastName", () -> "Doe");

		return Arrays.asList(ruleName, ruleLastName);
	}

}
```

