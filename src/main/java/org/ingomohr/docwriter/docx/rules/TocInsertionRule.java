package org.ingomohr.docwriter.docx.rules;

import static java.util.Objects.requireNonNull;

import java.util.List;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.toc.Toc;
import org.docx4j.toc.TocException;
import org.docx4j.toc.TocGenerator;
import org.docx4j.wml.P;
import org.ingomohr.docwriter.docx.util.DocxDataInspector;

/**
 * Rule to insert a table-of-contents (ToC).
 * <p>
 * The default implementation inserts a standard ToC:
 * <ul>
 * <li>Heading Text: "Table of Contents"</li>
 * <li>ToC Switches: "TOC \\o \"1-3\" \\n 1-3 \\h \\z \\u"</li>
 * <li>skipPageNumbering: true</li>
 * </ul>
 * Subclasses can override the settings to their liking.
 * </p>
 * <p>
 * The rule can be used to insert a ToC by replacing a ToC-placeholder or to
 * append a ToC to the end of a document.
 * <p>
 * Subclasses can override computing the insertion index. See
 * {@link #computeTocInsertionIndex(WordprocessingMLPackage)}.
 * </p>
 * 
 * @since 1.3
 */
public class TocInsertionRule implements DocumentRule {

	private final String tocPlaceholder;

	/**
	 * Creates a new TocInsertionRule specifying no ToC placeholder to replace.
	 */
	public TocInsertionRule() {
		this(null);
	}

	/**
	 * Creates a new TocInsertionRule to replace the given ToC-placeholder in a
	 * document with a new ToC.
	 * 
	 * @param tocPlaceholder the ToC placeholder to replace with the new ToC. If
	 *                       <code>null</code>, the ToC will be appended to the and
	 *                       of the document.
	 */
	public TocInsertionRule(String tocPlaceholder) {
		this.tocPlaceholder = tocPlaceholder;
	}

	@Override
	public boolean appliesTo(Object pObject) {
		return pObject instanceof WordprocessingMLPackage;
	}

	@Override
	public void apply(Object pObject) {

		final WordprocessingMLPackage doc = (WordprocessingMLPackage) pObject;
		final List<Object> contents = new DocxDataInspector().getContents(doc);

		final int insertionIndex = computeTocInsertionIndex(contents);

		final boolean foundPlaceholder = getTocPlaceholder() != null && insertionIndex != -1
				&& insertionIndex < contents.size();

		if (foundPlaceholder) {
			contents.remove(insertionIndex);
		}

		final String headingText = getHeadingText();
		final String switches = getTocSwitches();
		final boolean skipPageNumbering = isSkippingPageNumbering();

		try {
			Toc.setTocHeadingText(headingText);
			TocGenerator generator = new TocGenerator(doc);

			generator.generateToc(insertionIndex, switches, skipPageNumbering);
		} catch (TocException e) {
			throw new RuntimeException("Cannot create table of contents", e);
		}

	}

	/**
	 * Computes the ToC-insertion index - i.e. the index to insert the new ToC at.
	 * <p>
	 * The default implementation returns the index of the ToC placeholder (if
	 * found) or the length of the contents list otherwise.
	 * </p>
	 * 
	 * @param doc the document to compute the ToC-insertion index for. Cannot be
	 *            <code>null</code>.
	 * @return index in the given doc to insert the ToC at.
	 */
	protected int computeTocInsertionIndex(final List<Object> contents) {

		requireNonNull(contents);

		final String placeholder = getTocPlaceholder();

		int index;

		if (placeholder != null) {
			index = 0;
			for (Object obj : contents) {
				// The ToC placeholder is expected alone and in its own line - i.e. a paragraph
				if (obj instanceof P && placeholder.equals(obj.toString())) {
					break;
				}
				index++;
			}
		} else {
			index = contents.size();
		}

		return index;
	}

	/**
	 * Returns the ToC heading text.
	 * 
	 * @return ToC heading text.
	 */
	protected String getHeadingText() {
		return "Table of Contents";
	}

	/**
	 * Returns the ToC switches.
	 * <p>
	 * For a complete guide to Toc field switches check the docx spec in the web.
	 * e.g. this page should help you out: <a href="
	 * https://support.office.com/en-us/article/Field-codes-TOC-Table-of-Contents-field-1f538bc4-60e6-4854-9f64-67754d78d05c?ui=en-US&rs=en-US&ad=US.">Toc
	 * Switches (Microsoft)</a>
	 * </p>
	 * 
	 * @return ToC switches.
	 */
	protected String getTocSwitches() {
		return "TOC \\o \"1-3\" \\n 1-3 \\h \\z \\u";
	}

	/**
	 * The default implementation returns <code>true</code> to skip page numbering.
	 * 
	 * @return <code>true</code> if page numbering is being skipped at creating the
	 *         ToC.
	 */
	protected boolean isSkippingPageNumbering() {
		return true;
	}

	/**
	 * Returns the ToC placeholder as passed to the constructor.
	 * 
	 * @return ToC placeholder to be replaced with the new ToC. <code>null</code> if
	 *         not set.
	 */
	public String getTocPlaceholder() {
		return tocPlaceholder;
	}

}
