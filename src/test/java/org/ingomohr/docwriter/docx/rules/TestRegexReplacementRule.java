package org.ingomohr.docwriter.docx.rules;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Supplier;

import org.docx4j.wml.Text;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@TestMethodOrder(Alphanumeric.class)
public class TestRegexReplacementRule {

	private static final String REGEX_CAPITAL_LETTER_MINUS_INT = "[A-Z]-[0-9]";
	private static final String HELLO_WORLD = "hello world";

	private RegexReplacementRule objUT;

	private boolean appliesToResult;
	private Text text;
	private IllegalArgumentException thrownException;

	@BeforeEach
	void setup() {
		objUT = new RegexReplacementRule();

		appliesToResult = false;
		thrownException = null;
	}

	@Test
	void appliesTo_WrongObjectType_ReturnsFalse() {
		givenRegexToReplaceIs(REGEX_CAPITAL_LETTER_MINUS_INT);
		givenValueSupplierIs(() -> HELLO_WORLD);
		whenAppliesToIsCalledFor("");
		thenAppliesToResultIs(false);
	}

	@Test
	void appliesTo_TextDoesntMatchPattern_ReturnsFalse() {
		givenRegexToReplaceIs(REGEX_CAPITAL_LETTER_MINUS_INT);
		givenValueSupplierIs(() -> HELLO_WORLD);
		Text text = mkText("A-A");
		whenAppliesToIsCalledFor(text);
		thenAppliesToResultIs(false);
	}

	@Test
	void appliesTo_TextPartiallyMatchesPattern_ReturnsFalse() {
		givenRegexToReplaceIs("[A-Z]-");
		givenValueSupplierIs(() -> HELLO_WORLD);
		Text text = mkText("C");
		whenAppliesToIsCalledFor(text);
		thenAppliesToResultIs(false);
	}

	@Test
	void appliesTo_TextMatchesPattern_ReturnsTrue() {
		givenRegexToReplaceIs(REGEX_CAPITAL_LETTER_MINUS_INT);
		givenValueSupplierIs(() -> HELLO_WORLD);
		Text text = mkText("A-9");
		whenAppliesToIsCalledFor(text);
		thenAppliesToResultIs(true);
	}

	@Test
	void applyTo_TextMatchesPattern_ReplacesEntireValue() {
		givenRegexToReplaceIs(REGEX_CAPITAL_LETTER_MINUS_INT);
		givenValueSupplierIs(() -> HELLO_WORLD);
		Text text = mkText("A-9");
		whenApplyIsCalledFor(text);
		thenTextValueIs(HELLO_WORLD);
	}

	@Test
	void applyTo_RuleDoesntApplyToInput_ReplacesNothingAndThrowsException() {
		givenRegexToReplaceIs(REGEX_CAPITAL_LETTER_MINUS_INT);
		givenValueSupplierIs(() -> HELLO_WORLD);
		Text text = mkText("hello");
		whenApplyIsCalledFor(text);
		thenIllegalArgumentExceptionWasThrownFor(text);
		thenValueWasNotReplaced();
		thenTextValueIs("hello");
	}

	@Test
	void applyTo_ValueIsRegex_RegexIsReplaced() {
		givenRegexToReplaceIs("(foo)zar");
		givenValueSupplierIs(() -> "$1bar");
		Text text = mkText("foozar");
		whenApplyIsCalledFor(text);
		thenTextValueIs("foobar");
	}

	private void givenRegexToReplaceIs(String regexToReplace) {
		objUT.setRegexToReplace(regexToReplace);
	}

	private void givenValueSupplierIs(Supplier<String> pSupplier) {
		objUT.setValueSupplier(pSupplier);
	}

	private void whenAppliesToIsCalledFor(Object object) {
		appliesToResult = objUT.appliesTo(object);
	}

	private void whenApplyIsCalledFor(Text text) {
		try {
			objUT.apply(text);
		} catch (IllegalArgumentException ex) {
			thrownException = ex;
		}
	}

	private void thenAppliesToResultIs(boolean expectedResult) {
		assertEquals(expectedResult, appliesToResult);
	}

	private void thenTextValueIs(String expectedValue) {
		assertEquals(expectedValue, text.getValue());
	}

	private void thenValueWasNotReplaced() {
		verify(text, never()).setValue(Mockito.anyString());
	}

	private void thenIllegalArgumentExceptionWasThrownFor(Object object) {
		assertThat(thrownException.getMessage(), CoreMatchers.containsString(object.toString()));
	}

	private Text mkText(String value) {
		text = mock(Text.class);
		when(text.getValue()).thenReturn(value);

		prepareTextMockToAcceptNewValue(text);
		return text;
	}

	private void prepareTextMockToAcceptNewValue(final Text text) {
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

		Answer<String> answer = new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				when(text.getValue()).thenReturn(captor.getValue());
				return null;
			}

		};
		doAnswer(answer).when(text).setValue(captor.capture());
	}

}