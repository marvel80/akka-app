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
	
	/**
	 * a generic method to test actor behavior
	 * 
	 * @param movieName nameof movie to check
	 * @return	
	 */
	public CompletionStage<Object> whichPerformer(String movieName){
		Future<Object> scalaFuture = Patterns.ask(testActRef, movieName, 500);
		final CompletionStage<Object> compStage = FutureConverters.toJava(scalaFuture);
		return compStage;
	}
	
	/**
	 * this is a more asynchronous checker for actor where we block the thread by using Thread.sleep()
	 * @throws InterruptedException
	 */
	@Test
	public void logActor1() throws InterruptedException{
		System.out.println("\nlogActor-1 ");
		whichPerformer("Mission Impossible").thenAccept(x -> System.out.println("incoming message is : " + x)) ; 
		//.thenApply(y -> y.hashCode()).thenAccept(z -> System.out.println("hello" + z));
		
		Thread.sleep(502);
	}
	
	@Test
	public void logActor2() throws InterruptedException {
		// thenCompose tranf'r
		// demonstrates chained operation. since the second operation didn't get
		// correct movie, it should log error
		System.out.println("\nlogActor-2 ");
		whichPerformer("Mission Impossible").thenCompose(y -> whichPerformer(String.valueOf(y)));
	}
	
	@Test
	public void logActor3() throws InterruptedException {
		//handle error scenario. 
		//TODO check for explicit handler in java8.
		System.out.println("\nlogActor-3 ");
		whichPerformer("bad movie").handle((x, t) -> {
			if (t != null) {
				System.out.println("error occured. encountered bad value." + "\n" + t);
			}
			return null;
		});
	}
	
	@Test
	public void logActor4() throws InterruptedException {
		//handle error 2nd way
		System.out.println("\nlogActor-4 ");
		whichPerformer("bad movie").exceptionally(t -> {
			System.out.println("error handling 2");
			return "default";
		});
	}
	
	/**
	 * this is combination of logActor tests. all async functions are used, viz
	 * thenCompose and handle
	 */
	@Test
	public void chainedActorTest() {
		System.out.println("\nBEGIN : chainedActorTest");

		whichPerformer("Mission Impossible").thenCompose(x -> (whichPerformer("random"))).handle((x, ex) -> {
			if (ex != null) {
				System.out.println("eror hanling code is expected to trigger");
				return "default";
			}
			return x;
		});
		System.out.println("END : chainedActorTest \n ");
	}

}
