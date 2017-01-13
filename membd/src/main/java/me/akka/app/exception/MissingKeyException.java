package me.akka.app.exception;

import java.io.Serializable;

/**
 * this is the base exception for membd when key requested is not found
 * 
 *
 */
public class MissingKeyException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1633441524392406519L;

	private final String dbKey;

	public MissingKeyException(String givenKey) {
		this.dbKey = givenKey;
	}

	public String getDbKey() {
		return dbKey;
	}

}
