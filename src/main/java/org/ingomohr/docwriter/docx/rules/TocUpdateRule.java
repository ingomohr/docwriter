package org.ingomohr.docwriter.docx.rules;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.toc.TocException;
import org.docx4j.toc.TocGenerator;

/**
 * Rule to update an existing table-of-contents (ToC).
 * <p>
 * If there is no ToC in the document, this rule will not execute successfully.
 * </p>
 * 
 * @since 1.3
 */
public class TocUpdateRule implements DocumentRule {

	@Override
	public boolean appliesTo(Object pObject) {
		return pObject instanceof WordprocessingMLPackage;
	}

	@Override
	public void apply(Object pObject) {

		WordprocessingMLPackage doc = (WordprocessingMLPackage) pObject;

		boolean skipPageNumbering = isSkippingPageNumbering();

		try {
			TocGenerator generator = new TocGenerator(doc);
			generator.updateToc(skipPageNumbering);
		} catch (TocException e) {
			throw new RuntimeException("Cannot update table of contents", e);
		}

	}

	/**
	 * The default implementation returns <code>true</code> to skip page numbering.
	 * 
	 * @return <code>true</code> if page numbering is being skipped at updating the
	 *         ToC.
	 */
	protected boolean isSkippingPageNumbering() {
		return true;
	}

}
