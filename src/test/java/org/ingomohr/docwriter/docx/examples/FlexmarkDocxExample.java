package org.ingomohr.docwriter.docx.examples;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import com.vladsch.flexmark.docx.converter.DocxRenderer;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughSubscriptExtension;
import com.vladsch.flexmark.ext.ins.InsExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.SimTocExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.superscript.SuperscriptExtension;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class FlexmarkDocxExample {
	
	public static void main(String[] args) {
		
		DataHolder OPTIONS = new MutableDataSet()
	            .set(Parser.EXTENSIONS, Arrays.asList(
	                    DefinitionExtension.create(),
	                    EmojiExtension.create(),
	                    FootnoteExtension.create(),
	                    StrikethroughSubscriptExtension.create(),
	                    InsExtension.create(),
	                    SuperscriptExtension.create(),
	                    TablesExtension.create(),
	                    TocExtension.create(),
	                    SimTocExtension.create(),
	                    WikiLinkExtension.create()
	            ))
	            .set(DocxRenderer.SUPPRESS_HTML, true)
	            // the following two are needed to allow doc relative and site relative address resolution
	            .set(DocxRenderer.DOC_RELATIVE_URL, "file:///Users/vlad/src/pdf") // this will be used for URLs like 'images/...' or './' or '../'
	            .set(DocxRenderer.DOC_ROOT_URL, "file:///Users/vlad/src/pdf") // this will be used for URLs like: '/...'
	            ;

		String markdown = readMdSample();
		
		Parser PARSER = Parser.builder(OPTIONS).build();
        DocxRenderer RENDERER = DocxRenderer.builder(OPTIONS).build();

        Node document = PARSER.parse(markdown);

        // or to control the package
        WordprocessingMLPackage template = DocxRenderer.getDefaultTemplate();
        RENDERER.render(document, template);
        
        File file = new File("/users/ingomohr/Desktop/docwriter-helloworld.docx");
        try {
            template.save(file, Docx4J.FLAG_SAVE_ZIP_FILE);
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
	}

	private static String readMdSample() {
		Path in = Paths.get("src/test/resources/org/ingomohr/docwriter/docx/examples/sample.md");
		List<String> lines;
		try {
			lines = Files.readAllLines(in);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return String.join("\n", lines);
	}

}
