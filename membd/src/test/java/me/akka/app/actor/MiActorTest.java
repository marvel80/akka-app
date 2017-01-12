package me.akka.app.actor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;

public class MiActorTest {
	//heavy, so recommended to create one per application
	ActorSystem testSystem = ActorSystem.create();
	
	ActorRef testActRef = testSystem.actorOf(Props.create(MiActor.class));
	
	@Test
	public void testMiActorForMI() throws InterruptedException, ExecutionException, TimeoutException{
		Future<Object> scalaFuture = Patterns.ask(testActRef, "Mission Impossible", 500);
		final CompletionStage<Object> compStg = FutureConverters.toJava(scalaFuture);
		final CompletableFuture<Object> javaFuture = (CompletableFuture<Object>) compStg;
		
		Assert.assertEquals(String.valueOf(javaFuture.get(500, TimeUnit.MILLISECONDS)), "Tom Cruise");
	}
	
	@Test(expected = Exception.class)
	public void testMiActorForStarWars() throws InterruptedException, ExecutionException, TimeoutException{
		Future<Object> scalaFuture = Patterns.ask(testActRef, "Star Wars", 500);
		final CompletionStage<Object> compStg = FutureConverters.toJava(scalaFuture);
		final CompletableFuture<Object> javaFuture = (CompletableFuture<Object>) compStg;
		
		javaFuture.get(500, TimeUnit.MILLISECONDS);
	}

}
