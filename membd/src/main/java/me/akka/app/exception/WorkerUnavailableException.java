package me.akka.app.exception;

import java.io.Serializable;

public class WorkerUnavailableException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6791104444824931076L;
	
	public WorkerUnavailableException(){
		super();
	}
	
	public WorkerUnavailableException(String msg){
		super(msg);
	}
}
