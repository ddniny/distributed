package clock;

import timestamp.LogicalTimeStamp;
import timestamp.TimeStamp;

public class LogicalClockService extends ClockService {
	
	
	public void initialize(int Index, int processNo) {
		currentTimeStamp = new LogicalTimeStamp();
		processIndex = Index; 
	}
	
	public void updateTimeStamp () {
		currentTimeStamp.setTimeStamp((Integer)currentTimeStamp.getTimeStamp() + 1);
	}
	
	public void updateTimeStamp (TimeStamp ts) {
		TimeStamp maxTimeStamp;
		if (((LogicalTimeStamp) currentTimeStamp).compareTo((LogicalTimeStamp)ts) <= 0) {
			maxTimeStamp = ts;
		}
		else
			maxTimeStamp = currentTimeStamp;
		
		currentTimeStamp.setTimeStamp((Integer)maxTimeStamp.getTimeStamp() + 1);
	}
}
