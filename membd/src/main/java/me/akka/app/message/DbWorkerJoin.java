package me.akka.app.message;

import java.io.Serializable;

public class DbWorkerJoin implements DbMessage, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7926746743129671505L;

	@Override
	public String getKey() {
		return "COMMON_MSG_KEY";
	}

}
