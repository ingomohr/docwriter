[![Build Status](https://travis-ci.com/ingomohr/docwriter.svg?branch=master)](https://travis-ci.com/ingomohr/docwriter)
[![License](https://img.shields.io/badge/License-Apache%202.0-yellow.svg)](https://opensource.org/licenses/Apache-2.0)
[![GitHub release](https://img.shields.io/github/release/ingomohr/docwriter.svg)](https://GitHub.com/ingomohr/docwriter/releases/)
### Motivation
DocWriter is a Java API to write docx documents...

- (optionally) based on template files
- with option to replace placeholders on the document
- with option to append content as markdown to the document
- with option to insert and to update a table-of-contents (ToC)

### Examples
#### Simple Markdown-to-Doxc Conversion
This example creates a new docx file containing "Hello **World**!".
You can use this also for placing tables, code blocks, bullet lists etc.

```Java
SimpleMarkdownDocxWriter writer = new SimpleMarkdownDocxWriter();
writer.setMarkDownContent("Hello **World**!");

Path out = Paths.get(System.getProperty("user.home") + "/Desktop/hello-world.docx");
try {
	writer.write(null, out);
} catch (DocWriterException e) {
	e.printStackTrace();
}
```

#### Rule-based Docx Writer
Sometimes, simple things aren't just good enough. For this, there is a rule-based writer API: ``AbstractRuleBasedDocxWriter``.

This writer is based on the idea of constructing a writer by specifying a number of rules that can apply and perform a certain action.

There are rules built-in for replacing things and for markdown conversion.

##### Markdown-to-Docx Conversion
This is the "Hello World" example from before, written as rule-based writer.

```Java
public class SimpleWriter extends AbstractRuleBasedDocxWriter {
	
	public static void main(String[] args) {

		Path out = Paths.get(System.getProperty("user.home") + "/Desktop/my-doc.docx");
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
		// This is where the rules are specified.
		// For the conversion, we only need the MarkdownAppenderRule
		return Arrays.asList(new MarkdownAppenderRule(() -> "Hello _World_!"));
	}

}
```

##### Replacing Placeholders
This example reads a template file and in it replaces all occurrences of ``$(name)``and ``$(lastName)``.

```Java
public class PlaceholderReplacingWriter extends AbstractRuleBasedDocxWriter {

	public static void main(String[] args) {

		Path in = Paths.get(System.getProperty("user.home") + "/Desktop/my-doc-template.docx");
		Path out = Paths.get(System.getProperty("user.home") + "/Desktop/my-doc-replaced.docx");
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

