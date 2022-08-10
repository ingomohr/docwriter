package org.ingomohr.docwriter.docx.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.SdtContent;
import org.docx4j.wml.SdtElement;
import org.docx4j.wml.Text;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestDocxDataInspector {

	private DocxDataInspector objUT;

	@BeforeEach
	void prep() {
		objUT = new DocxDataInspector();
	}

	@Test
	void getAllElements_DirectType() {
		Text text = mock(Text.class);
		List<Text> result = objUT.getAllElements(text, Text.class);
		assertEquals(1, result.size());
		assertThat(result, CoreMatchers.hasItem(text));
	}

	@Test
	void getAllElements_JaxBElement() {
		Text text = mock(Text.class);

		@SuppressWarnings("unchecked")
		JAXBElement<Text> elmt = mock(JAXBElement.class);
		when(elmt.getValue()).thenReturn(text);

		List<Text> result = objUT.getAllElements(text, Text.class);
		assertEquals(1, result.size());
		assertThat(result, CoreMatchers.hasItem(text));
	}

	@Test
	void getAllElements_SdtElement() {

		SdtContent parent = mock(SdtContent.class);

		SdtElement sdtElmt = mock(SdtElement.class);
		SdtContent sdtContent = mock(SdtContent.class);
		when(sdtElmt.getSdtContent()).thenReturn(sdtContent);

		Text text = mock(Text.class);
		when(sdtContent.getContent()).thenReturn(Arrays.asList(text));

		when(parent.getContent()).thenReturn(Arrays.asList(sdtElmt));

		assertThat(objUT.getAllElements(parent, Text.class), CoreMatchers.hasItems(text));
	}

	@Test
	void getAllElements_MultiFindings() {
		Text text1 = mock(Text.class, "text1");
		Text text2 = mock(Text.class, "text2");

		Text text3 = mock(Text.class, "text3");
		ContentAccessor objChild = mock(ContentAccessor.class);
		when(objChild.getContent()).thenReturn(Arrays.asList(text3));

		ContentAccessor obj = mock(ContentAccessor.class);
		when(obj.getContent()).thenReturn(Arrays.asList(text1, objChild, text2));

		List<Text> result = objUT.getAllElements(obj, Text.class);
		assertEquals(3, result.size());
		assertThat(result, CoreMatchers.hasItems(text1, text2, text3));
	}

}
