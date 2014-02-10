package message;

import java.io.Serializable;

public class Message implements Serializable, Cloneable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected Header header = null;
    protected Object payload = null;
    protected boolean sendDuplicate;
    private boolean rcvDuplicate;
    private String medium = null;
    
    public class Header implements Serializable, Cloneable{
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private int sequenceNumber;
        private String source;
        private String dest;
        private String kind;
        
        public Header(String dest, String kind) {
            this.dest = dest;
            this.kind = kind;
        }
        
        public Header clone() {
        	Header cloned = new Header(this.dest, this.kind);
        	cloned.dest = this.dest;
        	cloned.kind = this.kind;
            return cloned;
        }

        @Override
        public String toString() {
            return "[sequenceNumber=" + sequenceNumber + ", source="
                    + source + ", dest=" + dest + ", kind=" + kind + "]";
        }   
    }
    

    
    public Message(String dest, String kind, Object data) {
        this.header = new Header(dest, kind);
        this.payload = data;
        this.sendDuplicate = false;
        this.rcvDuplicate = false;
    }

    public void set_seqNum(int sequenceNumber) {
        this.header.sequenceNumber = sequenceNumber;
    }
    
    public int get_seqNumr() {
        return this.header.sequenceNumber;
    }

    public String get_source() {
        return this.header.source;
    }

    public void set_source(String source) {
        this.header.source = source;
    }

    public String getDest() {
        return header.dest;
    }

    public void setDest(String dest) {
        this.header.dest = dest;
    }

    public String getKind() {
        return header.kind;
    }

    public void setKind(String kind) {
        this.header.kind = kind;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public void set_sendDuplicate(Boolean dupe) {
        this.sendDuplicate = dupe;
    }
    
    public boolean get_sendDuplicate() {
        return this.sendDuplicate;
    }
    
    public void set_rcvDuplicate(Boolean dupe) {
        this.rcvDuplicate = dupe;
    }
    
    public boolean get_rcvDuplicate() {
        return this.rcvDuplicate;
    }
    
    public String getMedium() {
		return medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}

    @Override
    public String toString() {
        return "[header=" + header + ", payload=" + payload
                + ", sendDuplicate=" + sendDuplicate + "]";
    }
}