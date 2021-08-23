package com.statestreet.interview.design.server;

import static com.statestreet.interview.design.util.AppConstants.PORT;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.statestreet.interview.design.model.PrimeResult;
import com.statestreet.interview.design.queue.DistributedQueue;

/**
 * This class is Server side equivalent of the Client class
 * {@link com.statestreet.interview.design.client.RandomizerClient}.
 * 
 * It listens for Client requests on a Socket & responses back too on it. Both
 * requests, responses are retrieved, added back via the
 * {@link com.statestreet.interview.design.queue.DistributedQueue}.
 */
public class PrimeServer {

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;

	public PrimeServer(DistributedQueue distributedQueue) {
		try {
			serverSocket = new ServerSocket(PORT);
			clientSocket = serverSocket.accept();// wait for the connections from the Client.
			dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
			dataInputStream = new DataInputStream(clientSocket.getInputStream());
			new Thread(new ListenerTask(distributedQueue)).start();
		} catch (IOException ioe) {
			System.out.println("Error in creating PrimeServer" + ioe.getMessage());
			throw new RuntimeException("Error in creating PrimeServer");
			/**
			 * rethrowing Exception so that the application doesn't come up in case of
			 * connection issues.
			 */
		}
	}

	public void sendResponseToClient(DistributedQueue distributedQueue) {
		try {
			PrimeResult primeResult = distributedQueue.removeResult();
			dataOutputStream.writeInt(primeResult.getNumber());
			dataOutputStream.writeBoolean(primeResult.isPrime());
		} catch (IOException ioe) {
			System.out.printf("Error in sending response to the Client %s\n", ioe.getMessage());
		} catch (InterruptedException ie) {
			System.out.printf("Error at Server while removing an element from the Blocking Queue %s\n",
					ie.getMessage());
		}
	}

	/**
	 * this Thread listens for requests from Client. As the listening needs to be
	 * continuous, its started in constructor & not invoked via the client class
	 * PrimeHandler unlike the sendResponseToClient() method above.
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
					int requestNum = dataInputStream.readInt();
					System.out.printf("Received request for number %d from the Client\n", requestNum);
					distributedQueue.addRequest(requestNum);
				} catch (IOException ioe) {
					System.out.printf("Error in getting request from the Client %s\n", ioe.getMessage());
				} catch (InterruptedException ie) {
					System.out.printf("Error at Server while adding element to the Blocking Queue %s\n",
							ie.getMessage());
				}

			}
		}
	}
}
