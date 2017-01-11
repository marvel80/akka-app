package me.akka.app.message;

public class SetRequest {
	private final String key;
	private final String value;
	
	public SetRequest(String k , String v){
		this.key = k;
		this.value = v;
	}
	
	public String getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
	
	
}
