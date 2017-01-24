package me.akka.app.actor;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.Status;
import akka.actor.UntypedActor;
import akka.japi.pf.ReceiveBuilder;
import me.akka.app.exception.MissingKeyException;
import me.akka.app.message.DbGetMessage;
import me.akka.app.message.DbOperationResultMessage;
import me.akka.app.message.DbPutMessage;

public class MemDbActor extends UntypedActor {
	private static final Logger log = LoggerFactory.getLogger(MemDbActor.class);
	private final Map<String, String> map;

	
	public MemDbActor() {
		map = new HashMap<>();
	}

	public Map<String, String> getMap() {
		return map;
	}

	@Override
	public void onReceive(Object arg0) throws Throwable {
		ReceiveBuilder.match(DbPutMessage.class, putMessage -> {
			log.info("_message=\"Recieved PUT msg\". key={} value={}", putMessage.getKey(),
					putMessage.getValue());
			map.put(putMessage.getKey(), putMessage.getValue());
			sender().tell(new DbOperationResultMessage(putMessage.getKey(), putMessage.getValue()), self());
		}).match(DbGetMessage.class, getMessage -> {
			log.info("_message=\"recieved GET msg\". key={} ", getMessage.getKey());
			String val = map.get(getMessage.getKey());
			sender().tell(val == null ? new Status.Failure(new MissingKeyException(getMessage.getKey()))
					: new DbOperationResultMessage(getMessage.getKey(), val), self());
		}).matchAny(o -> {
			log.info("recieved msg unkown", o);
			sender().tell(new Status.Failure(new IllegalArgumentException("bad message")), self());
		}).build();
		
	}

}
