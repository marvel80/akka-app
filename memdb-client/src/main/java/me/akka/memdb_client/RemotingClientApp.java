package me.akka.memdb_client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Kill;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.routing.RoundRobinPool;
import me.akka.app.message.DbGetMessage;
import me.akka.app.message.DbPutMessage;
import me.akka.memdb_client.actor.PublisherActor;
import me.akka.memdb_client.actor.SimpleRemotingActor;
import scala.compat.java8.FutureConverters;

public class RemotingClientApp {

	private static ActorSystem remoteSystem;
	private static ActorRef clientActor;
	private static final String REMOTE_DB_ACTOR_PATH = "akka://memDb@127.0.0.1:2553/user/reqHandler";
	
	private static final Logger log = LoggerFactory.getLogger(RemotingClientApp.class); 

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		remoteSystem = ActorSystem.create("memDb", ConfigFactory.load("client"));
		clientActor = remoteSystem.actorOf(Props.create(SimpleRemotingActor.class, 
				REMOTE_DB_ACTOR_PATH).withRouter(new RoundRobinPool(2)), "client");
		//clientActor = remoteSystem.actorOf(Props.create(PublisherActor.class), "clientDistributed");
		
		// put operation
		clientActor.tell(new DbPutMessage("key", "value"), clientActor);

		/*// get operation
		CompletionStage<Object> valueFuture = FutureConverters
				.toJava(Patterns.ask(clientActor, new DbGetMessage("key"), 13000));
		String returnValue = String.valueOf(((CompletableFuture<Object>) valueFuture).get());

		// return value
		log.info("return _value={}" , returnValue);*/
		
		clientActor.tell(new DbGetMessage("key"), clientActor);
		clientActor.tell(new DbGetMessage("key1"), clientActor);
		
		clientActor.tell(new DbPutMessage("key2", "value2"), clientActor);
		
		// clientActor.tell(Kill.getInstance(), clientActor.noSender());
	}

}
