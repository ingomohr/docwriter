package org.ingomohr.docwriter.docx;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Text;
import org.ingomohr.docwriter.AbstractDocWriter;
import org.ingomohr.docwriter.DocWriter;
import org.ingomohr.docwriter.DocWriterException;
import org.ingomohr.docwriter.docx.rules.DocumentRule;

/**
 * Rule-based {@link DocWriter} for DOCX files.
 * <p>
 * The writer is configured with a {@link RuleBasedDocxWriterCfg} which can be
 * used to setup placeholder-replacements.
 * </p>
 * 
 * @param <T> the configuration type.
 * @author Ingo Mohr
 */
public class RuleBasedDocxWriter<T extends RuleBasedDocxWriterCfg> extends AbstractDocWriter<T> {

	@Override
	public void write(RuleBasedDocxWriterCfg cfg, InputStream input, OutputStream target) throws DocWriterException {

		WordprocessingMLPackage doc = loadDocumentFromInput(input);

		modifyDoc(doc, cfg);

		save(doc, target);
	}

	private void modifyDoc(WordprocessingMLPackage doc, RuleBasedDocxWriterCfg cfg) {

		final MainDocumentPart part = doc.getMainDocumentPart();
		final List<Object> texts = getAllElementFromObject(part, Text.class).stream().map(Text.class::cast)
				.collect(Collectors.toList());
		texts.stream().forEach(txt -> {
			applyAllMatchingRulesToText(cfg, txt);
		});
	}

	private void applyAllMatchingRulesToText(RuleBasedDocxWriterCfg cfg, Object txt) {
		final List<DocumentRule> applyingRules = getAllMatchingRulesForText(cfg, txt);

		applyingRules.forEach(applyingRule -> {
			applyingRule.apply(txt);
		});
	}

	private List<DocumentRule> getAllMatchingRulesForText(RuleBasedDocxWriterCfg cfg, Object txt) {
		final List<DocumentRule> applyingRules = cfg.getRules().stream().filter(rule -> rule.appliesTo(txt))
				.collect(Collectors.toList());
		return applyingRules;
	}

	private WordprocessingMLPackage loadDocumentFromInput(InputStream input) throws DocWriterException {
		try {
			return loadDocumentFromInputIfAvailable(input);
		} catch (Docx4JException e) {
			throw new DocWriterException(e);
		}
	}

	private WordprocessingMLPackage loadDocumentFromInputIfAvailable(InputStream input) throws Docx4JException {
		if (input != null) {
			return WordprocessingMLPackage.load(input);
		}
		return null;
	}

	private void save(WordprocessingMLPackage doc, OutputStream target) throws DocWriterException {
		try {
			doc.save(requireNonNull(target));
		} catch (Docx4JException e) {
			throw new DocWriterException("Error writing target", e);
		}
	}

	public static List<Object> getAllElementFromObject(final Object obj, final Class<?> toSearch) {
		List<Object> result = new ArrayList<Object>();

		Object object = obj;

		if (object instanceof JAXBElement) {
			object = ((JAXBElement<?>) object).getValue();
		}

		if (object.getClass().equals(toSearch)) {
			result.add(object);
		} else if (object instanceof ContentAccessor) {
			List<?> children = ((ContentAccessor) object).getContent();
			for (Object child : children) {
				result.addAll(getAllElementFromObject(child, toSearch));
			}
		}

		return result;
	}

}
