package me.akka.app.actor;

import java.util.ArrayList;
import java.util.List;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import me.akka.app.exception.WorkerUnavailableException;
import me.akka.app.message.DbGetMessage;
import me.akka.app.message.DbMessage;
import me.akka.app.message.DbPutMessage;
import me.akka.app.message.DbWorkerJoin;

public class RequestHandler extends AbstractActor {
	List<ActorRef> workerNodes = new ArrayList<>();

	private RequestHandler() {
		receive(ReceiveBuilder.match(DbGetMessage.class, o -> handleDbMessage(o))
				.match(DbPutMessage.class, put -> handleDbMessage(put))
				.match(DbWorkerJoin.class, x -> {
					getContext().watch(sender());
					workerNodes.add(sender());
					})
				.matchAny(m -> unhandled(m)	)
				.build());
	}

	private void handleDbMessage(DbMessage msg) {
		if (workerNodes.isEmpty()) {
			sender().tell(new WorkerUnavailableException("no active worker present. try in some time."), self());
		} else {
			// modulo will point to worker that handles the key requested
			ActorRef worker = workerNodes.get(msg.getKey().hashCode() % workerNodes.size());
			worker.forward(msg, getContext());
		}
	}

}
