package me.akka.app.actor;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import me.akka.app.message.SetRequest;

public class MemDbActor extends AbstractActor {
	private static final Logger log = LoggerFactory.getLogger(MemDbActor.class);
	private final Map<String, String> map;

	private MemDbActor() {
		map = new HashMap<>();
		receive(ReceiveBuilder.match(SetRequest.class, message -> {
			log.info("_message=\"success. recived msg\". key={} value={}", message.getKey(), message.getValue());
			map.put(message.getKey(), message.getValue());
		}).matchAny(o -> log.info("recieved msg unkown", o)).build());
	}

	public Map<String, String> getMap() {
		return map;
	}
	
	

}
