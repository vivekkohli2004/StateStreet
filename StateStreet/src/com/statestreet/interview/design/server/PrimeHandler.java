package com.statestreet.interview.design.server;

import static com.statestreet.interview.design.util.AppConstants.NO_OF_THREADS_SERVER_CONSUMER;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.statestreet.interview.design.model.PrimeResult;
import com.statestreet.interview.design.queue.DistributedQueue;
import com.statestreet.interview.design.util.PrimeHelper;

/**
 * This main class is Server side equivalent of the Client class
 * {@link com.statestreet.interview.design.client.Randomizer}.
 * 
 * It extracts the requests from the wrapper Queue
 * {@link com.statestreet.interview.design.queue.DistributedQueue} & delegates
 * the "is-Prime" determination to a helper class. The response is then added
 * back to Queue.
 */

public class PrimeHandler {

	private DistributedQueue distributedQueue;

	private PrimeServer primeServer;

	public PrimeHandler() {
		distributedQueue = new DistributedQueue();
		/*
		 * the server needs a container Data Structure to save the requests got from
		 * Client.
		 */
		primeServer = new PrimeServer(distributedQueue);
		ExecutorService executorService = Executors.newFixedThreadPool(NO_OF_THREADS_SERVER_CONSUMER);
		executorService.submit(handlerTask);
	}

	Runnable handlerTask = () -> {
		while (true) {
			int requestNum;
			try {
				requestNum = distributedQueue.removeRequest();
				boolean result = PrimeHelper.isPrime(requestNum);
				distributedQueue.addResult(new PrimeResult(requestNum, result));
				primeServer.sendResponseToClient(distributedQueue);
			} catch (InterruptedException ie) {
				System.out.println("Error at the Server getting the request from Blocking Queue" + ie.getMessage());
			}
		}
	};

	public static void main(String[] args) {
		new PrimeHandler();
	}
}
