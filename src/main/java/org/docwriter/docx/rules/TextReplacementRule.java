package org.docwriter.docx.rules;

import java.util.Objects;

import org.docx4j.wml.Text;

/**
 * A rule to replace a certain text in the target document.
 * <p>
 * The text-to-replace is stored in {@link #getTextToReplace()}. The value to
 * set is stored in {@link #getValue()}.
 * </p>
 * 
 * @author Ingo Mohr
 */
public class TextReplacementRule implements DocumentRule {

	private String textToReplace;

	private String value;

	public TextReplacementRule() {
		this(null, null);
	}

	/**
	 * Creates a new rule.
	 * 
	 * @param textToReplace the text to replace.
	 * @param value         the value to replace the text with.
	 */
	public TextReplacementRule(String textToReplace, String value) {
		setTextToReplace(textToReplace);
		setValue(value);
	}

	@Override
	public void apply(Object object) {
		final String newVal = getValue();
		((Text) object).setValue(newVal);
	}

	@Override
	public boolean appliesTo(Object object) {
		if (object instanceof Text) {
			final Text text = (Text) object;

			final String txtVal = text.getValue();
			final String expectedTxtVal = getTextToReplace();

			final boolean appliesTo = Objects.equals(expectedTxtVal, txtVal);

			return appliesTo;
		}
		return false;
	}

	/**
	 * Returns the text to be replaced with the {@link #getValue()} of this rule.
	 * 
	 * @return text to be replaced.
	 */
	public String getTextToReplace() {
		return textToReplace;
	}

	/**
	 * Sets the text to be replaced with the {@link #getValue()} of this rule.
	 * 
	 * @param textToReplace the text to be replaced.
	 */
	public void setTextToReplace(String textToReplace) {
		this.textToReplace = textToReplace;
	}

	/**
	 * Returns the value that shall replace the {@link #getTextToReplace()}.
	 * 
	 * @return the value to replace the text with.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value that shall replace the {@link #getTextToReplace()}.
	 * 
	 * @param value the value to replace the text with.
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
