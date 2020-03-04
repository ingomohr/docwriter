package org.ingomohr.docwriter.docx.rules;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

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
import com.vladsch.flexmark.util.data.MutableDataSet;

/**
 * Appends content given as markdown to the document.
 * 
 * @author Ingo Mohr
 */
public class MarkdownAppenderRule implements DocumentRule {

	private Supplier<String> valueSupplier;

	public MarkdownAppenderRule() {
		this(null);
	}

	public MarkdownAppenderRule(Supplier<String> valueSupplier) {
		setValueSupplier(valueSupplier);
	}

	@Override
	public boolean appliesTo(Object object) {
		return object instanceof WordprocessingMLPackage;
	}

	@Override
	public void apply(Object object) {

		WordprocessingMLPackage doc = (WordprocessingMLPackage) object;
		

		String rawMarkdown = getNewValue();

		MutableDataSet options = createOptions();

		Parser parser = Parser.builder(options).build();
		DocxRenderer RENDERER = DocxRenderer.builder(options).build();

		Node document = parser.parse(rawMarkdown);

		RENDERER.render(document, doc);
	}

	/**
	 * Returns the options to be be applied to the transformation from markdown to
	 * docx.
	 * 
	 * @return options. Never <code>null</code>.
	 */
	protected MutableDataSet createOptions() {
		MutableDataSet options = new MutableDataSet();
		options.set(Parser.EXTENSIONS, getParserExtensions());
		options.set(DocxRenderer.SUPPRESS_HTML, true);
		return options;
	}

	/**
	 * Returns the parser extensions to be used for parsing the markdown content.
	 * 
	 * @return markdown parser options.
	 */
	protected List<Parser.ParserExtension> getParserExtensions() {
		return Arrays.asList(DefinitionExtension.create(), EmojiExtension.create(), FootnoteExtension.create(),
				StrikethroughSubscriptExtension.create(), InsExtension.create(), SuperscriptExtension.create(),
				TablesExtension.create(), TocExtension.create(), SimTocExtension.create(), WikiLinkExtension.create());
	}

	/**
	 * Returns the supplier to return the value to replace the text with.
	 * 
	 * @return value supplier.
	 */
	public Supplier<String> getValueSupplier() {
		return valueSupplier;
	}

	/**
	 * Sets the supplier to return the value to replace the text with.
	 * 
	 * @param valueSupplier the supplier to set.
	 */
	public void setValueSupplier(Supplier<String> valueSupplier) {
		this.valueSupplier = valueSupplier;
	}

	/**
	 * Resolves the new value from the {@link #getValueSupplier()}.
	 * 
	 * @return new value.
	 */
	protected String getNewValue() {
		Supplier<String> supplier = getValueSupplier();
		String newVal = supplier != null ? supplier.get() : null;
		return newVal;
	}

}
