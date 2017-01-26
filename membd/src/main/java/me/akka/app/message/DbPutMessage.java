package me.akka.app.message;

import java.io.Serializable;

public class DbPutMessage implements Serializable ,DbMessage{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6164756441371907974L;
	private final String key;
	private final String value;
	
	public DbPutMessage(String k, String v){
		super();
		this.key = k;
		this.value = v;
	}
	
	@Override
	public String getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
	
}
