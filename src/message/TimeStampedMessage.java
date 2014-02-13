package message;

import timestamp.TimeStamp;

public class TimeStampedMessage extends Message implements Cloneable {
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
	
	public TimeStampedMessage clone() {
		TimeStampedMessage cloned = new TimeStampedMessage(this.getDest(), this.getKind(), this.getPayload(), this.ts);
//		try {
//			cloned = (TimeStampedMessage) super.clone();
//					} catch (Exception e) {
//			// TODO: handle exception
//		}
		cloned.header = this.header.clone();
		cloned.groupName = this.groupName;
		cloned.multicast = this.multicast;
		cloned.sendDuplicate = this.sendDuplicate;
		String sourString = this.get_source();
		cloned.set_source(sourString);
		String destString = this.getDest();
		cloned.setDest(destString);
		int seqNum = this.get_seqNumr();
		cloned.set_seqNum(seqNum);
		cloned.setMedium(this.getMedium());
		return cloned;
	}
	
	public void setTimeStamp(Object newTS) {
		ts.setTimeStamp(newTS);
	}
	
	public TimeStamp getTimeStamp() {
		return ts;
	}
	
	 public String toString() {
		 return "[header=" + super.header + ", medium=" + super.getMedium() + ", multicast=" + multicast + ", Group=" + groupName + ", payload=" + super.payload
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
