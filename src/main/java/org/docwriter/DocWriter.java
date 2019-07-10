package org.docwriter;

import java.io.OutputStream;

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
	 * @param target the target to write to. Cannot be <code>null</code>.
	 */
	void write(T cfg, OutputStream target);

}
