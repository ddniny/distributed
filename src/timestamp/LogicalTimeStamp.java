package timestamp;

import java.io.Serializable;

public class LogicalTimeStamp implements TimeStamp, Comparable<LogicalTimeStamp>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int logicalTS;
	
	public LogicalTimeStamp() {
		this.logicalTS = 0;
	}

	@Override
	public int compareTo(LogicalTimeStamp l) {
		// TODO Auto-generated method stub
		return this.logicalTS - (Integer)l.getTimeStamp();
	}

	@Override
	public void setTimeStamp(Object nLST) {
		// TODO Auto-generated method stub
		logicalTS = (Integer)nLST;
	}

	@Override
	public Object getTimeStamp() {
		// TODO Auto-generated method stub
		return logicalTS;
	}

}
