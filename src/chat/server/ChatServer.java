package chat.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {

	Map<String, ServerConnection> connections;

	public ChatServer() {
		try {
			InetAddress localMachine = InetAddress.getLocalHost();
			String address = localMachine.getCanonicalHostName();
			if(!address.contains("greta-pt.ecs.vuw.ac.nz")) { //DONE change string to greta-pt
				System.out.println("This can only be run on greta-pt");
				System.exit(1);
			}
		} catch (UnknownHostException e1) {
			//e1.printStackTrace();//DONE remove this after debug
			System.exit(1);
		}
		connections = new HashMap<String, ServerConnection>();
		new ServerListener(this);
	}

	public void newClient(String name, ServerConnection con) {
		if(connections.containsKey(name)) {
			con.sendMessage("You are logged in from another location");
			con.dropConnection();
			return;
		}
		connections.put(name, con);
		broadcastMessage(name+" has joined...");

		//log message
		Date now = new Date();
		String code = sdf.format(now);
		System.out.println(code+" - "+name+" has joined...");
		//log message
	}
	SimpleDateFormat sdf = new SimpleDateFormat("HH:MM:ss dd/MM/yyyy");
	public void messageRecived(String client, String message) {
		//log message
		Date now = new Date();
		String code = sdf.format(now);
		System.out.println(code+" - "+client+": "+message);
		//log message

		broadcastMessage(client+": "+message);
	}
	public void dropClient(String name) {
		connections.remove(name);
		broadcastMessage(name+" has left...");

		//log message
		Date now = new Date();
		String code = sdf.format(now);
		System.out.println(code+" - "+name+" has left...");
		//log message

	}

	private void broadcastMessage(String message) {
		for(Map.Entry<String, ServerConnection> e : connections.entrySet()) {
			e.getValue().sendMessage(message);
		}
	}

}
class ServerListener extends Thread {
	private ChatServer master;
	private ServerSocket serverSocket;
	private boolean running = true;
	public ServerListener(ChatServer server) {
		master = server;
		start();
	}
	public void run() {
		try {
			serverSocket = new ServerSocket(55231);
		} catch (IOException e) {
			//e.printStackTrace();//DONE remove this after debugging
			System.out.println("Error starting server...");
			System.exit(1);
		}
		while(running) {
			try {
				Socket socket = serverSocket.accept();
				if(running) {
					new ServerConnection(socket, master);
				}
			} catch (IOException e) {
				System.out.println("Failed Client Connect Attempt");
			}
		}
	}
}
