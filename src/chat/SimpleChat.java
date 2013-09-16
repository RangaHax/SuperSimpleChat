package chat;

import java.net.InetAddress;
import java.net.UnknownHostException;

import chat.gui.ChatGUI;
import chat.server.ChatServer;

public class SimpleChat {
	public static void main(String[] args) {

		try {
			InetAddress localMachine = InetAddress.getLocalHost();
			String address = localMachine.getCanonicalHostName();
			if(!address.contains("ecs.vuw.ac.nz")) { //
				System.out.println("This can only be run on ecs machines");
				System.exit(1);
			}
		} catch (UnknownHostException e1) {
			//e1.printStackTrace();
			System.out.println("Unknown Error");
			System.exit(1);
		}

		if(args.length==0) new ChatGUI();
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
