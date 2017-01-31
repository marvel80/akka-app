package me.akka.memdb_client.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSub;
import akka.japi.pf.ReceiveBuilder;
import me.akka.app.message.DbGetMessage;
import me.akka.app.message.DbPutMessage;

public class PublisherActor extends AbstractActor{
	ActorRef mediator = DistributedPubSub.get(context().system()).mediator();
	
	public PublisherActor(){
		receive(ReceiveBuilder.match(DbGetMessage.class, ap -> {
			sendMessage(ap);
		}).match(DbPutMessage.class, put -> sendMessage(put)).matchAny(o -> unhandled(o)).build());
	}
	
	private void sendMessage(Object msg){
		mediator.tell(msg, self());
	}
	
}
