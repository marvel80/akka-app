package me.akka.app.message;

import java.io.Serializable;

public class DbOperationResultMessage implements DbMessage, Serializable{
	private static final long serialVersionUID = -8569153043862191964L;
	
	private final String key;
	private final String value;
	
	public DbOperationResultMessage(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
