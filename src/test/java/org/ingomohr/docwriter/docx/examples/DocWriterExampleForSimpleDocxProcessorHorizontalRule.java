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
            addMarkdown("""
                    # 1.  First
                    &nbsp;
                    content
                    &nbsp;

                    # 2.  Second
                    &nbsp;
                    content
                    &nbsp;

                    ***

                    **Date :** &nbsp;&nbsp;04.11.2022

                    ***""");

            addHeadlineH2("More Markdown");
            addMarkdown("Another horizontal rule:");
            addMarkdown("***");
            saveDocumentToPath(pTargetPath);
        }
    }

}