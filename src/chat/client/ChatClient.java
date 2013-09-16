package chat.client;

public interface ChatClient {

	public void shutdown();
	public void messageRecived(String message);

}
