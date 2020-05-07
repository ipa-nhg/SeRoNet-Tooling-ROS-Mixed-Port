package de.seronet_projekt.xtend.ROS.generator.ext

import org.xtend.smartsoft.generator.component.ActivityGeneratorExtension
import org.ecore.component.componentDefinition.Activity
import org.ecore.component.seronetExtension.MixedPortROSLink
import com.google.inject.Inject
import de.seronet_projekt.xtend.ROS.generator.MixedPortROSGenHelpers
import org.ecore.component.componentDefinition.ComponentDefinition
import rosInterfacesPool.RosSubscriber
import rosInterfacesPool.RosService

class ROSActivityGeneratorExtensionImpl implements ActivityGeneratorExtension {
	@Inject extension MixedPortROSGenHelpers;
	
	override getExtensionName() {
		"RosActivityGeneratorExtension"
	}
	
	override getUserHeaderIncludes(Activity activity) 
	'''
		«FOR link: activity.links.filter(MixedPortROSLink)»
		#include <«link.mixedportros.packageString»/«link.mixedportros.messageString».h>
		«ENDFOR»
	'''
	
	override getUserClassMemberPublicDefinition(Activity activity) 
	'''
	«FOR sub: activity.links.filter(MixedPortROSLink).map[mixedportros].filter[it.port instanceof RosSubscriber]»
	void «sub.name»_cb (const «sub.packageString»::«sub.messageString»::ConstPtr &msg);
	«ENDFOR»
	«FOR svrsrv: activity.links.filter(MixedPortROSLink).map[mixedportros].filter[it.port instanceof RosService]»
	«svrsrv.packageString»::«svrsrv.messageString»::Response «svrsrv.name»_callServiceQuery («svrsrv.packageString»::«svrsrv.messageString»::Request request);
	«ENDFOR»
	'''
	
	override getUserSourceImplementation(Activity activity) 
	'''
	«FOR sub: activity.links.filter(MixedPortROSLink).map[mixedportros].filter[it.port instanceof RosSubscriber]»
	void «activity.name.toFirstUpper»::«sub.name»_cb (const «sub.packageString»::«sub.messageString»::ConstPtr &msg) {
		// implement this method
	}
	«ENDFOR»
	«FOR svrsrv: activity.links.filter(MixedPortROSLink).map[mixedportros].filter[it.port instanceof RosService]»
	«svrsrv.packageString»::«svrsrv.messageString»::Response «activity.name.toFirstUpper»::«svrsrv.name»_callServiceQuery («svrsrv.packageString»::«svrsrv.messageString»::Request request) {
		// implement this method
	}
	«ENDFOR»
	'''
	
	def getComponent(Activity activity) {
		return (activity.eContainer as ComponentDefinition)
	}
}