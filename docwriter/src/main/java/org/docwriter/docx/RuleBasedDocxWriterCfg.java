package org.docwriter.docx;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.docwriter.DocWriterCfg;
import org.docwriter.docx.rules.DocumentRule;

/**
 * A configuration based on a list of rules to be applied in order to
 * create/update the target document.
 * 
 * @author Ingo Mohr
 */
public class RuleBasedDocxWriterCfg implements DocWriterCfg {

	private final List<DocumentRule> rules;

	/**
	 * Creates a new and empty configuration.
	 */
	public RuleBasedDocxWriterCfg() {
		this(Collections.emptyList());
	}

	/**
	 * Creates a new and empty configuration.
	 */
	public RuleBasedDocxWriterCfg(Collection<DocumentRule> rules) {
		this.rules = new ArrayList<DocumentRule>();

		requireNonNull(rules);
		this.rules.addAll(rules);
	}

	/**
	 * Adds the given rule.
	 * 
	 * @param rule the rule to add. Cannot be <code>null</code>.
	 */
	public void addRule(DocumentRule rule) {
		requireNonNull(rule);
		rules.add(rule);
	}

	/**
	 * Adds the given rule.
	 * 
	 * @param rule the rule to add. Cannot be <code>null</code>.
	 */
	public void addRules(DocumentRule... rules) {
		requireNonNull(rules);
		this.rules.addAll(Arrays.asList(rules));
	}

	/**
	 * Returns the rules added to this config.
	 * 
	 * @return rules. Never <code>null</code>, possibly empty.
	 */
	public List<DocumentRule> getRules() {
		return this.rules;
	}

}
