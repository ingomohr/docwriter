package org.ingomohr.docwriter.docx;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import org.docx4j.jaxb.Context;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Br;
import org.docx4j.wml.P;
import org.docx4j.wml.STBrType;
import org.ingomohr.docwriter.docx.rules.MarkdownAppenderRule;
import org.ingomohr.docwriter.docx.rules.TocInsertionRule;
import org.ingomohr.docwriter.docx.rules.TocUpdateRule;

import com.vladsch.flexmark.docx.converter.DocxRenderer;

/**
 * Simple Docx processor to open, modify and save docx files.
 * <p>
 * The processor provides a set of methods to load or create a document, to add
 * and modify content and to save the document.
 * </p>
 * 
 * @author Ingo Mohr
 * @since 2.1
 */
public class SimpleDocxProcessor {

	private WordprocessingMLPackage document;

	/**
	 * Creates a new document.
	 * <p>
	 * This will replace the current document if there is one.
	 * </p>
	 */
	public void createDocument() {
		setDocument(DocxRenderer.getDefaultTemplate());
	}

	/**
	 * Loads the document from the given path.
	 * 
	 * @param path the path to load from. Cannot be <code>null</code>.
	 * @throws IOException if loading fails.
	 */
	public void loadDocument(Path path) throws IOException {
		requireNonNull(path);
		loadDocument(path.toFile());
	}

	/**
	 * Loads the document from the given file.
	 * 
	 * @param file the file to load from. Cannot be <code>null</code>.
	 * @throws IOException if loading fails.
	 */
	public void loadDocument(File file) throws IOException {
		requireNonNull(file);
		try {
			setDocument(WordprocessingMLPackage.load(file));
		} catch (Docx4JException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Adds a toc to the document.
	 * <p>
	 * After all headlines have been added, the toc needs to be updated. See
	 * {@link #updateToc()}.
	 * </p>
	 * 
	 * @see #updateToc()
	 */
	public void addToc() {
		new TocInsertionRule().apply(assertedGetDocument());
	}

	/**
	 * Updates the toc.
	 * <p>
	 * This method should be called after a toc was added via {@link #addToc()} and
	 * all headlines have been added.
	 * </p>
	 * <p>
	 * If there is no toc, this does nothing.
	 * </p>
	 * 
	 * @see #addToc()
	 */
	public void updateToc() {
		new TocUpdateRule().apply(assertedGetDocument());
	}

	/**
	 * Replaces all occurrences of the given variable with the given replacement.
	 * <p>
	 * Example: Suppose you work on a docx template that has some variable of
	 * <code>${doc.id}</code>, you can call<br>
	 * <code>replaceVariable("doc.id", "DOC-562342");</code>.
	 * </p>
	 * 
	 * @param variable    the variable to replace - without the <code>${}</code>
	 *                    wrapper. Cannot be <code>null</code>.
	 * @param replacement the replacement to replace the placeholder with. Cannot be
	 *                    <code>null</code>.
	 * @throws Docx4JException if replacing fails.
	 * @since 4.0
	 */
	public void replaceVariable(String variable, String replacement) throws Docx4JException {
		WordprocessingMLPackage mainPackage = assertedGetDocument();

		try {
			VariablePrepare.prepare(mainPackage);
		} catch (Exception e) {
			throw new Docx4JException("Cannot tidy up document", e);
		}

		MainDocumentPart mainDocPart = mainPackage.getMainDocumentPart();

		var mapPlaceholderKeyToVal = new HashMap<String, String>();
		mapPlaceholderKeyToVal.put(variable, replacement);

		try {
			mainDocPart.variableReplace(mapPlaceholderKeyToVal);
		} catch (Exception e) {
			throw new Docx4JException("Cannot replace placeholder", e);
		}
	}

	/**
	 * Adds the given mark down content to the document.
	 * 
	 * @param markDownContent the content to add. Cannot be <code>null</code>.
	 */
	public void addMarkdown(String markDownContent) {
		MarkdownAppenderRule rule = createMarkdownAppenderRule();
		rule.setValueSupplier(() -> markDownContent);
		rule.apply(assertedGetDocument());
	}

	MarkdownAppenderRule createMarkdownAppenderRule() {
		return new MarkdownAppenderRule();
	}

	public void addHeadline(String prefix, String text) {
		addMarkdown(prefix + " " + text);
	}

	/**
	 * Adds a level 1 headline.
	 * 
	 * @param text the text to set.
	 */
	public void addHeadlineH1(String text) {
		addHeadline("#", text);
	}

	/**
	 * Adds a level 2 headline.
	 * 
	 * @param text the text to set.
	 */
	public void addHeadlineH2(String text) {
		addHeadline("##", text);
	}

	/**
	 * Adds a level 3 headline.
	 * 
	 * @param text the text to set.
	 */
	public void addHeadlineH3(String text) {
		addHeadline("###", text);
	}

	/**
	 * Adds a level 4 headline.
	 * 
	 * @param text the text to set.
	 */
	public void addHeadlineH4(String text) {
		addHeadline("####", text);
	}

	/**
	 * Adds a level 5 headline.
	 * 
	 * @param text the text to set.
	 */
	public void addHeadlineH5(String text) {
		addHeadline("#####", text);
	}

	/**
	 * Adds a level 6 headline.
	 * 
	 * @param text the text to set.
	 */
	public void addHeadlineH6(String text) {
		addHeadline("######", text);
	}

	/**
	 * Adds a page-break to the document - so that the next content will start a new
	 * page.
	 */
	public void addPageBreak() {
		MainDocumentPart documentPart = assertedGetDocument().getMainDocumentPart();

		Br breakObj = new Br();
		breakObj.setType(STBrType.PAGE);

		P paragraph = Context.getWmlObjectFactory().createP();
		paragraph.getContent().add(breakObj);
		documentPart.getJaxbElement().getBody().getContent().add(paragraph);
	}

	/**
	 * Saves the document to the given path.
	 * 
	 * @param targetPath the path to save to. Cannot be <code>null</code>.
	 * @throws IOException if there's a problem saving the document.
	 */
	public void saveDocumentToPath(Path targetPath) throws IOException {
		requireNonNull(targetPath);

		try (OutputStream out = Files.newOutputStream(targetPath)) {
			assertedGetDocument().save(out);
		} catch (Docx4JException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Saves the document to the given file.
	 * 
	 * @param file the file to save to. Cannot be <code>null</code>.
	 * @throws IOException if there's a problem saving the document.
	 */
	public void saveDocumentToPath(File file) throws IOException {
		requireNonNull(file);

		try {
			assertedGetDocument().save(file);
		} catch (Docx4JException e) {
			throw new IOException(e);
		}
	}

	public WordprocessingMLPackage getDocument() {
		return document;
	}

	public WordprocessingMLPackage assertedGetDocument() {
		WordprocessingMLPackage doc = getDocument();
		requireNonNull(doc, "No document found. See createDocument() and loadDocument() methods.");
		return doc;
	}

	public void setDocument(WordprocessingMLPackage document) {
		this.document = document;
	}

}
