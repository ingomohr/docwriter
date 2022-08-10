package org.ingomohr.docwriter.docx.util;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.docx4j.TraversalUtil;
import org.docx4j.finders.ClassFinder;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Body;
import org.docx4j.wml.Document;

/**
 * Provides access to data in a Docx document.
 * 
 * @author Ingo Mohr
 */
public class DocxDataInspector {

	/**
	 * Returns all elements of the given type that can be found in the document
	 * starting at the given object (and stepping down the corresponding tree).
	 * 
	 * @param startingObj the object to start at. Cannot be <code>null</code>.
	 * @param type        the type to find the elements for. Cannot be
	 *                    <code>null</code>.
	 * @return all elements of given type in the subtree of given object. Can
	 *         include the object itself.
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getAllElements(final Object startingObj, final Class<T> type) {

		requireNonNull(startingObj);
		requireNonNull(type);

		List<T> result = new ArrayList<>();

		if (type.isAssignableFrom(startingObj.getClass())) {
			result.add((T) startingObj);
		}

		ClassFinder classfinder = new ClassFinder(type);
		TraversalUtil.visit(startingObj, classfinder);

		result.addAll((Collection<? extends T>) classfinder.results);
		return result;
	}

	/**
	 * Returns all contents from the given document's main part.
	 * 
	 * @param document the document. Cannot be <code>null</code>.
	 * @return all contents from the main part of the given document. Never
	 *         <code>null</code>, possibly empty.Ï
	 */
	public List<Object> getContents(WordprocessingMLPackage document) {
		MainDocumentPart documentPart = requireNonNull(document).getMainDocumentPart();
		Document wmlDocumentEl = documentPart.getJaxbElement();
		Body body = wmlDocumentEl.getBody();

		List<Object> contents = body.getContent();
		return contents;
	}

}
