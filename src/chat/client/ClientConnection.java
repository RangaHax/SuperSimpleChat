package chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import chat.Encryption;

public class ClientConnection {

	private String clientName;
	private boolean running = false;
	private ChatClient master;

	private BufferedReader input;
	private PrintWriter output;
	private Socket socket;

	public ClientConnection(Socket s, ChatClient client) {
		try {
			socket = s;
			master = client;
			input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			output = new PrintWriter(s.getOutputStream());
			try {
				Runtime rt = Runtime.getRuntime();
				Process pr = rt.exec("finger " + System.getProperty("user.name"));
				BufferedReader sysInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				String line = sysInput.readLine();
				sysInput.close();
				int i = line.indexOf("Name: ");
				clientName = line.substring(i + 6);
			} catch (IOException e) {
				sendMessage(ClientConListener.KILL_MESSAGE);
				e.printStackTrace();
				System.exit(1);
			}
			output.println(Encryption.encrypt(clientName));
			output.flush();
			new ClientConListener(input, this);
		} catch (IOException e) {
			//e.printStackTrace();//DONE remove this after debugging
		}
	}
	public void sendMessage(String message) {
		if(message.equals(ClientConListener.KILL_MESSAGE)) running = false;
		output.println(Encryption.encrypt(message));
		output.flush();
		if(output.checkError()) master.shutdown();
	}
	public void reciveMessage(String message) {
		if(message.equals(ClientConListener.KILL_MESSAGE)) {
			if(running) {
				sendMessage(ClientConListener.KILL_MESSAGE);
			}
			try {
				input.close();
				output.close();
				socket.close();
			} catch (IOException e) {
				//e.printStackTrace();//DONE remove this after debugging
			}
			master.shutdown();
			return;
		}
		master.messageRecived(message);
	}
}
class ClientConListener extends Thread {
	private BufferedReader scan;
	private String lastMessage = "";
	private ClientConnection master;

	public static final String KILL_MESSAGE = "#KILL#";
	public ClientConListener(BufferedReader input, ClientConnection con) {
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
