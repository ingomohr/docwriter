package org.ingomohr.docwriter.docx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

import org.ingomohr.docwriter.DocWriter;
import org.ingomohr.docwriter.docx.rules.DocumentRule;
import org.ingomohr.docwriter.docx.rules.MarkdownAppenderRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestSimpleMarkdownDocxWriter {

	private SimpleMarkdownDocxWriter objUT;

	@BeforeEach
	void prep() {
		objUT = new SimpleMarkdownDocxWriter();
	}

	@Test
	void defaultDelegateWriterIsConfiguredCorrectly() {
		DocWriter writer = objUT.getWriter();
		assertTrue(writer instanceof AbstractRuleBasedDocxWriter);
		List<DocumentRule> rules = ((AbstractRuleBasedDocxWriter) writer).getRules();
		assertEquals(1, rules.size());
		assertTrue(rules.get(0) instanceof MarkdownAppenderRule);

		String inputVal = ((MarkdownAppenderRule) rules.get(0)).getValueSupplier().get();
		assertEquals(null, inputVal);

		objUT.setMarkDownContent("Foo Bar");
		inputVal = ((MarkdownAppenderRule) rules.get(0)).getValueSupplier().get();
		assertEquals("Foo Bar", inputVal);
	}

	@Test
	void writeDoesDelegate_ByPath() throws Exception {

		DocWriter writer = mock(DocWriter.class);
		objUT.setWriter(writer);
		assertSame(writer, objUT.getWriter());

		Path in = mock(Path.class);
		Path out = mock(Path.class);
		objUT.write(in, out);
		verify(writer, times(1)).write(in, out);

		InputStream inStream = mock(InputStream.class);
		OutputStream outStream = mock(OutputStream.class);
		objUT.write(inStream, outStream);
		verify(writer, times(1)).write(inStream, outStream);

	}

}
