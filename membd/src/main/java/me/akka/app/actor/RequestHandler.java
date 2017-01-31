package me.akka.app.actor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.japi.pf.ReceiveBuilder;
import me.akka.app.exception.WorkerUnavailableException;
import me.akka.app.message.DbGetMessage;
import me.akka.app.message.DbMessage;
import me.akka.app.message.DbPutMessage;
import me.akka.app.message.DbWorkerJoin;

public class RequestHandler extends AbstractActor {
	List<ActorRef> workerNodes = new ArrayList<>();
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private static boolean isSubscribed = false; 

	private RequestHandler() {
		if(!isSubscribed){
			subscribeToPubSubMediator();	
			isSubscribed = true;
		}
		
		receive(ReceiveBuilder.match(DbGetMessage.class, o -> handleDbMessage(o))
				.match(DbPutMessage.class, put -> handleDbMessage(put))
				.match(DbWorkerJoin.class, x -> {
					log.info("worker sent a joining request. actorPath={}" , sender().path());
					getContext().watch(sender());
					workerNodes.add(sender());
					})
				.match(DistributedPubSubMediator.SubscribeAck.class, 
						ack -> log.info("Subscribe Acknowledged for memberPath={}" , self().path()))
				.matchAny(m -> unhandled(m)	)
				.build());
	}

	private void handleDbMessage(DbMessage msg) {
		log.info("entering handle messasge. MessageType={}" , msg.getClass().getName());
		
		if (workerNodes.isEmpty()) {
			sender().tell(new WorkerUnavailableException("no active worker present. try in some time."), self());
		} else {
			// modulo will point to worker that handles the key requested
			ActorRef worker = workerNodes.get(msg.getKey().hashCode() % workerNodes.size());
			worker.forward(msg, getContext());
		}
	}
	
	private void subscribeToPubSubMediator(){
		ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
		mediator.tell(new DistributedPubSubMediator.Subscribe("dbRequests", self()), self());
	}

}
