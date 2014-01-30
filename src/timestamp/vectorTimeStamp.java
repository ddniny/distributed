package timestamp;


public class vectorTimeStamp implements TimeStamp, Comparable<vectorTimeStamp>{
	private int[] vectorTS;
	
	public vectorTimeStamp(int processNo) {
		vectorTS = new int[processNo];
	}

	@Override
	public int compareTo(vectorTimeStamp v) {
		// TODO Auto-generated method stub
		boolean hasLarger = false;
		boolean hasSmaller = false;
		for (int i = 0; i < vectorTS.length; i++) {
			if (vectorTS[i] < ((int[])v.getTimeStamp())[i]) hasSmaller = true;
			else if (vectorTS[i] > ((int[])v.getTimeStamp())[i]) hasLarger = true;
		}
		if (hasSmaller && !hasLarger) return -1;
		else if (hasSmaller && hasLarger) return 0;
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

}
