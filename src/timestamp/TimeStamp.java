package timestamp;

public abstract class TimeStamp implements Comparable<TimeStamp> {
	
	public abstract void setTimeStamp(Object o);
	public abstract Object getTimeStamp();
	public abstract TimeStamp clone();
}
