package thread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import timestamp.TimeStamp;
import timestamp.VectorTimeStamp;
import clock.ClockService;
import message.Message;
import message.MessagePasser;
import message.TimeStampedMessage;
import mutex.Mutex;

/**
 * 
 * Thread to listen user input 
 *
 */
public class UserThread extends Thread {

	@Override
	public void run() {
		BufferedReader in = null;
		MessagePasser passer = MessagePasser.getInstance();
		try {
			// TODO: you may want add some auto test here, so you can change the userTread's constructor
			// to get the input file name for each user. And read the file in specific format "Send bob"....


			while (true) {
				// wait user input
				System.out.println("Please enter your scenario \t 1: Send, 2: Receive, 3: Retrieve, 4: Request Mutex");
				in = new BufferedReader(new InputStreamReader(System.in));
				String cmdInput = in.readLine();
				// handle with "send"
				if (cmdInput.equals("1")) {
					System.out.println("Please enter your dest:");
					String dest = in.readLine();
					while (!passer.nodeMap.containsKey(dest) && !passer.groups.containsKey(dest)) {
						System.out.println("Your Dest has not been registered, enter again:");
						dest = in.readLine();
					}

					System.out.println("Please enter the kind:");
					String kind = in.readLine();
					System.out.println("Please enter the data:");
					String data = in.readLine();

					while (true) {
						System.out.println("Do you want send this message to logger? 1: Yes, 2: No");
						cmdInput = in.readLine();
						if (cmdInput.equals("1")) {
							passer.currentToLogger = true;
							break;
						}
						else if (cmdInput.equals("2")) {
							passer.currentToLogger = false;
							break;
						}
					}
					// create and send message
					//Message msg = new Message(dest, kind, data);

					TimeStampedMessage tsMsg = new TimeStampedMessage(dest, kind, data, passer.clock.getcurrentTimeStamp().clone());
					if (passer.groups.containsKey(tsMsg.getDest())) { //it is a multicast message
						passer.multicastService.bMulticast(tsMsg.getDest(), tsMsg);
					} else {
						tsMsg.set_source(passer.myself.getName());
						passer.send(tsMsg);
					}
					//                    msg.set_source(passer.myself.getName());
					//                    passer.send(msg);
				} else if (cmdInput.equals("2")) {
					TimeStampedMessage rcvTSmessageMessage = (TimeStampedMessage) passer.receive();
					System.out.println("Receive Messages : " + rcvTSmessageMessage);
					if (rcvTSmessageMessage != null) {
						while (true) {
							System.out.println("Do you want send this message to logger? 1: Yes, 2: No");
							cmdInput = in.readLine();
							if (cmdInput.equals("1")) {
								passer.currentToLogger = true;
								passer.sendAwayToLogger(rcvTSmessageMessage, "Receive send");
								break;
							}
							else if (cmdInput.equals("2")) {
								passer.currentToLogger = false;
								break;
							}
						}	
					}

				} else if (cmdInput.equals("3")) {
					TimeStampedMessage rtvMsg = new TimeStampedMessage("logger", "Retrieve", null, passer.clock.getcurrentTimeStamp());
					TimeStampedMessage preMessage = null;
					TimeStampedMessage curMessage = null;
					rtvMsg.set_source(passer.myself.getName());
					passer.sendAway(rtvMsg);
					UserThread.sleep(3000);
					ArrayList<TimeStampedMessage> logList = passer.printLog();
					while (!logList.isEmpty()) {
						curMessage = logList.remove(0);
						if (preMessage != null && preMessage.getTimeStamp().compareTo(curMessage.getTimeStamp()) == 0) {
							System.out.println("||");
						} else {
							if (preMessage != null) {
								System.out.println("->");
							}
						}
						preMessage = curMessage;
						System.out.print(curMessage);
					}
					System.out.println();
				} else if (cmdInput.equals("4")) {
					System.out.println("Sending request to group members...");
					System.out.println("Please wait...");
					Mutex.getInstance().request();
					System.out.println("Get into the critical section !");
					while (true) {
						System.out.println("Press \"1\" to release the section.");	
						cmdInput = in.readLine();
						if (cmdInput.equals("1")) {
							Mutex.getInstance().release();
							break;
						}
					}	
				}
			}
		} catch (Exception e) {
			System.err.println("ERROR: Reader corrput");
			e.printStackTrace();
		}
	}
}
