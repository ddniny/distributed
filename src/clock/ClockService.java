package clock;

import timestamp.TimeStamp;

public abstract class ClockService {
	TimeStamp currentTimeStamp;
	int processIndex;
	
	public TimeStamp getcurrentTimeStamp () {
		return currentTimeStamp;
	}
	
	public abstract void initialize (int processIndex, int processNo);
	
	/**
	 * increment currentTimeStamp by 1 between any two successive events 
	 * used by the event of sending a message
	 */
	public abstract void updateTimeStamp ();
	
	/**
	 * set currentTimeStamp to a value greater than its present value and greater than the 
	 * timestamp received from the message
	 * used by the event of receiving a message
	 * @param ts
	 */
	public abstract void updateTimeStamp (TimeStamp ts);

	public int getProcessIndex() {
		return processIndex;
	}

	public void setProcessIndex(int processIndex) {
		this.processIndex = processIndex;
	}
}
