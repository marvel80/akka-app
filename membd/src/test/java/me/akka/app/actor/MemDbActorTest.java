package me.akka.app.actor;

import org.junit.Assert;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import me.akka.app.message.SetRequest;

public class MemDbActorTest {
	ActorSystem system = ActorSystem.create();
	
	@Test
	public void testPutOperation(){
		TestActorRef<MemDbActor> testActorRef = TestActorRef.create(system, Props.create(MemDbActor.class));
		testActorRef.tell(new SetRequest("key", "value") , ActorRef.noSender());
		MemDbActor testActor = testActorRef.underlyingActor();
		
		Assert.assertEquals(testActor.getMap().get("key"), "value");
	}

}
