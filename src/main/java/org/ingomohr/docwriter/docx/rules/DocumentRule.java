package org.ingomohr.docwriter.docx.rules;

/**
 * A rule to be applied in order to create or update the contents of a document.
 * 
 * @author Ingo Mohr
 */
public interface DocumentRule {

	/**
	 * Applies the rule.
	 * <p>
	 * This method is supposed to be called if {@link #appliesTo(Object)} returned
	 * <code>true</code>.
	 * </p>
	 * 
	 * @param object the object to apply the rule to.
	 */
	void apply(Object object);

	/**
	 * Returns <code>true</code> if this rule applies to the given document data
	 * object.
	 * 
	 * @param object the object to check for.
	 * @return <code>true</code> if the rule can be applied to the given object.
	 */
	boolean appliesTo(Object object);

}
