package org.ingomohr.docwriter.docx;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.ingomohr.docwriter.DocWriterException;
import org.ingomohr.docwriter.docx.rules.DocumentRule;
import org.ingomohr.docwriter.docx.rules.MarkdownAppenderRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestAbstractRuleBasedDocxWriter {

	private AbstractRuleBasedDocxWriter objUT;

	private WordprocessingMLPackage lastExportedContent;

	@BeforeEach
	void prep() {
		objUT = new AbstractRuleBasedDocxWriter() {

			@Override
			protected List<DocumentRule> initRules() {
				return Arrays.asList(new MarkdownAppenderRule(() -> "Hello **small** _world_!"));
			}

			@Override
			protected void save(WordprocessingMLPackage pDoc, OutputStream pTarget) throws DocWriterException {
				lastExportedContent = pDoc;
				super.save(pDoc, pTarget);
			}
		};

		clearCallBackData();
	}

	private void clearCallBackData() {
		lastExportedContent = null;
	}

	@Test
	void test() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		objUT.write(null, out);

		List<String> lines = getAllLinesFromOutput(out);
		assertTrue(!lines.isEmpty());

		assertNotNull(lastExportedContent);
	}

	private List<String> getAllLinesFromOutput(ByteArrayOutputStream out) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		List<String> result = new ArrayList<>();
		for (;;) {
			String line = reader.readLine();
			if (line == null)
				break;
			result.add(line);
		}
		return result;
	}

}
