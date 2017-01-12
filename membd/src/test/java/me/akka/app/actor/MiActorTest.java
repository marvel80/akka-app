package me.akka.app.actor;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;

public class MiActorTest {
	ActorSystem testSystem = ActorSystem.create();
	
	@Test
	public void testMiActorForMI(){
		TestActorRef<MiActor> tstActorRef = TestActorRef.create(testSystem, Props.create(MiActor.class));
		MiActor actr = tstActorRef.underlyingActor();
		
		//tstActorRef.tell("Mission Impossible", ActorRef.noSender());
		tstActorRef.receive("Mission Impossible", ActorRef.noSender());
		
		
	}

}
