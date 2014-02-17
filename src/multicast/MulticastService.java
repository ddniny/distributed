package multicast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import clock.ClockService;
import clock.VectorClockService;
import message.Message;
import message.MessagePasser;
import message.TimeStampedMessage;
import thread.PairListenThread;
import timestamp.VectorTimeStamp;


public class MulticastService{

	//HashMap<String, ArrayList<String>> groupList;
	HashMap<String, VectorClockService> groupClocks; 
	ArrayList<TimeStampedMessage> receivedMessages;
	ArrayList<Message> holdbackQueue;
	MessagePasser mp = MessagePasser.getInstance();

	public MulticastService() throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		//super(configuration_filename, local_name);
		groupClocks = new HashMap<String, VectorClockService>();
		receivedMessages = new ArrayList<TimeStampedMessage>();
		holdbackQueue = new ArrayList<Message>();
		//int groupNum = getGroups().size();
		Set<String> nodeSet = mp.groups.keySet();
		for (String a : nodeSet) {
			VectorClockService groupClock = (VectorClockService) mp.csFactory.getClockService("VectorClock");
			groupClock.initialize(mp.processIndex, mp.nodeNum);
			groupClocks.put(a, groupClock);
		}     
	}

	public void bMulticast(String groupName, TimeStampedMessage message) throws IOException, CloneNotSupportedException {
		ArrayList<String> sendArrayList = mp.groups.get(groupName);
		if (!message.getKind().equals("mutexRequest") && !message.getKind().equals("releaseRequest")){
			if (message.get_source() == null) {    //TODO:��������
				message.set_source(mp.myself.getName());
				VectorClockService currentGroupClock = groupClocks.get(groupName);
				synchronized (mp.clock) {
					//TODO
					mp.clock.updateTimeStamp();
				}
				synchronized (currentGroupClock) {
					//TODO
					currentGroupClock.updateTimeStamp();
				}
				message.setTimeStamp(((VectorTimeStamp)currentGroupClock.getcurrentTimeStamp()).clone().getTimeStamp());//������multicast message����������group��timestamp send��������
				message.set_seqNum(mp.IDcounter.incrementAndGet());
			}
			message.setMulticast(true);
			message.setGroupName(groupName);
		}
		for (String a : sendArrayList) {
			//Message newMessage = new TimeStampedMessage(message);

			//((TimeStampedMessage)newMessage).setTimeStamp(currentTimeStamp);

			//			newMessage.set_dest(a);
			//			newMessage.setGroupName(groupName);
			//			newMessage.setMulticast(true);
			TimeStampedMessage newMessage = message.clone();
			newMessage.setDest(a);
			mp.send(newMessage);
		}
	}

	public void bDeliver(String groupName, TimeStampedMessage message) throws IOException, CloneNotSupportedException {
		synchronized (receivedMessages) {
			if (receivedMessages != null) {
				for (TimeStampedMessage tsMsg : receivedMessages) {
					if(tsMsg.get_source().equals(message.get_source()) && tsMsg.get_seqNumr() == message.get_seqNumr() && (tsMsg.get_sendDuplicate() == message.get_sendDuplicate())){
						return;
					}
				} 
			}
			receivedMessages.add(message);
			synchronized (holdbackQueue) {
				if (!message.get_source().equals(mp.localName)) {
					holdbackQueue.add(message);
					TimeStampedMessage newMessage = message.clone();
					newMessage.setMedium(mp.myself.getName());
					bMulticast(groupName, newMessage);
				}
			}
		}

		if (!message.get_source().equals(mp.localName)) {
			synchronized (holdbackQueue) {
				rDeliver(groupName, holdbackQueue);
			}
		} else {
			PairListenThread.receivePasser(message, mp);
		}
	}

	//TODO synchronized 
	public void rDeliver(String groupName, ArrayList<Message> holdbackQueue) throws IOException {	
		int sendFlag = 1;
		while (sendFlag > 0) {
			int size = holdbackQueue.size();
			sendFlag = 0;
			for (int i = 0; i < size; i++) {
				VectorClockService currentGroupClock = groupClocks.get(groupName);
				VectorTimeStamp currentTimeStamp = (VectorTimeStamp) groupClocks.get(groupName).getcurrentTimeStamp();
				int[] groupTime = (int[]) currentTimeStamp.getTimeStamp();
				TimeStampedMessage message = (TimeStampedMessage) holdbackQueue.get(i);
				int[] messageTime = (int[]) ((VectorTimeStamp)message.getTimeStamp()).getTimeStamp();
				int length = groupTime.length;
				String src = message.get_source();
				int index = mp.getNodeIndex(src);
				int flag = 0;
				if ((messageTime[index] - groupTime[index] == 1) || (messageTime[index] == groupTime[index] && message.get_sendDuplicate())) { //duplicate????
					for (int j = 0; j < length; j++) {
						if (j == index) {
							continue;
						} else {
							if (messageTime[j] > groupTime[j]) {  //TODO:����
								flag = 1;
								break;
							}
						}
					}
					if (flag == 0) {
						synchronized (currentGroupClock) {
							if (!message.get_sendDuplicate()) { 	//duplicate case
								currentGroupClock.updateGroupTimeStamp(index); //add the timeStamp on the sender's index position
								sendFlag++;
							}
						}
						synchronized (holdbackQueue) {
							holdbackQueue.remove(i);
							i--;
							size--;
						}	
						PairListenThread.receivePasser(message, mp);

					} 

				}

			}
		}

	}

}


