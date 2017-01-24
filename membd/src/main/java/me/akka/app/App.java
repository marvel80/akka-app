package me.akka.app;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import me.akka.app.actor.MemDbActor;

public class App {
	public static void main(String[] args) throws InterruptedException {
		// get the default ports for initial cluster
		// as defined in application.conf , 2551 and 2552 are the seed nodes.
		String[] startupNodes = { "2551", "2552", "25520" };

		for (String port : startupNodes) {
			startUpActorSystem(port);
		}
		
		// sleep for 10 sec
		Thread.sleep(10000L);
		
		//start a new node and monitor the joining of cluster
		startUpActorSystem("2553");
		
	}
	
	private static void startUpActorSystem(String port){
		// default config file is application.conf . thats why .load() works
		// without argument / location of conf file.
		Config portConfig = ConfigFactory.parseString("akka.remote.artery.canonical.port=" + port)
				.withFallback(ConfigFactory.load("clustering"));

		ActorSystem mainActorSys = ActorSystem.create("memDb" , portConfig);
		mainActorSys.actorOf(Props.create(MemDbActor.class), "memDbAct");		
	}
}
