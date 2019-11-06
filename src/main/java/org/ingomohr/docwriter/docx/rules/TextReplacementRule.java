package org.ingomohr.docwriter.docx.rules;

import java.util.Objects;
import java.util.function.Supplier;

import org.docx4j.wml.Text;

/**
 * A rule to replace a certain text in the target document.
 * <p>
 * The text-to-replace (aka placeholder) is stored in
 * {@link #getTextToReplace()}. The value to set is stored in
 * {@link #getValueSupplier()}.
 * </p>
 * <p>
 * </p>
 * <p>
 * Placeholder format is <code>$(placeholder-name)</code>.
 * </p>
 * 
 * @author Ingo Mohr
 */
public class TextReplacementRule implements DocumentRule {

	private String textToReplace;

	private Supplier<String> valueSupplier;

	public TextReplacementRule() {
		this(null, null);
	}

	/**
	 * Creates a new rule.
	 * 
	 * @param textToReplace the text to replace.
	 * @param valueSupplier the value supplier to use to return the value to replace
	 *                      the text with.
	 */
	public TextReplacementRule(String textToReplace, Supplier<String> valueSupplier) {
		setTextToReplace(textToReplace);
		setValueSupplier(valueSupplier);
	}

	@Override
	public void apply(Object object) {
		final Supplier<String> supplier = getValueSupplier();

		final String newVal = supplier != null ? supplier.get() : null;
		((Text) object).setValue(newVal);
	}

	@Override
	public boolean appliesTo(Object object) {
		if (object instanceof Text) {
			final Text text = (Text) object;

			final String txtVal = text.getValue();
			final String expectedTxtVal = toPlaceholder(getTextToReplace());

			final boolean appliesTo = Objects.equals(expectedTxtVal, txtVal);

			return appliesTo;
		}
		return false;
	}

	/**
	 * Returns the text to be replaced with the {@link #getValue()} of this rule.
	 * <p>
	 * The text in the document is supposed to have the text wrapped as
	 * <code>$(&lt;textToReplace&gt;)</code>.
	 * </p>
	 * 
	 * @return text to be replaced.
	 */
	public String getTextToReplace() {
		return textToReplace;
	}

	/**
	 * Sets the text to be replaced with the {@link #getValue()} of this rule.
	 * <p>
	 * The text in the document is supposed to have the given text wrapped as
	 * <code>$(&lt;textToReplace&gt;)</code>.
	 * </p>
	 * 
	 * @param textToReplace the text to be replaced.
	 */
	public void setTextToReplace(String textToReplace) {
		this.textToReplace = textToReplace;
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

	private String toPlaceholder(String text) {
		return "$(" + text + ")";
	}

}
