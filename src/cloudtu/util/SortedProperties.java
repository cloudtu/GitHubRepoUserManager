package cloudtu.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class SortedProperties extends Properties {
	private static final long serialVersionUID = -7489593870566322339L;
	
	/**
	 * Overrides, called by the store method.
	 */	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })	
	public synchronized Enumeration keys() {
		Enumeration keysEnum = super.keys();
		Vector keyList = new Vector();
		while (keysEnum.hasMoreElements()) {
			keyList.add(keysEnum.nextElement());
		}
		Collections.sort(keyList);
		return keyList.elements();
	}
}
