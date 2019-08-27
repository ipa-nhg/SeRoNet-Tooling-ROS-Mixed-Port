//--------------------------------------------------------------------------
// Code generated by the SmartSoft MDSD Toolchain
// The SmartSoft Toolchain has been developed by:
//  
// Service Robotics Research Center
// University of Applied Sciences Ulm
// Prittwitzstr. 10
// 89075 Ulm (Germany)
//
// Information about the SmartSoft MDSD Toolchain is available at:
// www.servicerobotik-ulm.de
//
// Please do not modify this file. It will be re-generated
// running the code generator.
//--------------------------------------------------------------------------
#ifndef _COMPONENTROSJOYSTICK_HH
#define _COMPONENTROSJOYSTICK_HH

#include <map>
#include <iostream>
#include "aceSmartSoft.hh"
#include "smartQueryServerTaskTrigger_T.h"
#include "ComponentRosJoystickCore.hh"

#include "ComponentRosJoystickPortFactoryInterface.hh"
#include "ComponentRosJoystickExtension.hh"

// forward declarations
class ComponentRosJoystickPortFactoryInterface;
class ComponentRosJoystickExtension;

// includes for ComponentRosJoystickROSExtension
#include "ComponentRosJoystickRosPortBaseClass.hh"

// includes for SeRoNetSDKComponentGeneratorExtension

// includes for PlainOpcUaComponentRosJoystickExtension
// include plain OPC UA device clients
#include "OpcUaOpcUaClient4.hh"
// include plain OPC UA status servers


// include communication objects
#include <CommBasicObjects/CommJoystick.hh>
#include <CommBasicObjects/CommJoystickACE.hh>

// include tasks
#include "JoystickActivity.hh"
// include UpcallManagers

// include input-handler
// include input-handler

// include handler
#include "CompHandler.hh"


#include "SmartStateChangeHandler.hh"

#define COMP ComponentRosJoystick::instance()

class ComponentRosJoystick : public ComponentRosJoystickCore {
private:
	static ComponentRosJoystick *_componentRosJoystick;
	
	// constructor
	ComponentRosJoystick();
	
	// copy-constructor
	ComponentRosJoystick(const ComponentRosJoystick& cc);
	
	// destructor
	~ComponentRosJoystick() { };
	
	// load parameter from ini file
	void loadParameter(int argc, char* argv[]);
	
	// instantiate comp-handler
	CompHandler compHandler;
	
	// helper method that maps a string-name to an according TaskTriggerSubject
	Smart::TaskTriggerSubject* getInputTaskTriggerFromString(const std::string &client);
	
	// internal map storing the different port-creation factories (that internally map to specific middleware implementations)
	std::map<std::string, ComponentRosJoystickPortFactoryInterface*> portFactoryRegistry;
	
	// internal map storing various extensions of this component class
	std::map<std::string, ComponentRosJoystickExtension*> componentExtensionRegistry;
	
public:
	
	// define tasks
	Smart::TaskTriggerSubject* joystickActivityTrigger;
	JoystickActivity *joystickActivity;
	
	// define input-ports
	
	// define request-ports
	
	// define input-handler
	
	// define output-ports
	Smart::IPushServerPattern<CommBasicObjects::CommJoystick> *joystickServiceOut;
	
	// define answer-ports
	
	// define request-handlers
	
	// definitions of ComponentRosJoystickROSExtension
	ComponentRosJoystickRosPortBaseClass *rosPorts;
	
	// definitions of SeRoNetSDKComponentGeneratorExtension
	
	// definitions of PlainOpcUaComponentRosJoystickExtension
	OPCUA::OpcUaClient4 *opcUaClient4;
	
	
	// define default slave ports
	SmartACE::StateSlave *stateSlave;
	SmartStateChangeHandler *stateChangeHandler;
	SmartACE::WiringSlave *wiringSlave;
	
	
	/// this method is used to register different PortFactory classes (one for each supported middleware framework)
	void addPortFactory(const std::string &name, ComponentRosJoystickPortFactoryInterface *portFactory);
	
	SmartACE::SmartComponent* getComponentImpl();
	
	/// this method is used to register different component-extension classes
	void addExtension(ComponentRosJoystickExtension *extension);
	
	/// this method allows to access the registered component-extensions (automatically converting to the actuall implementation type)
	template <typename T>
	T* getExtension(const std::string &name) {
		auto it = componentExtensionRegistry.find(name);
		if(it != componentExtensionRegistry.end()) {
			return dynamic_cast<T*>(it->second);
		}
		return 0;
	}
	
	/// initialize component's internal members
	void init(int argc, char *argv[]);
	
	/// execute the component's infrastructure
	void run();
	
	/// clean-up component's resources
	void fini();
	
	/// call this method to set the overall component into the Alive state (i.e. component is then ready to operate)
	void setStartupFinished();
	
	/// connect all component's client ports
	Smart::StatusCode connectAndStartAllServices();
	
	/// start all assocuated Activities
	void startAllTasks();
	
	/// start all associated timers
	void startAllTimers();
	

	// return singleton instance
	static ComponentRosJoystick* instance()
	{
		if(_componentRosJoystick == 0) {
			_componentRosJoystick = new ComponentRosJoystick();
		}
		return _componentRosJoystick;
	}
	
	static void deleteInstance() {
		if(_componentRosJoystick != 0) {
			delete _componentRosJoystick;
		}
	}
	
	// connections parameter
	struct connections_struct
	{
		// component struct
		struct component_struct
		{
			// the name of the component
			std::string name;
			std::string initialComponentMode;
			std::string defaultScheduler;
			bool useLogger;
		} component;
		
		//--- task parameter ---
		struct JoystickActivity_struct {
			double minActFreq;
			double maxActFreq;
			std::string trigger;
			// only one of the following two params is 
			// actually used at run-time according 
			// to the system config model
			double periodicActFreq;
			// or
			std::string inPortRef;
			int prescale;
			// scheduling parameters
			std::string scheduler;
			int priority;
			int cpuAffinity;
		} joystickActivity;
		
		//--- upcall parameter ---
		
		//--- server port parameter ---
		struct JoystickServiceOut_struct {
				std::string serviceName;
				std::string roboticMiddleware;
		} joystickServiceOut;
	
		//--- client port parameter ---
		
		// -- parameters for ComponentRosJoystickROSExtension
		
		// -- parameters for SeRoNetSDKComponentGeneratorExtension
		
		// -- parameters for PlainOpcUaComponentRosJoystickExtension
		struct OpcUaClient4_struct {
			bool autoConnect;
			std::string deviceURI;
			std::string rootObjectPath;
			std::string opcuaXmlFile;
		} opcUaClient4;
		
		
	} connections;
};
#endif
