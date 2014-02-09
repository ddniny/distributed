package message;

import timestamp.TimeStamp;

public class TimeStampedMessage extends Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; // Can be the same with its parent?
	private TimeStamp ts;
	private boolean multicast;
	private String groupName;

	public TimeStampedMessage(String dest, String kind, Object data, TimeStamp ts) {
		super(dest, kind, data);
		// TODO Auto-generated constructor stub
		this.ts = ts;
	}
	
	public void setTimeStamp(Object newTS) {
		ts.setTimeStamp(newTS);
	}
	
	public TimeStamp getTimeStamp() {
		return ts;
	}
	
	 public String toString() {
		 return "[header=" + super.header + ", payload=" + super.payload
	                + ", sendDuplicate=" + super.sendDuplicate + ", TimeStamp=" + getTimeStamp().toString() + "]";
	}

	public boolean isMulticast() {
		return multicast;
	}

	public void setMulticast(boolean multicast) {
		this.multicast = multicast;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	

}
