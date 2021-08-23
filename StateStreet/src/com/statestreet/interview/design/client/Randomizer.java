package com.statestreet.interview.design.client;

import static com.statestreet.interview.design.util.AppConstants.NO_OF_THREADS_CLIENT_CONSUMER;
import static com.statestreet.interview.design.util.AppConstants.NO_OF_THREADS_CLIENT_PUBLISHER;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.statestreet.interview.design.model.PrimeResult;
import com.statestreet.interview.design.queue.DistributedQueue;

/**
 * Main client class that starts 2 threads- one to publish random nos to Server
 * & other to get responses from the Server over a Java Socket connection.
 */
public class Randomizer {

	private DistributedQueue distributedQueue;

	private RandomizerClient randomizerClient;

	public Randomizer() {
		distributedQueue = new DistributedQueue();
		randomizerClient = new RandomizerClient(distributedQueue); // the client needs a container Data Structure to
																	// save the responses into.

		ExecutorService generatorService = Executors.newFixedThreadPool(NO_OF_THREADS_CLIENT_PUBLISHER);
		generatorService.submit(generatorTask);
		ExecutorService receiverService = Executors.newFixedThreadPool(NO_OF_THREADS_CLIENT_CONSUMER);
		receiverService.submit(receiverTask);
	}

	Runnable generatorTask = () -> {
		Random random = new Random();
		while (true) {
			int randomInt = Math.abs(random.nextInt());
			try {
				distributedQueue.addRequest(randomInt);
			} catch (InterruptedException ie) {
				System.out.println("Error at the Client in adding Request to the Blocking Queue" + ie.getMessage());
			}
			randomizerClient.sendToServer(distributedQueue);
		}
	};

	Runnable receiverTask = () -> {
		while (true) {
			PrimeResult result;
			try {
				result = distributedQueue.removeResult();
				if (result.isPrime()) {
					System.out.printf("The number %d is Prime.\n", result.getNumber());
				} else {
					System.out.printf("The number %d is not Prime.\n", result.getNumber());
				}
			} catch (InterruptedException ie) {
				System.out.println("Error at the Client in removing result from the Blocking Queue" + ie.getMessage());
			}
		}
	};

	public static void main(String[] args) {
		new Randomizer();
	}

}