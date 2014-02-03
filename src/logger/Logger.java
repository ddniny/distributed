package logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import record.Node;
import util.Config;
import message.MessagePasser;
import message.TimeStampedMessage;


public class Logger {
	private static volatile Logger instance = null;
	private static volatile ArrayList<TimeStampedMessage> logRecords = new  ArrayList<TimeStampedMessage>();
	public HashMap<String, ObjectOutputStream> outputStreamMap = null;
	public HashMap<String, Node> nodeMap = null;
	public Node myself = null;
	public String configFileName = null;
	public String localName = null;
	public long modified = 0;
	public static String logPath = "";

	public static Logger getInstance() {
		return instance;
	}
	public static void main (String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: configuration_filename local_name path_to_dump_logFile");
			System.exit(0);
		} 
		new LoggerUserThread().start();
		instance = new Logger();
		instance.parseTheConfigFile(args[0], args[1]);
		logPath = args[2];
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
		}
	}

	public static void log (TimeStampedMessage TSMsg) {
		int i = 0;
		synchronized (logRecords) {
			for (; i < logRecords.size(); i++) {
				if (logRecords.get(i).getTimeStamp().compareTo(TSMsg.getTimeStamp()) > 0) {
					logRecords.add(i, TSMsg);
				}
			}

			if (i == logRecords.size()) {
				logRecords.add(i, TSMsg);
			}
		}
		//System.out.println(TSMsg);
	}

	public void writeLogToFile () {
		TimeStampedMessage curMessage = null;
		TimeStampedMessage  preMessage = null;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String path = logPath + sdf.format(date) + " log.txt";
		try {   
			FileWriter fw=new FileWriter(path,true);   
			BufferedWriter bw=new BufferedWriter(fw);   
			for (int i = 0; i < logRecords.size(); i++) {
				curMessage = logRecords.get(i);
				if (preMessage != null && preMessage.getTimeStamp().compareTo(curMessage.getTimeStamp()) == 0) {
					bw.write("||");
					bw.newLine();
				} else {
					if (preMessage != null) {
						bw.write("->");
						bw.newLine();
					}
				}
				preMessage = curMessage;
				bw.write(curMessage.toString());
			}
			bw.close();  
			fw.close();   
		} catch (IOException e) {   
			// TODO Auto-generated catch block   
			e.printStackTrace();   
		} 
	}

	@SuppressWarnings("unchecked")
	public void parseTheConfigFile(String configuration_filename, String loggerName) {
		Yaml yaml = new Yaml();
		InputStream input = null;
		configFileName = configuration_filename;
		localName = loggerName;
		try {
			File file = new File(configuration_filename);
			modified = file.lastModified(); //get the last modification time
			input = new FileInputStream(file);
			Map<String,  ArrayList<Map<String, Object>>> map = 
					(Map<String,  ArrayList<Map<String, Object>>>) yaml.load(input);
			nodeMap = Config.parseNodeMap(map.get("Configuration"));
		} catch (FileNotFoundException e) {
			System.err.println("ERROR: Cannot find the configuration file!");
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.myself = nodeMap.get(loggerName);

		outputStreamMap = new HashMap<String, ObjectOutputStream>();
	}

	public void send(String sendTo) {
		synchronized(logRecords) {
			for (TimeStampedMessage msg : logRecords) {
				sendAway(msg, sendTo);
			}
		}
		System.out.println("Finish sending the log to " + sendTo);
	}

	private void sendAway(TimeStampedMessage message, String sendTo) {
		ObjectOutputStream out;
		try {
			// build connection if not
			if (!outputStreamMap.containsKey(sendTo)) {
				Node node = nodeMap.get(sendTo);

				Socket socket = new Socket(node.getIpAddress(), node.getPort());
				out = new ObjectOutputStream(socket.getOutputStream());
				outputStreamMap.put(sendTo, out);

			} else {
				out = outputStreamMap.get(sendTo);
			}

			// send message
			out.writeObject(message);
			out.flush();
			out.reset();
			//System.out.println("INFO: send message " + message);

		} catch (IOException e) {
			System.err.println("ERROR: send message error, the other side may be offline " + message);
		}
	}
}
