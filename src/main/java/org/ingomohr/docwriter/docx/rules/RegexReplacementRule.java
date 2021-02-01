package org.ingomohr.docwriter.docx.rules;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.docx4j.wml.Text;

/**
 * A rule to replace all findings of a certain regular expression.
 * <p>
 * The regex-to-replace is stored in {@link #getRegexToReplace()}. The value to
 * set is stored in {@link #getValueSupplier()}.
 * </p>
 * <p>
 * The value provided by the {@link #getValueSupplier()} is understood as as
 * regex.
 * </p>
 * 
 * @author Ingo Mohr
 * @since 1.2
 */
public class RegexReplacementRule implements DocumentRule {

	private String regexToReplace;

	private Supplier<String> valueSupplier;

	public RegexReplacementRule() {
		this(null, null);
	}

	/**
	 * Creates a new rule.
	 * 
	 * @param regexToReplace the regex to replace.
	 * @param valueSupplier  the value supplier to use to return the value to
	 *                       replace the regex with. The value from the supplier is
	 *                       understood as regex.
	 */
	public RegexReplacementRule(String regexToReplace, Supplier<String> valueSupplier) {
		setRegexToReplace(regexToReplace);
		setValueSupplier(valueSupplier);
	}

	@Override
	public boolean appliesTo(Object object) {
		if (object instanceof Text) {
			final Text text = (Text) object;

			final String txtVal = text.getValue();
			if (txtVal != null) {
				final String expectedRegExToReplace = getRegexToReplace();
				if (expectedRegExToReplace != null) {
					Pattern pattern = Pattern.compile(expectedRegExToReplace);
					final boolean appliesTo = pattern.matcher(txtVal).matches();
					return appliesTo;
				}
			}

		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Replaces the value of the given Text object with the value from the
	 * {@link #getValueSupplier()}.
	 * </p>
	 * 
	 * @throws IllegalArgumentException if the rule doesn't apply to the given
	 *                                  value.
	 */
	@Override
	public void apply(Object object) {
		if (!appliesTo(object)) {
			throw new IllegalArgumentException("Rule doesn't apply. Call appliesTo() first: " + object);
		}

		Text text = (Text) object;

		String newValueRegex = getNewValue();
		String oldValue = text.getValue();

		String resolvedNewValue = Pattern.compile(getRegexToReplace()).matcher(oldValue).replaceAll(newValueRegex);

		if (!Objects.equals(resolvedNewValue, oldValue)) {
			text.setValue(resolvedNewValue);
		}
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

	/**
	 * Returns the regex to replace.
	 * 
	 * @return regex to replace. <code>null</code> if not set.
	 */
	public String getRegexToReplace() {
		return regexToReplace;
	}

	/**
	 * Sets the regex to replace.
	 * 
	 * @param regexToReplace the regex to replace.
	 */
	public void setRegexToReplace(String regexToReplace) {
		this.regexToReplace = regexToReplace;
	}

	/**
	 * Returns the supplier to return the new value that is supposed to replace the
	 * regex. The value from the supplier is understood as regex.
	 * 
	 * @return supplier to return the new value. <code>null</code> if not set.
	 */
	public Supplier<String> getValueSupplier() {
		return valueSupplier;
	}

	/**
	 * Sets the supplier to return the new value that is supposed to replace the
	 * regex. The value from the supplier is understood as regex.
	 * 
	 * @param valueSupplier the supplier to return the new value.
	 */
	public void setValueSupplier(Supplier<String> valueSupplier) {
		this.valueSupplier = valueSupplier;
	}

}