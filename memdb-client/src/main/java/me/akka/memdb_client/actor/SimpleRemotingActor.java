package me.akka.memdb_client.actor;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Identify;
import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import akka.japi.Procedure;
import me.akka.app.message.DbGetMessage;
import me.akka.app.message.DbPutMessage;
import scala.concurrent.duration.Duration;

public class SimpleRemotingActor extends UntypedActor {

	private final String path;
	private ActorRef memDb;
	private static final Logger log = LoggerFactory.getLogger(SimpleRemotingActor.class);

	public SimpleRemotingActor(String path) {
		this.path = path;
		memDb = null;

		this.identify();
	}

	private void identify() {
		getContext().actorSelection(path).tell(new Identify(path), self());
		getContext().system().scheduler().scheduleOnce(Duration.create(3, TimeUnit.SECONDS), getSelf(),
				ReceiveTimeout.getInstance(), getContext().dispatcher(), getSelf());
	}

	@Override
	public void onReceive(Object incomingMessage) throws Throwable {
		log.info("Entering the recieve method. messageClass={}", incomingMessage.getClass().getName());

		if (incomingMessage instanceof ActorIdentity) {
			memDb = ((ActorIdentity) incomingMessage).getRef();
			if (memDb == null) {
				log.warn("remote DB unavilable");
			} else {
				getContext().watch(memDb);
				getContext().become(operate, true);
				log.info("watching memDb from remoting client actor");
			}
		} else if (incomingMessage instanceof ReceiveTimeout) {
			identify();
		} else {
			log.warn("memDb actor unavailable for now. className={}", incomingMessage.getClass().getName());
		}
	}

	// TODO make it lambda?
	Procedure<Object> operate = new Procedure<Object>() {

		@Override
		public void apply(Object dbOperationMessage) throws Exception {
			log.info("Entering operate. applyMessage={}", dbOperationMessage.getClass().getName());
			
			if (dbOperationMessage instanceof DbGetMessage || dbOperationMessage instanceof DbPutMessage) {
				memDb.tell(dbOperationMessage, self());
			} else if (dbOperationMessage instanceof ActorIdentity) {
				log.info("Actor identity message recieved" , ((ActorIdentity)dbOperationMessage).getRef().toString());
			} else if (dbOperationMessage instanceof ReceiveTimeout) {
				identify();
			} else if (dbOperationMessage instanceof IllegalArgumentException) {
				log.info("incomprehable/bad message sent to db.");
			} else {
				log.error("unsuported meessage encountered. classname={} operation={}",
						dbOperationMessage.getClass().getName(), dbOperationMessage);
				unhandled(dbOperationMessage);
			}
		}
	};

}
