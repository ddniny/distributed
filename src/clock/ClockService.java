package clock;

import timestamp.TimeStamp;

public abstract class ClockService {
	TimeStamp currentTimeStamp;
	
	public TimeStamp getcurrentTimeStamp () {
		return currentTimeStamp;
	}
	
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
}
