package timestamp;

public class logicalTimeStamp implements TimeStamp, Comparable<logicalTimeStamp> {
	
	private int logicalTS;
	
	public logicalTimeStamp() {
		this.logicalTS = 0;
	}

	@Override
	public int compareTo(logicalTimeStamp l) {
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
