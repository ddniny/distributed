package timestamp;

import java.io.Serializable;
import java.util.Arrays;

import message.TimeStampedMessage;


public class VectorTimeStamp extends TimeStamp implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int[] vectorTS;
	
	public VectorTimeStamp(int processNo) {
		vectorTS = new int[processNo];
	}
	
	public VectorTimeStamp clone() {
		VectorTimeStamp cloned = new VectorTimeStamp(vectorTS.length);
		for (int i = 0; i < cloned.vectorTS.length; i++) {
			cloned.vectorTS[i] = vectorTS[i];
		}
		return cloned;
	}
	
	@Override
	public int compareTo(TimeStamp v) {
		// TODO Auto-generated method stub
		boolean hasLarger = false;
		boolean hasSmaller = false;
		for (int i = 0; i < vectorTS.length; i++) {
			if (vectorTS[i] < ((int[])v.getTimeStamp())[i]) hasSmaller = true;
			else if (vectorTS[i] > ((int[])v.getTimeStamp())[i]) hasLarger = true;
		}
		if (hasSmaller && !hasLarger) return -1;
		else if (hasSmaller == hasLarger) return 0;
		else return 1;
	}

	@Override
	public void setTimeStamp(Object nTS) {
		// TODO Auto-generated method stub
		vectorTS = (int[])nTS;
	}

	@Override
	public Object getTimeStamp() {
		// TODO Auto-generated method stub
		return vectorTS;
	}
	
	public String toString() {
		return Arrays.toString(vectorTS);
	}

}
