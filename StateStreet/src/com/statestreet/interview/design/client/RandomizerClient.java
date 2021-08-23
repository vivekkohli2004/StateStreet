package com.statestreet.interview.design.client;

import static com.statestreet.interview.design.util.AppConstants.IP_ADDRESS;
import static com.statestreet.interview.design.util.AppConstants.PORT;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.statestreet.interview.design.model.PrimeResult;
import com.statestreet.interview.design.queue.DistributedQueue;

/**
 * Client side class to take care of Socket communications with the Server. It
 * sends the requests as well as receives responses from the Server. Both
 * requests, responses are added & retrieved back respectively via the
 * {@link com.statestreet.interview.design.queue.DistributedQueue}.
 **/
public class RandomizerClient {

	private Socket clientSocket;
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;

	public RandomizerClient(DistributedQueue distributedQueue) {
		try {
			clientSocket = new Socket(IP_ADDRESS, PORT);
			dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
			dataInputStream = new DataInputStream(clientSocket.getInputStream());
			new Thread(new ListenerTask(distributedQueue)).start();
		} catch (IOException ioe) {
			System.out.println("Error in creating RandomizerClient." + ioe.getMessage());
			throw new RuntimeException("Error in creating RandomizerClient.");
			/**
			 * rethrowing Exception so that the application doesn't come up in case of
			 * connection issues.
			 */
		}
	}

	public void sendToServer(DistributedQueue inputQueue) {
		Integer elem = null;
		try {
			elem = inputQueue.removeRequest();
			dataOutputStream.writeInt(elem);
		} catch (IOException ioe) {
			System.out.printf("Error in sending Integer %d to Server %s \n", elem, ioe.getMessage());
		} catch (InterruptedException ie) {
			System.out.println("Error at the Client getting request from Blocking Queue." + ie.getMessage());
		}
	}

	/**
	 * this Thread listens for responses from Server. As this has to be a continuous
	 * process since the application boots up (& not something to be done at runtime
	 * WITH EVERY REQUEST), its not implemented as a normal public method to be
	 * invoked from the calling class Randomizer unlike the sendToServer() method
	 * above.
	 */
	class ListenerTask implements Runnable {
		private DistributedQueue distributedQueue;

		ListenerTask(DistributedQueue distributedQueue) {
			this.distributedQueue = distributedQueue;
		}

		@Override
		public void run() {
			while (true) {
				try {
					int result = dataInputStream.readInt();
					boolean resultPrime = dataInputStream.readBoolean();
					distributedQueue.addResult(new PrimeResult(result, resultPrime));
				} catch (IOException ioe) {
					System.out.printf("Error in getting resuts from the Server %s\n", ioe.getMessage());
				} catch (InterruptedException ie) {
					System.out
							.println("Error at the Client in adding Response to the Blocking Queue." + ie.getMessage());
				}
			}

		}
	}

}
