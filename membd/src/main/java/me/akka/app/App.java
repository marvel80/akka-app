package me.akka.app;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import me.akka.app.actor.MemDbActor;

public class App {
	public static void main(String[] args) {
		//get the default ports for initial cluster
		String[] startupNodes = {"2551" , "2552", "25520"};	// as defined in application.conf , 2551 and 2552 are the seed nodes.
		
		for(String port : startupNodes){
			//default config file is application.conf
			Config portConfig = ConfigFactory.parseString("akka.remote.artery.canonical.port=" + port).withFallback(ConfigFactory.load());
			
			ActorSystem mainActorSys = ActorSystem.create("memDb" , portConfig);
			mainActorSys.actorOf(Props.create(MemDbActor.class) , "memDbAct");	
		}
	}
}
