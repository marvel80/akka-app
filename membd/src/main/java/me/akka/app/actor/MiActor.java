package me.akka.app.actor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Status;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;

public class MiActor extends AbstractActor {

	private final Logger log = LoggerFactory.getLogger(MiActor.class);

	/**
	 * given movie name MI, tells the artist name (Tom cruise).
	 * 
	 * @return
	 */
	public PartialFunction receive() {
		return ReceiveBuilder.matchEquals("Mission Impossible", s -> sender().tell("Tom Cruise", ActorRef.noSender()))
				.matchAny(x -> {
					sender().tell(new Status.Failure(new Exception("cant lookup that movie")), self());
					log.info("incorrect movie");
				}).build();
	}

}
