package org.ingomohr.docwriter.docx;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.ingomohr.docwriter.AbstractDocWriter;
import org.ingomohr.docwriter.DocWriter;
import org.ingomohr.docwriter.DocWriterException;
import org.ingomohr.docwriter.docx.rules.DocumentRule;
import org.ingomohr.docwriter.docx.rules.MarkdownAppenderRule;

/**
 * A simple writer to accept a markdown string and write it to a docx file.
 * <p>
 * Clients just set the content and call the write method.
 * </p>
 * <p>
 * <h3>Subclassing notes</h3> This class actually is just a simple-facade placed
 * before an {@link AbstractRuleBasedDocxWriter} which is used via delegation.
 * Subclasses can override the delegate-writer to customize the
 * writing-behavior.
 * </p>
 * 
 * @author Ingo Mohr
 * @since 1.1
 */
public class SimpleMarkdownDocxWriter extends AbstractDocWriter {

	private DocWriter writer;

	private String markDownContent;

	public SimpleMarkdownDocxWriter() {
		writer = createWriter();
	}

	@Override
	public void write(Path input, Path target) throws DocWriterException {
		requireNonNull(getWriter()).write(input, target);
	}

	@Override
	public void write(InputStream input, OutputStream target) throws DocWriterException {
		requireNonNull(getWriter()).write(input, target);
	}

	/**
	 * Creates the actual writer to be delegated to.
	 * <p>
	 * The default implementation creates an {@link AbstractRuleBasedDocxWriter}
	 * with one markdown rule.
	 * </p>
	 * 
	 * @return delegate-writer to be used for writing. Never <code>null</code>.
	 * @see #getWriter()
	 * @see #setWriter(AbstractRuleBasedDocxWriter)
	 */
	protected DocWriter createWriter() {
		return new AbstractRuleBasedDocxWriter() {

			@Override
			protected List<DocumentRule> initRules() {
				return Arrays
						.asList(new MarkdownAppenderRule(() -> SimpleMarkdownDocxWriter.this.getMarkDownContent()));
			}
		};
	}

	/**
	 * Returns the delegate-writer that is used for writing.
	 * <p>
	 * The default implementation of this class returns the writer created in
	 * {@link #createWriter()}.
	 * </p>
	 * 
	 * @return delegate-writer.
	 * @see #createWriter()
	 */
	protected DocWriter getWriter() {
		return writer;
	}

	/**
	 * Sets the delegate-writer that is used for writing.
	 * 
	 * @param writer the writer to set.
	 */
	protected void setWriter(DocWriter writer) {
		this.writer = writer;
	}

	/**
	 * Returns the markdown content that is to be written.
	 * 
	 * @return markdown content. <code>null</code> if not set.
	 */
	public String getMarkDownContent() {
		return markDownContent;
	}

	/**
	 * Sets the markdown content that is to be written.
	 * 
	 * @param markDownContent the markdown content to be written.
	 */
	public void setMarkDownContent(String markDownContent) {
		this.markDownContent = markDownContent;
	}

}
