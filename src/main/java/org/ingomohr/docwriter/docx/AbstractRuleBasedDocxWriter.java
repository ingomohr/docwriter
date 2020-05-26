package org.ingomohr.docwriter.docx;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.ingomohr.docwriter.AbstractDocWriter;
import org.ingomohr.docwriter.DocWriter;
import org.ingomohr.docwriter.DocWriterException;
import org.ingomohr.docwriter.docx.rules.DocumentRule;
import org.ingomohr.docwriter.docx.util.DocxDataInspector;

import com.vladsch.flexmark.docx.converter.DocxRenderer;

/**
 * Rule-based {@link DocWriter} for DOCX files.
 * <p>
 * Subclasses basically only have to implement abstract method
 * {@link #initRules()} to setup the rules to be applied to the elements of the
 * document.
 * </p>
 * 
 * @author Ingo Mohr
 */
public abstract class AbstractRuleBasedDocxWriter extends AbstractDocWriter {

	private List<DocumentRule> rules = new ArrayList<DocumentRule>();

	public AbstractRuleBasedDocxWriter() {

		if (isInitializingAutomatically()) {
			init();
		}
	}

	/**
	 * Initializes the writer.
	 * <p>
	 * By default this is called when the writer is created. Subclasses can override
	 * {@link #isInitializingAutomatically()} to suppress the automatic call. In
	 * that case, they have to call the method themselves.
	 * </p>
	 * <p>
	 * The default implementation calls {@link #initRules()} and sets the rules
	 * returned by that method.
	 * </p>
	 * 
	 * @see #isInitializingAutomatically()
	 * @since 1.3
	 */
	protected void init() {
		setRules(initRules());
	}

	/**
	 * Initializes the rules to be applied to the document (in their given order).
	 * 
	 * @return rules. Never <code>null</code>.
	 */
	protected abstract List<DocumentRule> initRules();

	@Override
	public void write(InputStream input, OutputStream target) throws DocWriterException {

		WordprocessingMLPackage doc = loadDocumentFromInput(input);

		if (doc == null) {
			doc = createDefaultDocument();
		}

		modifyDoc(doc);

		save(doc, target);
	}

	/**
	 * Creates the default document to be used to write to.
	 * <p>
	 * The default implementation of the writer calls this method if no input
	 * document was passed to the writer.
	 * </p>
	 * 
	 * @return new default document to write to.
	 */
	protected WordprocessingMLPackage createDefaultDocument() {
		return DocxRenderer.getDefaultTemplate();
	}

	/**
	 * Modifies the given document by filing through all elements in the doc and
	 * applying all matching rules that have been specified for this writer.
	 * 
	 * @param doc the document to modify.
	 */
	protected void modifyDoc(WordprocessingMLPackage doc) {

		applyAllMatchingRulesToElement(doc);

		final MainDocumentPart part = doc.getMainDocumentPart();
		applyAllMatchingRulesToElement(part);

		final DocxDataInspector inspector = new DocxDataInspector();
		final List<Object> docElements = inspector.getAllElements(part, Object.class);

		docElements.stream().forEach(elmt -> {
			applyAllMatchingRulesToElement(elmt);
		});
	}

	/**
	 * Applies all matching rules - i.e. all rules that match the given element - to
	 * the given element.
	 * 
	 * @param element the element to apply the rules to.
	 */
	protected void applyAllMatchingRulesToElement(Object element) {
		final List<DocumentRule> applyingRules = getAllMatchingRulesForElement(element);

		applyingRules.forEach(applyingRule -> {
			applyingRule.apply(element);
		});
	}

	/**
	 * Returns all matching rules for the given element.
	 * 
	 * @param element the element for which to return the matching rules.
	 * @return matching rules. Never <code>null</code>, possibly empty.
	 */
	protected List<DocumentRule> getAllMatchingRulesForElement(Object element) {
		final List<DocumentRule> applyingRules = getRules().stream().filter(rule -> rule.appliesTo(element))
				.collect(Collectors.toList());
		return applyingRules;
	}

	/**
	 * Loads the document from the given input.
	 * 
	 * @param input the input to load from.
	 * @return loaded document. <code>null</code> if input was <code>null</code>.
	 * @throws Docx4JException if loading fails.
	 */
	protected WordprocessingMLPackage loadDocumentFromInput(InputStream input) throws DocWriterException {
		try {
			return loadDocumentFromInputIfAvailable(input);
		} catch (Docx4JException e) {
			throw new DocWriterException(e);
		}
	}

	private WordprocessingMLPackage loadDocumentFromInputIfAvailable(InputStream input) throws Docx4JException {
		if (input != null) {
			return WordprocessingMLPackage.load(input);
		}
		return null;
	}

	/**
	 * Writes the given document to the given target.
	 * 
	 * @param doc    the document to write.
	 * @param target the target to write to.
	 * @throws DocWriterException if writing fails.
	 */
	protected void save(WordprocessingMLPackage doc, OutputStream target) throws DocWriterException {
		try {
			doc.save(requireNonNull(target));
		} catch (Docx4JException e) {
			throw new DocWriterException("Error writing target", e);
		}
	}

	/**
	 * Returns the rules to be applied to the document.
	 * 
	 * @return rules. Never <code>null</code>.
	 */
	protected List<DocumentRule> getRules() {
		return rules;
	}

	/**
	 * Sets the rules to be applied to the document.
	 * 
	 * @param rules the rules to set. Cannot be <code>null</code>.
	 * @since 1.3
	 */
	protected void setRules(List<DocumentRule> rules) {
		requireNonNull(rules);
		this.rules = rules;
	}

	/**
	 * Returns <code>true</code> if {@link #init()} is to be invoked automatically
	 * at construction time.
	 * <p>
	 * If <code>false</code>, subclasses call {@link #init()} themselves.
	 * </p>
	 * <p>
	 * Default is <code>true</code>.
	 * </p>
	 * 
	 * @return <code>true</code> to initialize automatically. <code>false</code>
	 *         otherwise.
	 * @see #init()
	 * @since 1.3
	 */
	protected boolean isInitializingAutomatically() {
		return true;
	}

}
