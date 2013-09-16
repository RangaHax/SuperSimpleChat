package chat.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;

import chat.client.ChatClient;
import chat.client.ClientConnection;

public class ChatGUI implements ChatClient {

	private JFrame frame;
	private JTextArea chatPane;
	private JPanel southPanel;
	private JTextField chatField;
	private JButton chatButton;

	public static final int HEIGHT = 500, WIDTH = 400;

	private boolean running = true;
	private ClientConnection client;

	public ChatGUI() {
		setupFrame();
		try {
			Socket socket = new Socket("greta-pt", 55231);//DONE change to greta-pt
			client = new ClientConnection(socket, this);
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "Could not find server", "Connection Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Could not connect to server", "Connection Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
			//e.printStackTrace();//DONE remove after debugging
		}
	}
	private void setupFrame() {
		frame = new JFrame();
		frame.setTitle("ECS Chat");
		int x = (int) ((java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2.0)-WIDTH/2);
		int y = (int) ((java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2.0)-HEIGHT/2-HEIGHT/8);
		frame.setBounds(x, y, WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		frame.setLayout(new BorderLayout());

		//display pane setup
		chatPane = new JTextArea();
		chatPane.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
		chatPane.setForeground(new Color(0,0,0));
		JScrollPane scroll = new JScrollPane(chatPane);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setAutoscrolls(true);
		frame.add(scroll, BorderLayout.CENTER);
		chatPane.setEditable(false);
		chatPane.setWrapStyleWord(true);
		chatPane.setLineWrap(true);

		southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());

		//input field setup
		chatField = new JTextField();
		chatField.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
		chatField.setForeground(new Color(0,0,0));
		southPanel.add(chatField, BorderLayout.CENTER);
		chatField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chat();
			}
		});

		//submit button setup
		chatButton = new JButton("Send");
		chatButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
		chatButton.setForeground(new Color(0,0,0));
		southPanel.add(chatButton, BorderLayout.EAST);
		chatButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chat();
			}
		});

		frame.add(southPanel, BorderLayout.SOUTH);
		frame.setVisible(true);
		chatField.requestFocus();
	}
	private void chat() {
		String text = chatField.getText();
		if(text.trim().equals("")) {
			chatField.setText("");
			chatField.requestFocus();
			return;
		}
		if(text.contains("#KILL#")) {
			chatField.setText("");
			chatField.requestFocus();
			return;
		}
		client.sendMessage(text);
		chatField.setText("");
		chatField.requestFocus();
	}

	@Override
	public void shutdown() {
		if(running) JOptionPane.showMessageDialog(null, "Server communication error", "Error", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}
	@Override
	public void messageRecived(String message) {
		if(chatPane.getText().equals("")) chatPane.setText(message);
		else chatPane.append("\n"+message);
		chatPane.setCaretPosition(chatPane.getText().length());
	}
}
