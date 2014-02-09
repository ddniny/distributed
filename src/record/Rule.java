package record;

import message.Message;

public class Rule {
	public enum ACTION {
		DROP, DELAY, DUPLICATE, DEFAULT
	}

	private ACTION action = null;
	private String src = null;
	private String dest = null;
	private String kind = null; 
	private Boolean duplicate = null;
	private int id = 0;
	private int Nth = 0;
	private int everyNth = 0;
	private int matchedTimes = 0;

	public ACTION getAction() {
		return action;
	}
	public void setAction(String action) {
		String str = action.toLowerCase();
		if (str.equals("duplicate")) {
			this.action = ACTION.DUPLICATE;
		} else if (str.equals("delay")) {
			this.action = ACTION.DELAY;
		} else if (str.equals("drop")) {
			this.action = ACTION.DROP;
		}
	}
	public void setAction(ACTION action) {
		this.action = action;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNth() {
		return Nth;
	}
	public void setNth(int nth) {
		Nth = nth;
	}
	public int getEveryNth() {
		return everyNth;
	}
	public void setEveryNth(int everyNth) {
		this.everyNth = everyNth;
	}

	public int getMatchedTimes() {
		return matchedTimes;
	}
	public void setMatchedTimes(int m) {
		this.matchedTimes = m;
	}

	public Boolean getDuplicate () {
		return duplicate;
	}

	public void setDuplicate (Boolean dup) {
		this.duplicate = dup;
	}
	
	/**
	 * Check if the rule is matched
	 * @param message
	 * @return
	 */
	public boolean isMatch(Message message) {
		if (this.duplicate == null || (this.duplicate.equals(message.get_sendDuplicate()))) {
			if (this.id == 0 || this.id == message.get_seqNumr()){
				if (this.src == null || this.src.equalsIgnoreCase(message.get_source())){
					if (this.dest == null || this.dest.equalsIgnoreCase(message.getDest())){
						if (this.kind == null || this.kind.equalsIgnoreCase(message.getKind())){
							setMatchedTimes(this.matchedTimes + 1);
							if (this.Nth != 0) {
								if (this.Nth == this.matchedTimes) {
									return true;
								}else if (this.everyNth == 0) {
									return false;
								}
							}
							if (this.everyNth != 0) {
								if (this.matchedTimes % this.everyNth == 0) {
									return true;
								}else return false;
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object rule) {
	    if (!(rule instanceof Rule)) {
	        return false;
	    }
	    
	    Rule r = (Rule) rule;
	    
		if (!r.getAction().equals(action)) return false;
		
		if (r.getSrc() != null) {
			if (!r.getSrc().equals(src)) return false;
		}else {
			if (src != null) return false;
		}
		
		if (r.getDest() != null) {
			if (!r.getDest().equals(dest)) return false;
		}else {
			if (dest != null) return false;
		}

		if (r.getKind() != null) {
			if (!r.getKind().equals(kind)) return false;
		} else {
			if (kind != null) return false;
		}

		if (r.getDuplicate() != null) {
			if (!r.getDuplicate().equals(duplicate)) return false;
		} else {
			if (duplicate != null) return false;
		}

		return  r.getId() == getId() && r.getNth() == getNth() && r.getEveryNth() == getEveryNth();
	}

	@Override
	public String toString() {
		return "[action=" + action + ", src=" + src + ", dest=" + dest
				+ ", kind=" + kind + ", id=" + id + ", Nth=" + Nth
				+ ", everyNth=" + everyNth + ", duplicate =" + duplicate + "]";
	}
}
