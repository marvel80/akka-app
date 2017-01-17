package me.akka.memdb_client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for Simple Client.
 */
public class SimpleClientTest {

	SimpleClient testClient = new SimpleClient("127.0.0.1:25522");

	@Test
	public void testPositiveFlow() throws InterruptedException, ExecutionException {
		// put
		testClient.put("key1", "value1");

		// test get on above put
		String testVal = String.valueOf(((CompletableFuture<Object>) testClient.get("key1")).get());
		Assert.assertEquals("value1", testVal);
	}

}
