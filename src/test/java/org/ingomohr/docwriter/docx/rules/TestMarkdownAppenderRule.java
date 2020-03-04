package org.ingomohr.docwriter.docx.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestMarkdownAppenderRule {

	private MarkdownAppenderRule objUT;

	@BeforeEach
	void prep() {
		objUT = new MarkdownAppenderRule();
	}

	@Test
	void appliesTo() {
		WordprocessingMLPackage pack = mock(WordprocessingMLPackage.class);
		assertEquals(true, objUT.appliesTo(pack));
		assertEquals(false, objUT.appliesTo(null));
		assertEquals(false, objUT.appliesTo("wrongType"));
	}

	@Test
	void getNewValue_NoSupplier_ReturnsNull() {
		objUT.setValueSupplier(null);
		assertEquals(null, objUT.getNewValue());
	}

	@Test
	void getNewValue_SupplierReturnsNull_ReturnsNull() {
		objUT.setValueSupplier(() -> null);
		assertEquals(null, objUT.getNewValue());
	}

	@Test
	void getNewValue_HappyDay() {
		objUT.setValueSupplier(() -> "Hello 42");
		assertEquals("Hello 42", objUT.getNewValue());
	}

}
