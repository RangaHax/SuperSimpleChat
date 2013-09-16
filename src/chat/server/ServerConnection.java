package chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import chat.Encryption;

public class ServerConnection {

	private String clientName;
	private boolean running = false;
	private ChatServer master;

	private BufferedReader input;
	private PrintWriter output;
	private Socket socket;

	public ServerConnection(Socket s, ChatServer server) {
		try {
			socket = s;
			master = server;
			input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			output = new PrintWriter(s.getOutputStream());
			clientName = Encryption.decrypt(input.readLine());
			if(clientName == null) {
				try {
					input.close();
					output.close();
					socket.close();
				} catch (IOException e) {
					//e.printStackTrace();//DONE remove this after debugging
				}
				return;
			}
			new ServerConListener(input, this);
			master.newClient(clientName, this);
		} catch (IOException e) {
			//e.printStackTrace();//DONE remove this after debugging
		}
	}
	public void sendMessage(String message) {
		if(message.equals(ServerConListener.KILL_MESSAGE)) running = false;
		output.println(Encryption.encrypt(message));
		output.flush();
	}
	public void reciveMessage(String message) {
		if(message.equals(ServerConListener.KILL_MESSAGE)) {
			if(running) {
				sendMessage(ServerConListener.KILL_MESSAGE);
			}
			try {
				input.close();
				output.close();
				socket.close();
			} catch (IOException e) {
				//e.printStackTrace();//DONE remove this after debugging
			}
			if(!dropped) master.dropClient(clientName);
			return;
		}
		master.messageRecived(clientName, message);
	}

	private boolean dropped = false;
	public void dropConnection() {
		dropped = true;
		running = false;
		sendMessage(ServerConListener.KILL_MESSAGE);
	}
}
class ServerConListener extends Thread {
	private BufferedReader scan;
	private String lastMessage = "";
	private ServerConnection master;

	public static final String KILL_MESSAGE = "#KILL#";
	public ServerConListener(BufferedReader input, ServerConnection con) {
		scan = input;
		master = con;
		start();
	}
	public void run() {
		while(!lastMessage.equals(KILL_MESSAGE)) {
			boolean encryptionFailure = false;
			try {
				String s = scan.readLine();
				if(s == null) lastMessage = KILL_MESSAGE;
				else s = Encryption.decrypt(s);
				if(s != null) {
					lastMessage = s;
				}
				else {
					encryptionFailure = true;
				}
			} catch (IOException e) {
				lastMessage = KILL_MESSAGE;
				//e.printStackTrace(); //DONE remove this after debugging
			}
			if(!encryptionFailure) master.reciveMessage(lastMessage);
		}
	}
}
