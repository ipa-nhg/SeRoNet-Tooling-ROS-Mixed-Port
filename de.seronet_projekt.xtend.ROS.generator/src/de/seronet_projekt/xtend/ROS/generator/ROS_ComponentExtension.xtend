package de.seronet_projekt.xtend.ROS.generator

import com.google.inject.Inject
import org.xtend.smartsoft.generator.component.SmartComponentExtension
import org.xtend.smartsoft.generator.component.SmartComponent
import rosInterfacesPool.RosPublisher
import rosInterfacesPool.RosSubscriber
import rosInterfacesPool.RosSrvClient
import rosInterfacesPool.RosSrvServer
import org.ecore.component.componentDefinition.ComponentDefinition
import org.ecore.component.seronetExtension.MixedPortROS
import rosInterfacesPool.RosActionClient
import rosInterfacesPool.RosActionServer

class ROS_ComponentExtension {
	@Inject extension CopyrightHelpers
	@Inject extension SmartComponentExtension
	@Inject extension SmartComponent
	@Inject extension ROS_Callbacks
	@Inject extension MixedPortROSGenHelpers;
	
	def String getRosPortBaseClassHeaderFile(ComponentDefinition comp) '''«comp.name»RosPortBaseClass.hh'''
	def String getRosPortExtensionHeaderFilename(ComponentDefinition component) '''«component.name»RosPortExtension.hh'''
	def String getRosPortExtensionSourceFilename(ComponentDefinition component) '''«component.name»RosPortExtension.cc'''
	
	def compileRosPortBaseClassHeader(ComponentDefinition comp) 
	'''
	«getCopyright()»
	
	#ifndef ROS_PORT_BASE_CLASS_H_
	#define ROS_PORT_BASE_CLASS_H_
	
	#include <ros/ros.h>
	«IF comp.hasActionClients»
	#include <actionlib/client/simple_action_client.h>
	«ENDIF»
	«IF comp.hasActionServers»
	#include <actionlib/server/simple_action_server.h>
	«ENDIF»
	«FOR port: comp.ROSPublishers»
	#include <«port.packageString»/«port.messageString».h>
	«ENDFOR»

	«FOR port: comp.ROSActionClient»
	#include <«port.packageString»/«port.messageString».h>
	«ENDFOR»
	«FOR port: comp.ROSActionServer»
	#include <«port.packageString»/«port.messageString».h>
	«ENDFOR»

	
	class «comp.name»RosPortBaseClass {
	public:
		«comp.name»RosPortBaseClass() { };
		virtual ~«comp.name»RosPortBaseClass() { }
		
		«FOR port: comp.allROSPorts»
		«port.rosType» «port.name»;
		«ENDFOR»
		
		«IF comp.hasRosPublishers»
		«FOR port:comp.ROSPublishers»
		void «port.name»_publish_ros_msg(const «port.packageString»::«port.messageString» &msg);
		«ENDFOR»
		«ENDIF»

	};
	
	#endif // ROS_PORT_BASE_CLASS_H_
	'''
	
	def compileRosPortExtensionHeader(ComponentDefinition comp)
	'''
	«getCopyright()»
	
	#ifndef ROS_PORT_COMPONENT_EXTENSION_H_
	#define ROS_PORT_COMPONENT_EXTENSION_H_
	
	#include "«comp.rosPortBaseClassHeaderFile»"
	«IF comp.hasRosSubscribers || comp.hasRosSvrServers»
	#include "«comp.rosPortCallbacksUserClassHeaderFile»"
	«ENDIF»
	
	#include "«comp.componentExtensionHeaderFilename»"
	
	// include component's main class
	#include "«comp.compHeaderFilename»"
	
	class «comp.name»RosPortExtension : public «comp.name»Extension, public «comp.name»RosPortBaseClass
	{
	private:
		ros::NodeHandle *nh;
		«IF comp.hasRosSubscribers || comp.hasRosSvrServers»
		«comp.name»RosPortCallbacks *callbacksPtr;
		«ENDIF»
		virtual int extensionExecution() override;
	public:
		«comp.name»RosPortExtension();
		virtual ~«comp.name»RosPortExtension();
	
		virtual void loadParameters(const SmartACE::SmartIniParameter &parameter);
		virtual void initialize(«comp.name» *component, int argc, char* argv[]) override;
		virtual int onStartup() override;

		«FOR port: comp.allROSPorts»
			«IF port.isROSActionClient || port.isROSActionServer»
				inline «port.rosType» get«port.name.toFirstUpper»Ptr() {
					return «port.name»;
				}
			«ELSE»
				inline «port.rosType»* get«port.name.toFirstUpper»Ptr() {
					return &«port.name»;
				}
			«ENDIF»
		«ENDFOR»

		virtual int onShutdown(const std::chrono::steady_clock::duration &timeoutTime=std::chrono::seconds(2)) override;
		virtual void destroy() override;
	};
	
	#endif /* ROS_PORT_COMPONENT_EXTENSION_H_ */
	'''
	
	
	def compileRosPortExtensionSource(ComponentDefinition component)
	'''
	«getCopyright()»
	
	#include "«component.rosPortExtensionHeaderFilename»"
	
	// statically create a global PlainOpcUa«component.name»Extension instance
	static «component.name»RosPortExtension ros_port_extension;
	
	«component.name»RosPortExtension::«component.name»RosPortExtension()
	:	«component.name»Extension("«component.name»RosPortExtension")
	{
		nh = 0;
		«IF component.hasRosSubscribers || component.hasRosSvrServers»
		callbacksPtr = 0;
		«ENDIF»
	}
	
	«component.name»RosPortExtension::~«component.name»RosPortExtension()
	{  }
	
	void «component.name»RosPortExtension::loadParameters(const SmartACE::SmartIniParameter &parameter)
	{  }
	
	void «component.name»RosPortExtension::initialize(«component.name» *component, int argc, char* argv[])
	{
		ros::init(argc, argv, "«component.name»", ros::init_options::NoSigintHandler);
		nh = new ros::NodeHandle();
		«IF component.hasRosSubscribers || component.hasRosSvrServers»
		callbacksPtr = new «component.name»RosPortCallbacks();
		«ENDIF»
		component->rosPorts = this;
		
		«FOR port: component.allROSPorts»
		«port.compilePortCreation»
		«ENDFOR»
	}
	
	«IF component.hasRosPublishers»
	«FOR port: component.ROSPublishers»
	void «component.name»RosPortBaseClass::«port.name»_publish_ros_msg(const «port.packageString»::«port.messageString» &msg){
		«port.name».publish(msg);
	}
	«ENDFOR»
	«ENDIF»
	
	int «component.name»RosPortExtension::onStartup()
	{
		return startExtensionThread();
	}
	
	int «component.name»RosPortExtension::extensionExecution()
	{
		ros::spin();
		return 0;
	}
	
	int «component.name»RosPortExtension::onShutdown(const std::chrono::steady_clock::duration &timeoutTime)
	{
		ros::shutdown();
		return stopExtensionThread(timeoutTime);
	}
	
	void «component.name»RosPortExtension::destroy()
	{
		«FOR actPort: component.ROSActionClient»
		delete «actPort.name»;
		«ENDFOR»
		«FOR actPort: component.ROSActionServer»
		delete «actPort.name»;
		«ENDFOR»
		delete nh;
		«IF component.hasRosSubscribers || component.hasRosSvrServers»
		delete callbacksPtr;
		«ENDIF»
	}
	'''
	
	def getRosType(MixedPortROS mrp) {
		val port = mrp.port
		switch (port) {
			RosPublisher: '''ros::Publisher'''
			RosSubscriber: '''ros::Subscriber'''
			RosSrvClient: '''ros::ServiceClient'''
			RosSrvServer: '''ros::ServiceServer'''
			RosActionClient: '''actionlib::SimpleActionClient<«mrp.packageString»::«mrp.messageString»>*'''
			RosActionServer: '''actionlib::SimpleActionServer<«mrp.packageString»::«mrp.messageString»>*'''
		}
	}
	
	def compilePortCreation(MixedPortROS mrp) {
		val port = mrp.port
		switch (port) {
			RosPublisher: '''«port.name» = nh->advertise<«mrp.packageString»::«mrp.messageString»>("«port.topicName»", 10);'''
			RosSubscriber: '''«port.name» = nh->subscribe("«port.topicName»", 10, &«(mrp.eContainer as ComponentDefinition).name»RosPortCallbacks::«mrp.name»_cb, callbacksPtr);'''
			RosSrvClient: '''«port.name» = nh->serviceClient<«mrp.packageString»::«mrp.messageString»>("«port.srvName»");'''
			RosSrvServer: '''«port.name» = nh->advertiseService("«port.srvName»", &«(mrp.eContainer as ComponentDefinition).name»RosPortCallbacks::«port.name»_cb, callbacksPtr);'''
			RosActionClient: '''«port.name» = new actionlib::SimpleActionClient<«mrp.packageString»::«mrp.messageString»>(*nh, "«port.actionName»", true);'''
			RosActionServer: '''«port.name» = new actionlib::SimpleActionServer<«mrp.packageString»::«mrp.messageString»>(*nh, "«port.actionName»", false);'''
		}
	}
}