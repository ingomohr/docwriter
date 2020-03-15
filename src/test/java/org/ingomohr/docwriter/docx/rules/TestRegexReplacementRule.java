package org.ingomohr.docwriter.docx.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.docx4j.wml.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestRegexReplacementRule {

    private static final String REGEX_TO_REPLACE = "[A-Z]-[0-9]";
    private static final String VALUE = "hello world";
    private RegexReplacementRule objUT;

    @BeforeEach
    void setup() {
        objUT = new RegexReplacementRule();

        objUT.setRegexToReplace(REGEX_TO_REPLACE);
        objUT.setValueSupplier(() -> VALUE);
    }

    @Test
    void appliesTo_wrongObjectType() {

        String wrongObj = "";
        assertEquals(false, objUT.appliesTo(wrongObj));
    }

    @Test
    void appliesTo_wrongTextToReplace() {
        Text text = createTextWithValue("A-A");
        assertEquals(false, objUT.appliesTo(text));
    }

    @Test
    void appliesTo_happyDay() {
        Text text = createTextWithValue("A-9");
        assertEquals(true, objUT.appliesTo(text));
    }

    @Test
    void applyTo() {
        Text text = createTextWithValue(REGEX_TO_REPLACE);
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