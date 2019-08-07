package org.ingomohr.docwriter.docx.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.docx4j.wml.Text;
import org.ingomohr.docwriter.docx.rules.TextReplacementRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTextReplacementRule {

	private static final String TEXT_TO_REPLACE = "#x#";
	private static final String VALUE = "hello world";
	private TextReplacementRule objUT;

	@BeforeEach
	void setup() {
		objUT = new TextReplacementRule();

		objUT.setTextToReplace(TEXT_TO_REPLACE);
		objUT.setValue(VALUE);
	}

	@Test
	void appliesTo_wrongObjectType() {

		String wrongObj = "";
		assertEquals(false, objUT.appliesTo(wrongObj));
	}

	@Test
	void appliesTo_wrongTextToReplace() {
		Text text = createTextWithValue("wrongTextToReplace");
		assertEquals(false, objUT.appliesTo(text));
	}

	@Test
	void appliesTo_happyDay() {
		Text text = createTextWithValue("$(" + TEXT_TO_REPLACE + ")");
		assertEquals(true, objUT.appliesTo(text));
	}

	@Test
	void applyTo() {
		Text text = createTextWithValue(TEXT_TO_REPLACE);
		objUT.apply(text);
		assertEquals(VALUE, text.getValue());

	}

	private Text createTextWithValue(String value) {
		// Mockito refuses to mock this, so we create a real object
		Text text = new Text();
		text.setValue(value);
		return text;
	}

}
