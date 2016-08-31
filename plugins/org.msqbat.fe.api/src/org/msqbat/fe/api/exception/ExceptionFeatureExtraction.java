package org.msqbat.fe.api.exception;

public class ExceptionFeatureExtraction extends Exception {

	private static final long serialVersionUID = 6000965237070329156L;

	public ExceptionFeatureExtraction() {
		super();
	}

	public ExceptionFeatureExtraction(final String message) {
		super(message);
	}

	public ExceptionFeatureExtraction(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ExceptionFeatureExtraction(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ExceptionFeatureExtraction(final Throwable cause) {
		super(cause);
	}
}
