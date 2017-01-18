package me.akka.memdb_client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import me.akka.app.message.DbGetMessage;
import me.akka.app.message.DbPutMessage;
import me.akka.memdb_client.actor.SimpleRemotingActor;
import scala.compat.java8.FutureConverters;

public class RemotingClientApp {

	private static ActorSystem remoteSystem;
	private static ActorRef clientActor;
	private static final String REMOTE_DB_ACTOR_PATH = "akka://memDb@127.0.0.1:25520/user/memDbAct";
	
	private static final Logger log = LoggerFactory.getLogger(RemotingClientApp.class); 

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		remoteSystem = ActorSystem.create("remotingClient", ConfigFactory.load("client"));
		clientActor = remoteSystem.actorOf(Props.create(SimpleRemotingActor.class, REMOTE_DB_ACTOR_PATH), "client");

		// put operation
		clientActor.tell(new DbPutMessage("key", "value"), clientActor);

		/*// get operation
		CompletionStage<Object> valueFuture = FutureConverters
				.toJava(Patterns.ask(clientActor, new DbGetMessage("key"), 13000));
		String returnValue = String.valueOf(((CompletableFuture<Object>) valueFuture).get());

		// return value
		log.info("return _value={}" , returnValue);*/
		
		clientActor.tell(new DbGetMessage("key"), clientActor);
	}

}
