package es.ubu.lsi.client;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.HashSet;
import java.util.Set;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.common.ChatMessage.MessageType;

/**
 * Client.
 * 
 * @author http://www.dreamincode.net
 * @author Raúl Marticorena
 * @author Joaquin P. Seco
 *
 */
public class ChatClientImpl implements ChatClient {

	/** Input stream. */
	private ObjectInputStream sInput; // to read from the socket
	/** Output stream. */
	private ObjectOutputStream sOutput; // to write on the socket
	/** Socket. */
	private Socket socket;

	/** Server name/IP. */
	private String server;
	/** User name. */
	private String username;
	/** Port. */
	private int port;
	/** Users blocked by this client. */
	private Set<String> bannedUsers = new HashSet<>();

	/** Flag to keep running main thread. */
	private boolean carryOn = true;

	/** Id. */
	private int id;

	/**
	 * Constructor.
	 * 
	 * @param server   server
	 * @param port     port
	 * @param username user name
	 * 
	 */
	public ChatClientImpl(String server, int port, String username) {
		// which calls the common constructor with the GUI set to null
		this.server = server;
		this.port = port;
		this.username = username;
	}

	/**
	 * Starts chat.
	 * 
	 * @return true if everything goes right, false in other case
	 */
	@Override
	public boolean start() {
		// try to connect to the server
		try {
			socket = new Socket(server, port);
			String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
			display(msg);
			sInput = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());

		} catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		} catch (Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}
		// Login and receive id
		try {
			sOutput.writeObject(username);
			sOutput.flush();
			id = sInput.readInt();
		} catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		// creates the Thread to listen from the server
		new Thread(new ChatClientListener()).start();
		// success we inform the caller that it worked
		return true;
	}

	/**
	 * Displays messages.
	 * 
	 * @param msg text to show in console
	 */
	private void display(String msg) {
		System.out.println(msg); // println in console mode
	}

	/**
	 * Sends a message to the server.
	 * 
	 * @param msg message
	 */
	@Override
	public synchronized void sendMessage(ChatMessage msg) {
		try {
			if (this.carryOn) {
				sOutput.writeObject(msg);
				sOutput.flush();
			}
		} catch (IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	/**
	 * Disconnect client closing resources.
	 */
	@Override
	public void disconnect() {

		try {
			display("Trying to disconnect and close client with username " + username);
			if (sInput != null) {
				sInput.close();
				sInput = null;
			}
			if (sOutput != null) {
				sOutput.close();
				sOutput = null;
			}
			if (socket != null && !socket.isClosed()) {
				socket.close();
				socket = null;
			}
		} catch (Exception e) {

			display("Disconnect with error, closing resources, closed previously.");
		} finally {
			display("Bye!");
			carryOn = false;
		}
	}

	/**
	 * Bans a user in the current client.
	 * 
	 * @param bannedUser user to ban
	 */
	private void banUser(String bannedUser) {
		if (bannedUser == null || bannedUser.trim().isEmpty()) {
			display("Usage: ban <nickname>");
			return;
		}
		bannedUsers.add(bannedUser);
		display("Has bloqueado a " + bannedUser);
		sendMessage(new ChatMessage(id, MessageType.MESSAGE,
				username + " ha baneado a " + bannedUser));
	}

	/**
	 * Unbans a user in the current client.
	 * 
	 * @param unbannedUser user to unban
	 */
	private void unbanUser(String unbannedUser) {
		if (unbannedUser == null || unbannedUser.trim().isEmpty()) {
			display("Usage: unban <nickname>");
			return;
		}
		bannedUsers.remove(unbannedUser);
		display("Has desbloqueado a " + unbannedUser);
		sendMessage(new ChatMessage(id, MessageType.MESSAGE,
				username + " ha desbaneado a " + unbannedUser));
	}
	
	/**
	 * Checks if a received text belongs to a banned user.
	 * 
	 * @param text received text
	 * @return true if the message belongs to a banned user, false in other case
	 */
	private boolean isMessageFromBannedUser(String text) {
		if (text == null) {
			return false;
		}

		for (String bannedUser : bannedUsers) {
			if (text.contains(bannedUser + ":")) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Starts the client from console.
	 *
	 * Valid examples:
	 * {@code java es.ubu.lsi.client.ChatClientImpl juan}
	 * {@code java es.ubu.lsi.client.ChatClientImpl localhost juan}
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName;

		if (args.length == 1) {
			userName = args[0];
		} else if (args.length == 2) {
			serverAddress = args[0];
			userName = args[1];
		} else {
			System.err.println("Uso: java es.ubu.lsi.client.ChatClientImpl [servidor] nickname");
			return;
		}

		ChatClient client = new ChatClientImpl(serverAddress, portNumber, userName);

		if (!client.start()) {
			System.err.println("Error connecting server. Check network and server status.");
			return;
		}

		ChatClientImpl clientChat = ((ChatClientImpl) client);
		try (Scanner scan = new Scanner(System.in)) {
			while (clientChat.carryOn) {
				System.out.print("> ");
				String userMsg = scan.nextLine().trim();

				if (userMsg.equalsIgnoreCase(MessageType.LOGOUT.toString())) {
					client.sendMessage(
							new ChatMessage(clientChat.id, MessageType.LOGOUT, MessageType.LOGOUT.toString()));
					break;

				} else if (userMsg.equalsIgnoreCase(MessageType.SHUTDOWN.toString())) {
					client.sendMessage(
							new ChatMessage(clientChat.id, MessageType.SHUTDOWN, MessageType.SHUTDOWN.toString()));
					break;

				} else if (userMsg.toLowerCase().startsWith("ban ")) {
					String bannedUser = userMsg.substring(4).trim();
					clientChat.banUser(bannedUser);

				} else if (userMsg.toLowerCase().startsWith("unban ")) {
					String unbannedUser = userMsg.substring(6).trim();
					clientChat.unbanUser(unbannedUser);

				} else {
					client.sendMessage(new ChatMessage(clientChat.id, MessageType.MESSAGE, userMsg));
				}
				System.out.println();
			}
		}

		client.disconnect();
	}

	/**
	 * Client listener for messages from server.
	 * 
	 */
	class ChatClientListener implements Runnable {

		/**
		 * Run.
		 */
		public void run() {
			while (true) {
				try {
					ChatMessage msg = (ChatMessage) sInput.readObject();

					if (msg.getId() != id && !isMessageFromBannedUser(msg.getMessage())) {
						System.out.println(msg.getMessage());
						System.out.print("\n> ");
					}

				} catch (IOException e) {
					display("Server has closed the connection.");
					carryOn = false;
					break;
				} catch (ClassNotFoundException e2) {
					throw new RuntimeException("Wrong message type", e2);
				}
			}
		}
	} // ChatClientListener

} // ChatClient