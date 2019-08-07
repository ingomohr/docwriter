package org.ingomohr.docwriter;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Writes a document based on a configuration.
 * 
 * @param <T> the configuration type.
 * 
 * 
 * @author Ingo Mohr
 */
public interface DocWriter<T extends DocWriterCfg> {

	/**
	 * Writes a document according to the given {@link DocWriterCfg}.
	 * 
	 * @param cfg    the configuration. Cannot be <code>null</code>.
	 * @param input  the input to read from. <i>Optional</i>.
	 * @param target the target to write to. Cannot be <code>null</code>.
	 * @throws DocWriterException if there's a problem writing the document.
	 */
	void write(T cfg, InputStream input, OutputStream target) throws DocWriterException;

	/**
	 * Writes a document according to the given {@link DocWriterCfg}.
	 * 
	 * @param cfg    the configuration. Cannot be <code>null</code>.
	 * @param input  the input to read from. <i>Optional</i>.
	 * @param target the target to write to. Cannot be <code>null</code>.
	 * @throws DocWriterException if there's a problem writing the document.
	 */
	void write(T cfg, Path input, Path target) throws DocWriterException;

}
