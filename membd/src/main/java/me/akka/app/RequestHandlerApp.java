package me.akka.app;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import me.akka.app.actor.RequestHandler;

public class RequestHandlerApp {
	public static void main(String[] args) throws InterruptedException {
		// get the default ports for initial cluster
		// as defined in application.conf , 2551 and 2552 are the seed nodes.
		String[] startupNodes = args ;

		for (String port : startupNodes) {
			startUpActorSystem(port);
		}
	}
	
	private static void startUpActorSystem(String port){
		// default config file is application.conf . thats why .load() works
		// without argument / location of conf file.
		Config portConfig = ConfigFactory.parseString("akka.remote.artery.canonical.port=" + port)
				.withFallback(ConfigFactory.parseString("akka.cluster.roles = [reqHandler]"))
				.withFallback(ConfigFactory.load("clustering"));

		ActorSystem mainActorSys = ActorSystem.create("memDb" , portConfig);
		mainActorSys.actorOf(Props.create(RequestHandler.class).withRouter(new RoundRobinPool(1)), "reqHandler");		
		
		
	}
}
