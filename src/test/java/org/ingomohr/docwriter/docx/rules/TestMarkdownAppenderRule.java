package org.ingomohr.docwriter.docx.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;

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

	@Test
	void getParserOptions() {
		List<Object> options = objUT.getParserOptions();
		assertTrue(options.get(0) instanceof TablesExtension);
		assertTrue(options.get(1) instanceof StrikethroughExtension);

		assertEquals(2, options.size());
	}

}
