package org.soc.shoppe.plugin.deal;

import org.vosao.business.plugin.AbstractPluginEntryPoint;

public class DealEntryPoint extends AbstractPluginEntryPoint{

	private DealVelocityPlugin dealVelocityPlugin;
	
	@Override
	public Object getPluginVelocityService() {
		if (dealVelocityPlugin == null) {
			dealVelocityPlugin = new DealVelocityPlugin(getBusiness());
		}
		return dealVelocityPlugin;
	}
	
}
