package com.statestreet.interview.design.queue;

import static com.statestreet.interview.design.util.AppConstants.QUEUE_MAX_SIZE;

import java.util.concurrent.ArrayBlockingQueue;

import com.statestreet.interview.design.model.PrimeResult;

/**
 * This is a wrapper over conventional Java BlockingQueues & encapsulates 2 of
 * them- one containing requests & the other responses. One instance each of
 * this class is used at the Client & Server side. The outside classes only use
 * this class for any underlying Queue operations.
 */
public class DistributedQueue {

	private ArrayBlockingQueue<Integer> inputQueue;
	private ArrayBlockingQueue<PrimeResult> outPutQueue;

	public DistributedQueue() {
		inputQueue = new ArrayBlockingQueue<Integer>(QUEUE_MAX_SIZE);

		/**
		 * creating an ArryBlockingQueue with max size of 1000 elements right at onset
		 * as 1000 shouldn't be a big threshold.
		 */
		outPutQueue = new ArrayBlockingQueue<PrimeResult>(QUEUE_MAX_SIZE);
	}

	public void addRequest(Integer elem) throws InterruptedException {
		inputQueue.put(elem); // blocking call
	}

	public Integer removeRequest() throws InterruptedException {
		return inputQueue.take();// blocking call
	}

	public PrimeResult removeResult() throws InterruptedException {
		return outPutQueue.take();// blocking call
	}

	public void addResult(PrimeResult primeResult) throws InterruptedException {
		outPutQueue.put(primeResult);// blocking call
	}

}
