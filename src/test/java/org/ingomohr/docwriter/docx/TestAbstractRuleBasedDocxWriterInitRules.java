package org.ingomohr.docwriter.docx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import org.ingomohr.docwriter.docx.rules.DocumentRule;
import org.ingomohr.docwriter.docx.rules.TocInsertionRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests rules-initialization modes.
 */
public class TestAbstractRuleBasedDocxWriterInitRules {

	private List<DocumentRule> rules;

	@SuppressWarnings("unchecked")
	@BeforeEach
	void prep() {
		rules = mock(List.class);
	}

	@Test
	void initAutomatically() {
		final AbstractRuleBasedDocxWriter writer = new AbstractRuleBasedDocxWriter() {

			@Override
			protected List<DocumentRule> initRules() {
				return rules;
			}
		};
		assertSame(rules, writer.getRules());
	}

	@Test
	void initExplicity() {
		final AbstractRuleBasedDocxWriter writer = new AbstractRuleBasedDocxWriter() {

			@Override
			protected List<DocumentRule> initRules() {
				return rules;
			}

			@Override
			protected boolean isInitializingAutomatically() {
				return false;
			}

		};
		assertEquals(0, writer.getRules().size());
		writer.setRules(writer.initRules());
		assertSame(rules, writer.getRules());
	}

	@Test
	void passParameterToInit() {
		MyWriter writer = new MyWriter("helloToc");
		assertEquals("helloToc", ((TocInsertionRule) writer.getRules().get(0)).getTocPlaceholder());
	}

	private static class MyWriter extends AbstractRuleBasedDocxWriter {

		private String val;

		public MyWriter(String val) {
			this.val = val;
			init();
		}

		@Override
		protected List<DocumentRule> initRules() {
			return Arrays.asList(new TocInsertionRule(val));
		}

		@Override
		protected boolean isInitializingAutomatically() {
			return false;
		}

	}

}
