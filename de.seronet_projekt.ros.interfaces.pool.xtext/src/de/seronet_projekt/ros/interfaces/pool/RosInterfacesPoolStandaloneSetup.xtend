/*
 * generated by Xtext 2.12.0
 */
package de.seronet_projekt.ros.interfaces.pool


/**
 * Initialization support for running Xtext languages without Equinox extension registry.
 */
class RosInterfacesPoolStandaloneSetup extends RosInterfacesPoolStandaloneSetupGenerated {

	def static void doSetup() {
		new RosInterfacesPoolStandaloneSetup().createInjectorAndDoEMFRegistration()
	}
}
