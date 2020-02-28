package org.ingomohr.docwriter.docx.rules;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import com.vladsch.flexmark.docx.converter.DocxRenderer;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.parser.Parser;
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

		MutableDataSet options = new MutableDataSet();
		options.set(Parser.EXTENSIONS, getParserOptions());

		Parser parser = Parser.builder(options).build();
		DocxRenderer RENDERER = DocxRenderer.builder(options).build();

		Node document = parser.parse(rawMarkdown);

		RENDERER.render(document, doc);
	}

	/**
	 * Returns the options to be used for parsing the markdown content.
	 * 
	 * @return markdown parser options.
	 */
	protected List<Object> getParserOptions() {
		return Arrays.asList(TablesExtension.create(), StrikethroughExtension.create());
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
