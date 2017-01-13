package me.akka.app;

import akka.actor.ActorSystem;
import akka.actor.Props;
import me.akka.app.actor.MemDbActor;

public class App {
	public static void main(String[] args) {
		ActorSystem mainActorSys = ActorSystem.create("memDb");
		mainActorSys.actorOf(Props.create(MemDbActor.class) , "memDbAct");
		
	}
}
