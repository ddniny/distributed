package logger;

import java.io.ObjectInputStream;
import java.net.Socket;
import message.TimeStampedMessage;

public class LoggerListenThread extends Thread {
	private Socket socket = null;

	public LoggerListenThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			while(true) {
				TimeStampedMessage message = (TimeStampedMessage)in.readObject();
				if (message.getKind().equals("Retrieve")) {
					Logger.getInstance().send(message.get_source());
				} else {
					Logger.log(message);
				}
			}
		} catch (Exception e) {
			System.err.println("ERROR: LoggerListenThread corrupt");
			e.printStackTrace();
		}
	}
}
