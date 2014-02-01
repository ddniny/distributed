package clock;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ClockServiceFactory {

	private HashMap<String, Class> map = new HashMap<String, Class>();
	
	public void registerClockService (String serviceID, Class serviceClass) {
		map.put(serviceID, serviceClass);
	}
	
	public ClockService getClockService (String serviceID) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Class serviceClass = map.get(serviceID);
		Constructor serviceConstructor = serviceClass.getConstructor(new Class[] { });
		return (ClockService) serviceConstructor.newInstance(new Object[] { } );
	}
}
