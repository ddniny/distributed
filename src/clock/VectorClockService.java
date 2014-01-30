package clock;

import timestamp.TimeStamp;
import timestamp.VectorTimeStamp;

public class VectorClockService extends ClockService {
	private int processIndex;
	
	public void updateTimeStamp () {
		int[] nextTimeStamp = (int[]) currentTimeStamp.getTimeStamp();
		nextTimeStamp[processIndex]++;
		currentTimeStamp.setTimeStamp(nextTimeStamp);
	}
	
	public void updateTimeStamp (TimeStamp ts) {
		int[] receivedTSVector = (int[])((VectorTimeStamp)ts).getTimeStamp();
		int[] currentTSVector = (int[])((VectorTimeStamp)currentTimeStamp).getTimeStamp();
		for (int i = 0; i < receivedTSVector.length; i++) {
			currentTSVector[i] = Math.max(currentTSVector[i], receivedTSVector[i]);
		}
		currentTSVector[processIndex]++;
		currentTimeStamp.setTimeStamp(currentTSVector);
	}
	
	public int getProcessIndex () {
		return processIndex;
	}
	
	public void setProcessIndex (int index) {
		processIndex = index;
	}
}
