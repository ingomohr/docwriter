package org.ingomohr.docwriter;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Base implementation of a {@link DocWriter}. Implements file access.
 * 
 * @param <T> configuration type.
 * @author Ingo Mohr
 */
public abstract class AbstractDocWriter<T extends DocWriterCfg> implements DocWriter<T> {

	@Override
	public void write(T cfg, Path input, Path target) throws DocWriterException {

		try (InputStream in = openInputIfAvailable(input)) {

			try (OutputStream out = Files.newOutputStream(requireNonNull(target))) {
				write(requireNonNull(cfg), in, out);
			}

		} catch (IOException e) {
			throw new DocWriterException("Error writing document", e);
		}
	}

	/**
	 * Opens the {@link InputStream} for the given input path.
	 * 
	 * @param input the input path to open.
	 * @return input stream or <code>null</code> if the given path is
	 *         <code>null</code>.
	 * @throws IOException if stream cannot be opened from given path.
	 */
	protected InputStream openInputIfAvailable(Path input) throws IOException {
		if (input != null) {
			return Files.newInputStream(input);
		}
		return null;
	}

}
