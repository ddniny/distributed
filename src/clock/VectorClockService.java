package clock;

import timestamp.TimeStamp;
import timestamp.VectorTimeStamp;

public class VectorClockService extends ClockService {
	
	public void initialize (int Index, int processNo) {
		currentTimeStamp = new VectorTimeStamp(processNo);
		processIndex = Index;
	}
	
	public void updateTimeStamp () {
		int[] nextTimeStamp = (int[]) currentTimeStamp.getTimeStamp();
		nextTimeStamp[getProcessIndex()]++;
		currentTimeStamp.setTimeStamp(nextTimeStamp);
	}
	
	public void updateTimeStamp (TimeStamp ts) {
		int[] receivedTSVector = (int[])((VectorTimeStamp)ts).getTimeStamp();
		int[] currentTSVector = (int[])((VectorTimeStamp)currentTimeStamp).getTimeStamp();
		for (int i = 0; i < receivedTSVector.length; i++) {
			currentTSVector[i] = Math.max(currentTSVector[i], receivedTSVector[i]);
		}
		currentTSVector[getProcessIndex()]++;
		currentTimeStamp.setTimeStamp(currentTSVector);
	}
	
	public void updateGroupTimeStamp (int senderIndex) { //
		int[] nextTimeStamp = (int[]) currentTimeStamp.getTimeStamp();
		nextTimeStamp[senderIndex]++;
		currentTimeStamp.setTimeStamp(nextTimeStamp);
	}
	
	public int getProcessIndex () {
		return processIndex;
	}
	
//	public void setProcessIndex (int index) {
//		processIndex = index;
//	}
}
