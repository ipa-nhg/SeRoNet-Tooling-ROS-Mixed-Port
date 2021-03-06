/*
 * generated by Xtext 2.16.0
 */
package de.seronet_projekt.ros.interfaces.pool.ui;

import com.google.inject.Injector;
import de.seronet_projekt.ros.interfaces.pool.xtext.ui.internal.XtextActivator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;

/**
 * This class was generated. Customizations should only happen in a newly
 * introduced subclass. 
 */
public class RosInterfacesPoolExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return Platform.getBundle(XtextActivator.PLUGIN_ID);
	}
	
	@Override
	protected Injector getInjector() {
		XtextActivator activator = XtextActivator.getInstance();
		return activator != null ? activator.getInjector(XtextActivator.DE_SERONET_PROJEKT_ROS_INTERFACES_POOL_ROSINTERFACESPOOL) : null;
	}

}
