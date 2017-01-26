package me.akka.app.actor;

import java.util.concurrent.CompletionStage;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.testkit.TestActorRef;
import me.akka.app.message.DbGetMessage;
import me.akka.app.message.DbMessage;
import me.akka.app.message.DbPutMessage;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;

public class MemDbActorTest {
	Logger log = LoggerFactory.getLogger(MemDbActorTest.class);
	
	ActorSystem system = ActorSystem.create();
	TestActorRef<MemDbActor> testActorRef = TestActorRef.create(system, Props.create(MemDbActor.class));
	
	@Test
	public void testPutOperation(){
		log.info("Test Put operation");
		testActorRef.tell(new DbPutMessage("key", "value") , ActorRef.noSender());
		testActorRef.tell(new DbPutMessage("key2", "value2") , ActorRef.noSender());
		
		MemDbActor testActor = testActorRef.underlyingActor();
		
		Assert.assertEquals(testActor.getMap().size(), 2);
		Assert.assertEquals(testActor.getMap().get("key"), "value");
		Assert.assertEquals(testActor.getMap().get("key2"), "value2");
	}
	
	@Test
	public void testGetOperation(){
		log.info("Test Get operation");
		testActorRef.tell(new DbPutMessage("key3", "value3") , ActorRef.noSender());
		
		whichOperation(new DbGetMessage("key3")).handle((x ,ex) -> {
			if(ex != null){
				log.error("FAILED : testGetOperation");
				//Assert.fail();
			}
			
			//Assert.assertEquals(x.toString(), "value3");
			return null;
		});		
	}
	
	@Test
	public void testPutInvalidKeyFormat() {
		log.info("Test GET with invalid key ");
		whichOperation(new DbMessage() {

			@Override
			public String getKey() {
				return "NEW_KEY";
			}
		}).handle((x, ex) -> {
			if (ex != null) {
				// Assert.assertNotNull(ex);
				return null;
			}
			log.error("FAILED : testPutInvalidKeyFormat");
			// Assert.fail();
			return null;
		});
	}
	
	/**
	 * a generic method to test actor behavior
	 * 
	 * @param message
	 * @return	
	 */
	public CompletionStage<Object> whichOperation(DbMessage message){
		Future<Object> scalaFuture = Patterns.ask(testActorRef, message, 500);
		final CompletionStage<Object> compStage = FutureConverters.toJava(scalaFuture);
		return compStage;
	}

}
