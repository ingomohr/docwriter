package org.ingomohr.docwriter;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Writes a document based on a configuration.
 * 
 * @author Ingo Mohr
 */
public interface DocWriter {

	/**
	 * Writes a document.
	 * 
	 * @param input  the input to read from. <i>Optional</i>.
	 * @param target the target to write to. Cannot be <code>null</code>.
	 * @throws DocWriterException if there's a problem writing the document.
	 */
	void write(InputStream input, OutputStream target) throws DocWriterException;

	/**
	 * Writes a document.
	 * 
	 * @param input  the input to read from. <i>Optional</i>.
	 * @param target the target to write to. Cannot be <code>null</code>.
	 * @throws DocWriterException if there's a problem writing the document.
	 */
	void write(Path input, Path target) throws DocWriterException;

	/**
	 * Writes a document.
	 * 
	 * @param target the target to write to. Cannot be <code>null</code>.
	 * @throws DocWriterException if there's a problem writing the document.
	 * @since 1.2
	 */
	default void write(Path target) throws DocWriterException {
		write(null, target);
	}

	/**
	 * Writes a document.
	 * 
	 * @param target the target to write to. Cannot be <code>null</code>.
	 * @throws DocWriterException if there's a problem writing the document.
	 * @since 1.2
	 */
	default void write(OutputStream target) throws DocWriterException {
		write(null, target);
	}

}
