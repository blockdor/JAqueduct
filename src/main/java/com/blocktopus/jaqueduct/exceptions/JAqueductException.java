package com.blocktopus.jaqueduct.exceptions;

import com.blocktopus.jaqueduct.JsonObject;

public class JAqueductException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void setJsonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public JAqueductException(String message, JsonObject jsonObject) {
		super(message);
		this.jsonObject = jsonObject;
	}

	public JAqueductException(String message, Throwable cause, JsonObject jsonObject) {
		super(message, cause);
		this.jsonObject = jsonObject;
	}

	private JsonObject jsonObject;

	public JAqueductException(String message, Throwable cause) {
		super(message, cause);
	}

	public JAqueductException(String message) {
		super(message);
	}

	public JAqueductException(Throwable cause) {
		super(cause);
	}

}
