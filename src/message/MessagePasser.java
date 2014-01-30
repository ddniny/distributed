package message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.yaml.snakeyaml.Yaml;

import record.Node;
import record.Rule;
import record.Rule.ACTION;
import thread.ListenerThread;
import thread.UserThread;
import util.Config;

public class MessagePasser {
	// instance to call by other classes
	private static volatile MessagePasser instance = null;

	// node and rules
	public HashMap<String, Node> nodeMap = null;
	public HashMap<String, ObjectOutputStream> outputStreamMap = null;
	public ArrayList<Rule> sendRules = null;
	public ArrayList<Rule> rcvRules = null;
	public Node myself = null;
	public int nodeNum = 0;   //~~~~~~~~used for construct the clock

	// queue and other data structure useful in communication
	// TODO replace Message with TimeStamped Message
	public ConcurrentLinkedQueue<Message> delayInMsgQueue;
	public ConcurrentLinkedQueue<Message> delayOutMsgQueue;
	public ConcurrentLinkedQueue<Message> rcvBuffer;
	public ArrayList<Message> receiveList;

	// set up an atomic counter for message id
	private AtomicInteger IDcounter;

	//file
	public long modified = 0;
	public String configFileName = null;
	public String localName = null;
	
	private ServerSocket server;
	
	//ClockService
	// TODO public ClockService clock;


	/** Constructor of MessagePasser, parse the configuration file
	 *  and build the initial connection
	 * 
	 * @param configuration_filename
	 * @param local_name
	 */
	@SuppressWarnings("unchecked")
	private MessagePasser(String configuration_filename, String local_name) {
		// parse the configuration file
		Yaml yaml = new Yaml();
		InputStream input = null;
		configFileName = configuration_filename;
		localName = local_name;
		try {
			File file = new File(configuration_filename);
			modified = file.lastModified(); //get the last modification time
			input = new FileInputStream(file);
			Map<String,  ArrayList<Map<String, Object>>> map = 
					(Map<String,  ArrayList<Map<String, Object>>>) yaml.load(input);
			nodeMap = Config.parseNodeMap(map.get("Configuration"));
			nodeNum = nodeMap.size(); //~~~~
			sendRules = Config.parseRules(map.get("SendRules"));
			rcvRules = Config.parseRules(map.get("ReceiveRules"));
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
		this.myself = nodeMap.get(local_name);
		IDcounter = new AtomicInteger(0);

		// initiate data structures
		outputStreamMap = new HashMap<String, ObjectOutputStream>();
		delayInMsgQueue = new ConcurrentLinkedQueue<Message>();
		delayOutMsgQueue = new ConcurrentLinkedQueue<Message>();
		rcvBuffer = new ConcurrentLinkedQueue<Message>();
		receiveList = new ArrayList<Message>();
		
		// TODO: using objectFactory create a clock
		// @param: the kind of clock
		// @param: nodeNum
		// clock = ClockServiceFactory.getClock();
	}

	/**
	 * get the singleton instance
	 * @return
	 */
	public static MessagePasser getInstance() {
		return instance;
	}

	/**
	 * Send the message to the other end
	 * @param message
	 * @throws IOException 
	 */
	public void send(Message message) throws IOException {
		message.set_seqNum(IDcounter.incrementAndGet());
		boolean duplicate = false;
		switch (matchSendRule(message)) {
		case DROP:
			System.out.println("INFO: Drop Message (Send) " + message);
			break;
		case DELAY:
			delayOutMsgQueue.add(message);
			break;
		case DUPLICATE:
			// no break, because at least one message should be sent
			duplicate = true;
		default:
			sendAway(message);    
			// send delayed message
			synchronized(delayOutMsgQueue) {
				while (!delayOutMsgQueue.isEmpty()) {
					sendAway(delayOutMsgQueue.poll());
				}
			}
			// send duplicated message if needed
			if (duplicate) {
				message.set_sendDuplicate(true);
				sendAway(message);
			}
		}
	}


	/**
	 * Send away message to specific destination
	 * @param message
	 */
	@SuppressWarnings("resource")
	private void sendAway(Message message) {
		ObjectOutputStream out;

		try {
			// build connection if not
			if (!outputStreamMap.containsKey(message.getDest())) {
				Node node = nodeMap.get(message.getDest());

				Socket socket = new Socket(node.getIpAddress(), node.getPort());
				out = new ObjectOutputStream(socket.getOutputStream());
				outputStreamMap.put(message.getDest(), out);

			} else {
				out = outputStreamMap.get(message.getDest());
			}

			// send message
			out.writeObject(message);
			out.flush();
			out.reset();
			System.out.println("INFO: send message " + message);

		} catch (IOException e) {
			System.err.println("ERROR: send message error, the other side may be offline " + message);
		}
	}

	/**
	 * Judge if match one send rule
	 * @param message
	 * @return return the action which is needed to be taken
	 * @throws IOException 
	 */
	private ACTION matchSendRule(Message message) throws IOException {
		checkModified();
		for (Rule rule : sendRules){
			if (rule.isMatch(message)) {
				return rule.getAction();
			}
		}
		return ACTION.DEFAULT;
	}

	/**
	 * Receive message from rcvBuffer
	 * @return
	 */
	//public ArrayList<Message> receive() {
	public Message receive() {
		
		synchronized (rcvBuffer) {
			while (!rcvBuffer.isEmpty()) {
				receiveList.add(rcvBuffer.poll());
			}
		}
		// TODO compare and increament timeStamp
//		for (TimeStampedMessage msg : receiveList) {
//			msg.getTimeStamp().
//		}
		if (receiveList.isEmpty()) return null;
		return receiveList.remove(0);
	}

	/**
	 * Check if the file is modified
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void checkModified () throws IOException { 
		File file = new File(configFileName);
		long lastModified = file.lastModified();
		int closeServerF = 0;
		if (lastModified > modified) {
		    System.out.println("INFO: Configuration file modified! Reload again!");
		    
			modified = lastModified;
			Yaml yaml = new Yaml();
			InputStream input;
			
			try {
				input = new FileInputStream(file);
				Map<String,  ArrayList<Map<String, Object>>> map = 
						(Map<String,  ArrayList<Map<String, Object>>>) yaml.load(input);
				HashMap<String, Node> newNodeMap = Config.parseNodeMap(map.get("Configuration"));
				ArrayList<Rule> newSendRules = Config.parseRules(map.get("SendRules"));
				ArrayList<Rule> newRcvRules = Config.parseRules(map.get("ReceiveRules"));
				this.myself = newNodeMap.get(localName);
				outputStreamMap.clear();
				
				closeServerF = nodeMap.get(localName).equals(newNodeMap.get(localName));
				for (Rule newRule : newSendRules) {
	                int matchIndex = -1;
				    if ((matchIndex = sendRules.indexOf(newRule)) != -1) {
				        newRule.setMatchedTimes(sendRules.get(matchIndex).getMatchedTimes());
				    }
				}

				for (Rule newRule : newRcvRules) {
	                int matchIndex = -1;
	                if ((matchIndex = rcvRules.indexOf(newRule)) != -1) {
                        newRule.setMatchedTimes(rcvRules.get(matchIndex).getMatchedTimes());
                    }
				}

				nodeMap = newNodeMap;
				sendRules = newSendRules;
				rcvRules = newRcvRules;
				if (closeServerF > 0){
				    server.close();
				    server = new ServerSocket(myself.getPort());
				    new ListenerThread(server).start();
				}
				
				input.close();
			} catch (FileNotFoundException e) {
				System.err.println("ERROR: The configuration file has been deleted!");
				e.printStackTrace();
				System.exit(-1);			
			}
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.out.println("Usage: configuration_filename local_name");
			System.exit(0);
		}    
		instance = new MessagePasser(args[0], args[1]);
		instance.server = new ServerSocket(instance.myself.getPort());
		// set up listener thread to build connection with other nodes
		new ListenerThread(instance.server).start();
		// set up user thread to receive user input
		new UserThread().start();
	}
}
