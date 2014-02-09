package record;

public class Node {
    private String name;
    private String ipAddress;
    private int port;
    
    /**
     * Constructor of node
     * @param name
     * @param ipAddress
     * @param port
     */
    public Node(String name, String ipAddress, int port) {
        super();
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getName() {
        return name;
    }
    public void setAlice(String name) {
        this.name = name;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    
    /**
     * Judge whether to nodes are equals
     * @param n
     * @return if equals return 0; if the name changed return -1; else return 1
     */
    public int equals(Node n) {
    	int v = 0;
    	if (n.getName() != null) {
    		if (!n.getName().equals(name)) v = -1;
    	}else {
    		if(name != null) 
    			v = -1;
    	}
    	if (n.getIpAddress() != null) {
    		if (!n.getIpAddress().equals(ipAddress)) v = 1;
    	}else {
    		if(ipAddress != null)
    			v = 1;
    	}
    	if (n.getPort() != port) {
    		v = 1;
    	}
    	
    	return v;
    }
    
    @Override
    public String toString() {
        return "[name=" + name + ", ipAddress=" + ipAddress + ", port="
                + port + "]";
    }
}
