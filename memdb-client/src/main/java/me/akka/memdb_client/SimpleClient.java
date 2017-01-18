package me.akka.memdb_client;

import java.util.concurrent.CompletionStage;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import me.akka.app.message.DbGetMessage;
import me.akka.app.message.DbPutMessage;
import scala.compat.java8.FutureConverters;

public class SimpleClient {
	private final ActorSystem dbActorSys;
	private final ActorSelection db;
	
	public SimpleClient(){
		dbActorSys = ActorSystem.create("testerSystem");
		
		//pattern -> "akka://actorSystemName@host:port/user/actorName"
		db = dbActorSys.actorSelection("akka://memDb@127.0.0.1:25520/user/memDbAct");
	}
	
	public CompletionStage<Object> put(String key , String value){
		return FutureConverters.toJava(Patterns.ask(db, new DbPutMessage(key, value), 2500));
	}
	
	public CompletionStage<Object> get(String key){
		return FutureConverters.toJava(Patterns.ask(db,  new DbGetMessage(key), 2500));
	}
	
}
