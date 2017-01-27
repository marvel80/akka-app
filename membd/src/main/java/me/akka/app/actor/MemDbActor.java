package me.akka.app.actor;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.AbstractActor;
import akka.actor.Status;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.cluster.Member;
import akka.cluster.protobuf.msg.ClusterMessages.MemberStatus;
import akka.japi.pf.ReceiveBuilder;
import me.akka.app.exception.MissingKeyException;
import me.akka.app.message.DbGetMessage;
import me.akka.app.message.DbOperationResultMessage;
import me.akka.app.message.DbPutMessage;
import me.akka.app.message.DbWorkerJoin;

public class MemDbActor extends AbstractActor {
	private static final Logger log = LoggerFactory.getLogger(MemDbActor.class);
	private final Map<String, String> map;
	
	Cluster clusterInfo = Cluster.get(getContext().system());
	
	@Override
	public void preStart() {
		log.info("subscribing to cluster events");
		clusterInfo.subscribe(self(), ClusterEvent.initialStateAsEvents(), MemberEvent.class, UnreachableMember.class, MemberUp.class);
	}
	
	@Override
	public void postStop(){
		log.info("un-subscribing from the cluster activity");
		clusterInfo.unsubscribe(self());
	}
	
	private MemDbActor() {
		map = new HashMap<>();
		receive(ReceiveBuilder.match(DbPutMessage.class, putMessage -> {
			log.info("_message=\"Recieved PUT msg\". key={} value={}", putMessage.getKey(),
					putMessage.getValue());
			map.put(putMessage.getKey(), putMessage.getValue());
			sender().tell(new DbOperationResultMessage(putMessage.getKey(), putMessage.getValue()), self());
		}).match(DbGetMessage.class, getMessage -> {
			log.info("_message=\"recieved GET msg\". key={} ", getMessage.getKey());
			String val = map.get(getMessage.getKey());
			sender().tell(val == null ? new Status.Failure(new MissingKeyException(getMessage.getKey()))
					: new DbOperationResultMessage(getMessage.getKey(), val), self());
		}).match(CurrentClusterState.class, z -> {
			CurrentClusterState state = (CurrentClusterState) z;
			for (Member member : state.getMembers()) {
				if (member.status().equals(MemberStatus.Up)) {
					// register with the request handler.
					registerWorkerToRequestHandler(member);
				}
			}
		}).matchAny(o -> {
			log.info("recieved msg unkown. classname={}", o.getClass().getName());
			sender().tell(new Status.Failure(new IllegalArgumentException("bad message")), self());
			unhandled(o);
		}).build());
	}

	private void registerWorkerToRequestHandler(Member member) {
		if(member.hasRole("reqHandler")){
			getContext().actorSelection(member.address() + ":2553" + "/user/reqHandler").tell(new DbWorkerJoin(), self());	
		}
	}
	
	public Map<String, String> getMap() {
		return map;
	}

}
