package logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import message.TimeStampedMessage;


public class Logger {
	private static volatile ArrayList<TimeStampedMessage> logRecords = new  ArrayList<TimeStampedMessage>();
	
	public static void main (String[] args) {
		try {
			ServerSocket server = new ServerSocket(6666);
            while(true) {
                Socket socket = server.accept();
                System.out.println("INFO: connect to " + socket.getRemoteSocketAddress());
                // open a new thread to listen messages coming from the other side
                new LoggerListenThread(socket).start();               
            }
        } catch (IOException e) {
            System.err.println("ERROR: server socket corrupt");
           // e.printStackTrace();
        }
	}
	
	public static void log (TimeStampedMessage TSMsg) {
		int i = 0;
		for (; i < logRecords.size(); i++) {
			if (logRecords.get(i).getTimeStamp().compareTo(TSMsg.getTimeStamp()) > 0) {
				logRecords.add(i, TSMsg);
			}
		}
		
		if (i == logRecords.size()) {
			logRecords.add(i, TSMsg);
		}
		System.out.println(TSMsg);
	}
}
