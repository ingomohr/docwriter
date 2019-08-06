package org.docwriter;

/**
 * Exception thrown if a {@link DocWriter} cannot write.
 * 
 * @author Ingo Mohr
 */
public class DocWriterException extends Exception {

	private static final long serialVersionUID = 6574905307129688972L;

	public DocWriterException() {
		super();
	}

	public DocWriterException(String message, Throwable cause) {
		super(message, cause);
	}

	public DocWriterException(String message) {
		super(message);
	}

	public DocWriterException(Throwable cause) {
		super(cause);
	}

}
