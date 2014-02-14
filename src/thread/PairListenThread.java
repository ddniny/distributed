package thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import message.Message;
import message.MessagePasser;
import message.TimeStampedMessage;
import mutex.Mutex;
import record.Rule;
import record.Rule.ACTION;

/**
 * 
 * Thread to listen to particular node (socket)
 *
 */
public class PairListenThread extends Thread {
	private Socket socket = null;

	public PairListenThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		MessagePasser passer = MessagePasser.getInstance();
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			while(true) {
				TimeStampedMessage message = (TimeStampedMessage)in.readObject();
				if (message.isMulticast()) {
					passer.multicastService.bDeliver(message.getGroupName(), message);
				} else {
					receivePasser(message, passer);
				}

			}
		} catch (Exception e) {
			System.err.println("ERROR: PairListenThread corrupt");
			e.printStackTrace();
		}
	}

	public static void receivePasser(TimeStampedMessage message, MessagePasser passer) throws IOException {
		switch (matchReceiveRule(message, passer)) {
		case DROP:
			System.out.println("INFO: Drop Message (Receive) " + message);
			break;
		case DELAY:
			passer.delayInMsgQueue.add(message);
			break;
		case DUPLICATE:
			// no break, because at least one message should be received
			message.set_rcvDuplicate(true);
		default:
			if (message.getKind().equals("mutexRequest")) {
				passer.clock.updateTimeStamp(message.getTimeStamp());
				Mutex.getInstance().requstHandle(message);
			} else if (message.getKind().equals("releaseRequest")) {
				passer.clock.updateTimeStamp(message.getTimeStamp());
				Mutex.getInstance().releaseHandle(message);;
			} else if (message.getKind().equals("mutexReply")) {
				passer.clock.updateTimeStamp(message.getTimeStamp());
				Mutex.getInstance().voteHandle(message);
			} else {
				receiveIn(message, passer);       
				// receive delayed message
				synchronized(passer.delayInMsgQueue) {
					while (!passer.delayInMsgQueue.isEmpty()) {
						receiveIn(passer.delayInMsgQueue.poll(), passer);
					}
				}

				// receive duplicated message if needed
				if (message.get_rcvDuplicate()) {
					receiveIn(message, passer);
				}
			}
		} 
	}

	/**
	 * Add message to receive buffer
	 * @param message
	 * @param passer
	 */
	private static void receiveIn(TimeStampedMessage message, MessagePasser passer) {
		passer.rcvBuffer.offer(message);
	}

	/**
	 * Check if the rule is matched
	 * @param message
	 * @param passer
	 * @return
	 * @throws IOException
	 */
	private static ACTION matchReceiveRule(Message message, MessagePasser passer) throws IOException {
		passer.checkModified();
		if (message.get_source().equals(passer.localName) && (message.getKind().equals("mutexRequest") || message.getKind().equals("releaseRequest"))) {
			return ACTION.DROP;
		}
		for (Rule rule : passer.rcvRules){
			if (rule.isMatch(message)) {
				return rule.getAction();
			}
		}
		return ACTION.DEFAULT;
	}
}
