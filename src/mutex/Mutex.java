package mutex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import message.Message;
import message.MessagePasser;
import message.TimeStampedMessage;


public class Mutex {
	public enum State {HELD, WANTED, RELEASED};
	private State state;
	private boolean vote;	
	private static Mutex mutex = null;
	private MessagePasser passer;
	private ArrayList<TimeStampedMessage> requests;
	private Set<String> groupMember;
	private Set<String> voteGet;
	private TimeStampedMessage preReleasedMessage;

	public Mutex() {
		this.state = State.RELEASED;
		this.vote = false;
		passer = MessagePasser.getInstance();
		requests = new ArrayList<TimeStampedMessage>();
		voteGet = new HashSet<String>();
		groupMember = new HashSet<String>();

		// Add all of the other members in the group[i] that this node in to a HashSet groupMember
		ArrayList<String> groupIn = passer.myself.getMemberOf();
		
		for (String group : groupIn) {
			Integer index = passer.getNodeIndex(passer.localName);
			if (group.contains(index.toString())) {
				for (String m: passer.groups.get(group)) {
					//if (!m.equals(passer.localName)) {
					groupMember.add(m);	
					//}
				}
			}
		}
		
		
		/*for (String group : groupIn) {
			for (String m: passer.groups.get(group)) {
				if (!m.equals(passer.localName)) {
					groupMember.add(m);
				}
			}
		}*/
	}

	public static Mutex getInstance() {
		if (mutex == null) {
			mutex = new Mutex();
		}
		return mutex;
	}

	/**
	 * Multicast request for mutex
	 * @throws CloneNotSupportedException 
	 * @throws IOException 
	 */
	public void request() throws IOException, CloneNotSupportedException {
		//state := WANTED;
		state = State.WANTED;
		voteGet.clear();
		//Multicast request to all processes in Vi;
		ArrayList<String> groups = passer.myself.getMemberOf();
		TimeStampedMessage mtxMsg = new TimeStampedMessage(null, "mutexRequest", null, passer.clock.getcurrentTimeStamp().clone());
		mtxMsg.set_source(passer.localName);
		mtxMsg.set_seqNum(passer.IDcounter.incrementAndGet());
		for (String group : groups) {
			Integer index = passer.getNodeIndex(passer.localName);
			if (group.contains(index.toString())) {
				mtxMsg.setDest(group);
				passer.multicastService.bMulticast(group, mtxMsg);
			}
		}
		//Wait until (number of replies received = K)
		while (!state.toString().equals("HELD")) {
			System.out.print("");
		}

	}

	/**
	 * Handle the mutex request from other process
	 * @throws IOException 
	 */
	public void requstHandle(Message mtxMsg) throws IOException {
		if (state.toString().equals("HELD") || vote) {
			//queue request from pi without replying
			for (int i = 0; i < requests.size(); i++) { //Don't queue this message if it has already been received
				if (requests.get(i).get_seqNumr() == mtxMsg.get_seqNumr() && requests.get(i).get_source().equals(mtxMsg.get_source())) {
					return;
				}
			}
//			if (requests.isEmpty()) {
//				requests.add((TimeStampedMessage) mtxMsg);
//			}
//			else {
			int i = 0;
				for (; i < requests.size(); i++) {
					if (requests.get(i).getTimeStamp().compareTo(((TimeStampedMessage) mtxMsg).getTimeStamp()) > 0) {
						requests.add(i, (TimeStampedMessage) mtxMsg);
						break;
					}
				}
				if (i == requests.size()) requests.add(i, (TimeStampedMessage) mtxMsg);
			//}
		} else { //send reply to pi; voted := TRUE;
			TimeStampedMessage reply = new TimeStampedMessage(mtxMsg.get_source(), "mutexReply", null, passer.clock.getcurrentTimeStamp().clone());
			reply.set_source(passer.localName);
			reply.set_seqNum(passer.IDcounter.incrementAndGet());
			passer.send(reply);
			vote = true;
		}
	}

	/**
	 * Multicast the release message
	 * @throws CloneNotSupportedException 
	 * @throws IOException 
	 */
	public void release() throws IOException, CloneNotSupportedException {
		//state := RELEASED;
		state = State.RELEASED;
		//Multicast release message to all processes in Vi;
		ArrayList<String> groups = passer.myself.getMemberOf();
		TimeStampedMessage rlsMsg = new TimeStampedMessage(null, "releaseRequest", null, passer.clock.getcurrentTimeStamp().clone());
		rlsMsg.set_source(passer.localName);
		rlsMsg.set_seqNum(passer.IDcounter.incrementAndGet());
		for (String group : groups) {
			Integer index = passer.getNodeIndex(passer.localName);
			if (group.contains(index.toString())) {
				rlsMsg.setDest(group);
				passer.multicastService.bMulticast(group, rlsMsg);
			}
		}
		/*for (String group : groups) {
			rlsMsg.setDest(group);
			passer.multicastService.bMulticast(group, rlsMsg);
		}*/

		releaseHandle(rlsMsg);

	}

	/**
	 * Handle the release message from other process
	 * @throws IOException 
	 */
	public void releaseHandle(TimeStampedMessage message) throws IOException {
		//if the released message has already received then drop it
		if (preReleasedMessage != null) {
			if (preReleasedMessage.get_seqNumr() == message.get_seqNumr() && preReleasedMessage.get_source().equals(message.get_source()))
				return;
		}
		preReleasedMessage = message;
		if (!requests.isEmpty()) {
			TimeStampedMessage next = requests.remove(0);
			vote = false;
			requstHandle(next);
			vote = true;
		} else {
			vote = false;
		}
	}

	/**
	 * Handle the vote this process get
	 */
	public void voteHandle(TimeStampedMessage voteMsg) {
		voteGet.add(voteMsg.get_source());
		if (voteGet.size() == groupMember.size()) {
			state = State.HELD;
		}

	}


}
