/*
 * generated by Xtext 2.12.0
 */
package de.seronet_projekt.ros.interfaces.pool.ide

import com.google.inject.Guice
import de.seronet_projekt.ros.interfaces.pool.RosInterfacesPoolRuntimeModule
import de.seronet_projekt.ros.interfaces.pool.RosInterfacesPoolStandaloneSetup
import org.eclipse.xtext.util.Modules2

/**
 * Initialization support for running Xtext languages as language servers.
 */
class RosInterfacesPoolIdeSetup extends RosInterfacesPoolStandaloneSetup {

	override createInjector() {
		Guice.createInjector(Modules2.mixin(new RosInterfacesPoolRuntimeModule, new RosInterfacesPoolIdeModule))
	}
	
}
