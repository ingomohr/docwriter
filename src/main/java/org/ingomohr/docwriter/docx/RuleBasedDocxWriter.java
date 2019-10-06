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

		WordprocessingMLPackage doc = loadIfAvailableOrInit(input);

		modify(doc, cfg);

		save(target, doc);
	}

	private void modify(WordprocessingMLPackage doc, RuleBasedDocxWriterCfg cfg) {

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

	private WordprocessingMLPackage loadIfAvailableOrInit(InputStream input) throws DocWriterException {
		try {
			return loadFromInputIfAvailable(input);
		} catch (Docx4JException e) {
			throw new DocWriterException(e);
		}
	}

	private void save(OutputStream target, WordprocessingMLPackage doc) throws DocWriterException {
		try {
			doc.save(requireNonNull(target));
		} catch (Docx4JException e) {
			throw new DocWriterException("Error writing target", e);
		}
	}

	private WordprocessingMLPackage loadFromInputIfAvailable(InputStream input) throws Docx4JException {
		if (input != null) {
			return WordprocessingMLPackage.load(input);
		}
		return null;
	}

	public static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
		List<Object> result = new ArrayList<Object>();
		if (obj instanceof JAXBElement)
			obj = ((JAXBElement<?>) obj).getValue();

		if (obj.getClass().equals(toSearch))
			result.add(obj);
		else if (obj instanceof ContentAccessor) {
			List<?> children = ((ContentAccessor) obj).getContent();
			for (Object child : children) {
				result.addAll(getAllElementFromObject(child, toSearch));
			}
		}
		return result;
	}

}
