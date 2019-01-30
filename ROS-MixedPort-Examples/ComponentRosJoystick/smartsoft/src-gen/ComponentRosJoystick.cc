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
#include "ComponentRosJoystick.hh"
#include "smartTimedTaskTrigger.h"
//FIXME: implement logging
//#include "smartGlobalLogger.hh"

// the ace port-factory is used as a default port-mapping
#include "ComponentRosJoystickAcePortFactory.hh"


// initialize static singleton pointer to zero
ComponentRosJoystick* ComponentRosJoystick::_componentRosJoystick = 0;

// constructor
ComponentRosJoystick::ComponentRosJoystick()
{
	std::cout << "constructor of ComponentRosJoystick\n";
	
	// set all pointer members to NULL
	joystickActivity = NULL;
	joystickActivityTrigger = NULL;
	joystickServiceOut = NULL;
	stateChangeHandler = NULL;
	stateSlave = NULL;
	wiringSlave = NULL;
	
	// set default ini parameter values
	connections.component.name = "ComponentRosJoystick";
	connections.component.initialComponentMode = "Neutral";
	connections.component.defaultScheduler = "DEFAULT";
	connections.component.useLogger = false;
	
	connections.joystickServiceOut.serviceName = "JoystickServiceOut";
	connections.joystickServiceOut.roboticMiddleware = "ACE_SmartSoft";
	connections.joystickActivity.minActFreq = 0.0;
	connections.joystickActivity.maxActFreq = 0.0;
	connections.joystickActivity.trigger = "PeriodicTimer";
	connections.joystickActivity.periodicActFreq = 10.0;
	// scheduling default parameters
	connections.joystickActivity.scheduler = "DEFAULT";
	connections.joystickActivity.priority = -1;
	connections.joystickActivity.cpuAffinity = -1;
	
	// initialize members of ComponentRosJoystickROSExtension
	rosPorts = 0;
	
	// initialize members of SeRoNetSDKComponentGeneratorExtension
	
	// initialize members of PlainOpcUaComponentRosJoystickExtension
	
}

void ComponentRosJoystick::addPortFactory(const std::string &name, ComponentRosJoystickPortFactoryInterface *portFactory)
{
	portFactoryRegistry[name] = portFactory;
}

void ComponentRosJoystick::addExtension(ComponentRosJoystickExtension *extension)
{
	componentExtensionRegistry[extension->getName()] = extension;
}

/**
 * Notify the component that setup/initialization is finished.
 * You may call this function from anywhere in the component.
 *
 * Set component's internal lifecycle state automaton (if any) into 
 * Alive mode (from here on the component is ready to provide its services)
 */
void ComponentRosJoystick::setStartupFinished() {
	stateSlave->setWaitState("Alive");
	std::cout << "ComponentDefinition initialization/startup finished." << std::endl;
}




/**
 * First connect ALL client ports contained in this component, then start all services:
 * activate state, push, etc...
 */
Smart::StatusCode ComponentRosJoystick::connectAndStartAllServices() {
	Smart::StatusCode status = Smart::SMART_OK;
	
	return status;
}

/**
 * Start all tasks contained in this component.
 */
void ComponentRosJoystick::startAllTasks() {
	// start task JoystickActivity
	if(connections.joystickActivity.scheduler != "DEFAULT") {
		ACE_Sched_Params joystickActivity_SchedParams(ACE_SCHED_OTHER, ACE_THR_PRI_OTHER_DEF);
		if(connections.joystickActivity.scheduler == "FIFO") {
			joystickActivity_SchedParams.policy(ACE_SCHED_FIFO);
			joystickActivity_SchedParams.priority(ACE_THR_PRI_FIFO_MIN);
		} else if(connections.joystickActivity.scheduler == "RR") {
			joystickActivity_SchedParams.policy(ACE_SCHED_RR);
			joystickActivity_SchedParams.priority(ACE_THR_PRI_RR_MIN);
		}
		joystickActivity->start(joystickActivity_SchedParams, connections.joystickActivity.cpuAffinity);
	} else {
		joystickActivity->start();
	}
}

/**
 * Start all timers contained in this component
 */
void ComponentRosJoystick::startAllTimers() {
}


Smart::TaskTriggerSubject* ComponentRosJoystick::getInputTaskTriggerFromString(const std::string &client)
{
	
	return NULL;
}


void ComponentRosJoystick::init(int argc, char *argv[])
{
	try {
		Smart::StatusCode status;
		
		// load initial parameters from ini-file (if found)
		loadParameter(argc, argv);
		
		
		// initializations of ComponentRosJoystickROSExtension
		
		// initializations of SeRoNetSDKComponentGeneratorExtension
		
		// initializations of PlainOpcUaComponentRosJoystickExtension
		
		
		// initialize all registered port-factories
		for(auto portFactory = portFactoryRegistry.begin(); portFactory != portFactoryRegistry.end(); portFactory++) 
		{
			portFactory->second->initialize(this, argc, argv);
		}
		
		// initialize all registered component-extensions
		for(auto extension = componentExtensionRegistry.begin(); extension != componentExtensionRegistry.end(); extension++) 
		{
			extension->second->initialize(this, argc, argv);
		}
		
		ComponentRosJoystickPortFactoryInterface *acePortFactory = portFactoryRegistry["ACE_SmartSoft"];
		if(acePortFactory == 0) {
			std::cerr << "ERROR: acePortFactory NOT instantiated -> exit(-1)" << std::endl;
			exit(-1);
		}
		
		// this pointer is used for backwards compatibility (deprecated: should be removed as soon as all patterns, including coordination, are moved to port-factory)
		SmartACE::SmartComponent *component = dynamic_cast<ComponentRosJoystickAcePortFactory*>(acePortFactory)->getComponentImpl();
		
		std::cout << "ComponentDefinition ComponentRosJoystick is named " << connections.component.name << std::endl;
		
		if(connections.component.useLogger == true) {
			//FIXME: use logging
			//Smart::LOGGER->openLogFileInFolder("data/"+connections.component.name);
			//Smart::LOGGER->startLogging();
		}

		// create event-test handlers (if needed)
		
		// create server ports
		// TODO: set minCycleTime from Ini-file
		joystickServiceOut = portFactoryRegistry[connections.joystickServiceOut.roboticMiddleware]->createJoystickServiceOut(connections.joystickServiceOut.serviceName);
		
		// create client ports
		
		// create InputTaskTriggers and UpcallManagers
		
		// create input-handler
		
		// create request-handlers
		
		// create state pattern
		stateChangeHandler = new SmartStateChangeHandler();
		stateSlave = new SmartACE::StateSlave(component, stateChangeHandler);
		status = stateSlave->setUpInitialState(connections.component.initialComponentMode);
		if (status != Smart::SMART_OK) std::cerr << status << "; failed setting initial ComponentMode: " << connections.component.initialComponentMode << std::endl;
		// activate state slave
		status = stateSlave->activate();
		if(status != Smart::SMART_OK) std::cerr << "ERROR: activate state" << std::endl;
		
		wiringSlave = new SmartACE::WiringSlave(component);
		// add client port to wiring slave
		
		
		
		// create Task JoystickActivity
		joystickActivity = new JoystickActivity(component);
		// configure input-links
		// configure task-trigger (if task is configurable)
		if(connections.joystickActivity.trigger == "PeriodicTimer") {
			// create PeriodicTimerTrigger
			int microseconds = 1000*1000 / connections.joystickActivity.periodicActFreq;
			if(microseconds > 0) {
				Smart::TimedTaskTrigger *triggerPtr = new Smart::TimedTaskTrigger();
				triggerPtr->attach(joystickActivity);
				component->getTimerManager()->scheduleTimer(triggerPtr, std::chrono::microseconds(microseconds), std::chrono::microseconds(microseconds));
				// store trigger in class member
				joystickActivityTrigger = triggerPtr;
			} else {
				std::cerr << "ERROR: could not set-up Timer with cycle-time " << microseconds << " as activation source for Task JoystickActivity" << std::endl;
			}
		} else if(connections.joystickActivity.trigger == "DataTriggered") {
			joystickActivityTrigger = getInputTaskTriggerFromString(connections.joystickActivity.inPortRef);
			if(joystickActivityTrigger != NULL) {
				joystickActivityTrigger->attach(joystickActivity, connections.joystickActivity.prescale);
			} else {
				std::cerr << "ERROR: could not set-up InPort " << connections.joystickActivity.inPortRef << " as activation source for Task JoystickActivity" << std::endl;
			}
		} else
		{
			// setup default task-trigger as PeriodicTimer
			Smart::TimedTaskTrigger *triggerPtr = new Smart::TimedTaskTrigger();
			int microseconds = 1000*1000 / 10.0;
			if(microseconds > 0) {
				component->getTimerManager()->scheduleTimer(triggerPtr, std::chrono::microseconds(microseconds), std::chrono::microseconds(microseconds));
				triggerPtr->attach(joystickActivity);
				// store trigger in class member
				joystickActivityTrigger = triggerPtr;
			} else {
				std::cerr << "ERROR: could not set-up Timer with cycle-time " << microseconds << " as activation source for Task JoystickActivity" << std::endl;
			}
		}
		
		
		// link observers with subjects
	} catch (const std::exception &ex) {
		std::cerr << "Uncaught std exception" << ex.what() << std::endl;
	} catch (...) {
		std::cerr << "Uncaught exception" << std::endl;
	}
}

// run the component
void ComponentRosJoystick::run()
{
	stateSlave->acquire("init");
	// startup all registered port-factories
	for(auto portFactory = portFactoryRegistry.begin(); portFactory != portFactoryRegistry.end(); portFactory++) 
	{
		portFactory->second->onStartup();
	}
	
	// startup all registered component-extensions
	for(auto extension = componentExtensionRegistry.begin(); extension != componentExtensionRegistry.end(); extension++) 
	{
		extension->second->onStartup();
	}
	stateSlave->release("init");
	
	// do not call this handler within the init state (see above) as this handler internally calls setStartupFinished() (this should be fixed in future)
	compHandler.onStartup();
	
	// this call blocks until the component is commanded to shutdown
	stateSlave->acquire("shutdown");
	
	// shutdown all registered component-extensions
	for(auto extension = componentExtensionRegistry.begin(); extension != componentExtensionRegistry.end(); extension++) 
	{
		extension->second->onShutdown();
	}
	
	// shutdown all registered port-factories
	for(auto portFactory = portFactoryRegistry.begin(); portFactory != portFactoryRegistry.end(); portFactory++) 
	{
		portFactory->second->onShutdown();
	}
	
	if(connections.component.useLogger == true) {
		//FIXME: use logging
		//Smart::LOGGER->stopLogging();
	}
	
	compHandler.onShutdown();
	
	stateSlave->release("shutdown");
}

// clean-up component's resources
void ComponentRosJoystick::fini()
{
	// unlink all observers
	
	// destroy all task instances
	// unlink all UpcallManagers
	// unlink the TaskTrigger
	joystickActivityTrigger->detach(joystickActivity);
	delete joystickActivity;

	// destroy all input-handler

	// destroy InputTaskTriggers and UpcallManagers

	// destroy client ports

	// destroy server ports
	delete joystickServiceOut;
	// destroy event-test handlers (if needed)
	
	// destroy request-handlers
	
	delete stateSlave;
	// destroy state-change-handler
	delete stateChangeHandler;
	
	// destroy all master/slave ports
	delete wiringSlave;
	

	// destroy all registered component-extensions
	for(auto extension = componentExtensionRegistry.begin(); extension != componentExtensionRegistry.end(); extension++) 
	{
		extension->second->destroy();
	}

	// destroy all registered port-factories
	for(auto portFactory = portFactoryRegistry.begin(); portFactory != portFactoryRegistry.end(); portFactory++) 
	{
		portFactory->second->destroy();
	}
	
	// destruction of ComponentRosJoystickROSExtension
	
	// destruction of SeRoNetSDKComponentGeneratorExtension
	
	// destruction of PlainOpcUaComponentRosJoystickExtension
	
}

void ComponentRosJoystick::loadParameter(int argc, char *argv[])
{
	/*
	 Parameters can be specified via command line --filename=<filename> or -f <filename>

	 With this parameter present:
	   - The component will look for the file in the current working directory,
	     a path relative to the current directory or any absolute path
	   - The component will use the default values if the file cannot be found

	 With this parameter absent:
	   - <Name of Component>.ini will be read from current working directory, if found there
	   - $SMART_ROOT/etc/<Name of Component>.ini will be read otherwise
	   - Default values will be used if neither found in working directory or /etc
	 */
	SmartACE::SmartIniParameter parameter;
	std::ifstream parameterfile;
	bool parameterFileFound = false;

	// load parameters
	try
	{
		// if paramfile is given as argument
		if(parameter.tryAddFileFromArgs(argc,argv,"filename", 'f'))
		{
			parameterFileFound = true;
			std::cout << "parameter file is loaded from an argv argument \n";
		} else if(parameter.searchFile("ComponentRosJoystick.ini", parameterfile)) {
			parameterFileFound = true;
			std::cout << "load ComponentRosJoystick.ini parameter file\n";
			parameter.addFile(parameterfile);
		} else {
			std::cout << "WARNING: ComponentRosJoystick.ini parameter file not found! (using default values or command line arguments)\n";
		}
		
		// add command line arguments to allow overwriting of parameters
		// from file
		parameter.addCommandLineArgs(argc,argv,"component");
		
		// initialize the naming service using the command line parameters parsed in the
		// SmartIniParameter class. The naming service parameters are expected to be in
		// the "component" parameter group.
		SmartACE::NAMING::instance()->checkForHelpArg(argc,argv);
		if(parameterFileFound) 
		{
			if(SmartACE::NAMING::instance()->init(parameter.getAllParametersFromGroup("component")) != 0) {
				// initialization of naming service failed
				throw std::logic_error( "<NamingService> Service initialization failed!\nPossible causes could be:\n-> Erroneous configuration.\n-> Naming service not reachable.\n" );
			}
		} else {
			if(SmartACE::NAMING::instance()->init(argc, argv) != 0) {
				// initialization of naming service failed
				throw std::logic_error( "<NamingService> Service initialization failed!\nPossible causes could be:\n-> Erroneous configuration.\n-> Naming service not reachable.\n" );
			}
		}
			
		// print all known parameters
		// parameter.print();
		
		//--- server port // client port // other parameter ---
		// load parameter
		parameter.getString("component", "name", connections.component.name);
		parameter.getString("component", "initialComponentMode", connections.component.initialComponentMode);
		if(parameter.checkIfParameterExists("component", "defaultScheduler")) {
			parameter.getString("component", "defaultScheduler", connections.component.defaultScheduler);
		}
		if(parameter.checkIfParameterExists("component", "useLogger")) {
			parameter.getBoolean("component", "useLogger", connections.component.useLogger);
		}
		
		
		// load parameters for server JoystickServiceOut
		parameter.getString("JoystickServiceOut", "serviceName", connections.joystickServiceOut.serviceName);
		if(parameter.checkIfParameterExists("JoystickServiceOut", "roboticMiddleware")) {
			parameter.getString("JoystickServiceOut", "roboticMiddleware", connections.joystickServiceOut.roboticMiddleware);
		}
		
		// load parameters for task JoystickActivity
		parameter.getDouble("JoystickActivity", "minActFreqHz", connections.joystickActivity.minActFreq);
		parameter.getDouble("JoystickActivity", "maxActFreqHz", connections.joystickActivity.maxActFreq);
		parameter.getString("JoystickActivity", "triggerType", connections.joystickActivity.trigger);
		if(connections.joystickActivity.trigger == "PeriodicTimer") {
			parameter.getDouble("JoystickActivity", "periodicActFreqHz", connections.joystickActivity.periodicActFreq);
		} else if(connections.joystickActivity.trigger == "DataTriggered") {
			parameter.getString("JoystickActivity", "inPortRef", connections.joystickActivity.inPortRef);
			parameter.getInteger("JoystickActivity", "prescale", connections.joystickActivity.prescale);
		}
		if(parameter.checkIfParameterExists("JoystickActivity", "scheduler")) {
			parameter.getString("JoystickActivity", "scheduler", connections.joystickActivity.scheduler);
		}
		if(parameter.checkIfParameterExists("JoystickActivity", "priority")) {
			parameter.getInteger("JoystickActivity", "priority", connections.joystickActivity.priority);
		}
		if(parameter.checkIfParameterExists("JoystickActivity", "cpuAffinity")) {
			parameter.getInteger("JoystickActivity", "cpuAffinity", connections.joystickActivity.cpuAffinity);
		}
		
		// load parameters for ComponentRosJoystickROSExtension
		
		// load parameters for SeRoNetSDKComponentGeneratorExtension
		
		// load parameters for PlainOpcUaComponentRosJoystickExtension
		
		
		// load parameters for all registered component-extensions
		for(auto extension = componentExtensionRegistry.begin(); extension != componentExtensionRegistry.end(); extension++) 
		{
			extension->second->loadParameters(parameter);
		}
		
	
	} catch (const SmartACE::IniParameterError & e) {
		std::cerr << e.what() << std::endl;
	} catch (const std::exception &ex) {
		std::cerr << "Uncaught std::exception: " << ex.what() << std::endl;
	} catch (...) {
		std::cerr << "Uncaught exception" << std::endl;
	}
}
