package org.soc.shoppe.busdata.owlNamespaceConstants;

import java.util.HashMap;
import java.util.Map;

public class OwlNamespace {

	private static final Map<String, String> namespaceMap = new HashMap<String, String>();
	
	static {
		namespaceMap.put("academy", "aca");
	}

	public static Map<String, String> getNamespacemap() {
		return namespaceMap;
	}
}
