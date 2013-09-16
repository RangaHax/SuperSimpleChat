package chat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import chat.client.ClientConnection;
import chat.gui.ChatGUI;
import chat.server.ChatServer;

public class SimpleChat {
	public static void main(String[] args) {

		try {
			InetAddress localMachine = InetAddress.getLocalHost();
			String address = localMachine.getCanonicalHostName();
			if(!address.contains("ecs.vuw.ac.nz")) {
				System.out.println("This can only be run on ecs machines");
				System.exit(1);
			}
		} catch (UnknownHostException e1) {
			//e1.printStackTrace();
			System.out.println("Unknown Error");
			System.exit(1);
		}

		if(args.length==0) {
			try {
				Socket socket = new Socket("greta-pt", 55231);//DONE change to greta-pt
				new ChatGUI(socket);
			} catch (UnknownHostException e) {
				JOptionPane.showMessageDialog(null, "Could not find server", "Connection Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Could not connect to server", "Connection Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
				//e.printStackTrace();//DONE remove after debugging
			}
		}
		else if(args.length ==1) {
			if(args[0].equals("secretserver")) {
				new ChatServer();
			}
			else {
				System.out.println("Error, this program takes no arguments");
				System.exit(1);
			}
		}
		else {
			System.out.println("Error, this program takes no arguments");
			System.exit(1);
		}
	}
}
