package me.akka.app.message;

import java.io.Serializable;

/**
 * this is a generic message for value retrieval from memdb. gets the value
 * based on key in the message.
 *
 */
public class DbGetMessage implements Serializable, DbMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3293813299230776317L;
	private final String key;

	public DbGetMessage(String k) {
		this.key = k;
	}

	public String getKey() {
		return key;
	}

}
