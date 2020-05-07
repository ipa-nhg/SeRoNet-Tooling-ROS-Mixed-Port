//--------------------------------------------------------------------------
//
//  Copyright (C) 2018 Alex Lotz, Dennis Stampfer, Matthias Lutz
//
//        lotz@hs-ulm.de
//        stampfer@hs-ulm.de
//        lutz@hs-ulm.de
//
//        Servicerobotics Ulm
//        Christian Schlegel
//        University of Applied Sciences
//        Prittwitzstr. 10
//        89075 Ulm
//        Germany
//
//  This file is part of the SmartSoft MDSD Toolchain. 
//
// Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
// 
// 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the 
//    documentation and/or other materials provided with the distribution.
// 
// 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this 
//    software without specific prior written permission.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
// THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
// BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
// POSSIBILITY OF SUCH DAMAGE.
//
//--------------------------------------------------------------------------
package de.seronet_projekt.xtend.ROS.generator

import com.google.inject.Inject
import rosInterfacesPool.RosSubscriber
import rosInterfacesPool.RosSrvServer
import org.ecore.component.componentDefinition.ComponentDefinition
import org.xtend.smartsoft.generator.component.SmartComponent
import org.ecore.component.componentDefinition.Activity
import org.ecore.component.seronetExtension.MixedPortROSLink

class ROS_Callbacks {
	@Inject extension CopyrightHelpers;
	@Inject extension SmartComponent;
	@Inject extension MixedPortROSGenHelpers;
	
	def getRosPortCallbacksInterfaceHeaderFile(ComponentDefinition comp) {
		comp.name+"RosPortCallbacksInterface.hh"
	}
	
	def getRosPortCallbacksUserClassHeaderFile(ComponentDefinition comp) {
		comp.name+"RosPortCallbacks.hh"
	}
	def getRosPortCallbacksUserClassSourceFile(ComponentDefinition comp) {
		comp.name+"RosPortCallbacks.cc"
	}
	
	
	def compileRosPortCallbacksInterfaceHeader(ComponentDefinition comp) 
	'''
	«getCopyright()»
	
	#ifndef ROS_PORT_CALLBACKS_INTERFACE_H_
	#define ROS_PORT_CALLBACKS_INTERFACE_H_
	
	#include <ros/ros.h>
	
	«FOR port: comp.allROSPorts»
	#include <«port.packageString»/«port.messageString».h>
	«ENDFOR»
	
	class «comp.name»RosPortCallbacksInterface {
	public:
		«comp.name»RosPortCallbacksInterface() { };
		virtual ~«comp.name»RosPortCallbacksInterface() { }
		
		«FOR sub: comp.allROSPorts.filter[it.port instanceof RosSubscriber]»
			virtual void «sub.name»_cb (const «sub.packageString»::«sub.messageString»::ConstPtr &msg) = 0;
			
		«ENDFOR»
		«FOR srvClient: comp.allROSPorts.filter[it.port instanceof RosSrvServer]»
			virtual bool «srvClient.name»_cb («srvClient.packageString»::«srvClient.messageString»::Request &req, «srvClient.packageString»::«srvClient.messageString»::Response &res) = 0;
			
		«ENDFOR»
	};
	
	#endif // ROS_PORT_CALLBACKS_INTERFACE_H_
	'''
	
	def compileRosPortCallbacksUserClassHeader(ComponentDefinition comp) 
	'''
	«getCopyright()»
	
	#ifndef ROS_PORT_CALLBACKS_H_
	#define ROS_PORT_CALLBACKS_H_
	
	#include "«comp.rosPortCallbacksInterfaceHeaderFile»"
	
	class «comp.name»RosPortCallbacks : public «comp.name»RosPortCallbacksInterface {
	public:
		«comp.name»RosPortCallbacks();
		virtual ~«comp.name»RosPortCallbacks();
		
		«FOR sub: comp.allROSPorts.filter[it.port instanceof RosSubscriber]»
			virtual void «sub.name»_cb (const «sub.packageString»::«sub.messageString»::ConstPtr &msg) override;
			
		«ENDFOR»
		«FOR srvClient: comp.allROSPorts.filter[it.port instanceof RosSrvServer]»
			virtual bool «srvClient.name»_cb («srvClient.packageString»::«srvClient.messageString»::Request &req, «srvClient.packageString»::«srvClient.messageString»::Response &res) override;
			
		«ENDFOR»
	};
	
	#endif // ROS_PORT_CALLBACKS_H_
	'''
	
	def compileRosPortCallbacksUserClassSource(ComponentDefinition comp) 
	'''
	«getCopyright()»
	
	#include "«comp.rosPortCallbacksUserClassHeaderFile»"
	
	// include component's main class
	#include "«comp.compHeaderFilename»"
	
	«comp.name»RosPortCallbacks::«comp.name»RosPortCallbacks() {  }
	
	«comp.name»RosPortCallbacks::~«comp.name»RosPortCallbacks() {  }
	
	«FOR sub: comp.allROSPorts.filter[it.port instanceof RosSubscriber]»
		void «comp.name»RosPortCallbacks::«sub.name»_cb (const «sub.packageString»::«sub.messageString»::ConstPtr &msg)
		{
			// for implementing this method, you can use the "COMP->" macro to access the component's class members
			«FOR activity: comp.elements.filter(Activity)»
				«FOR link: activity.links.filter(MixedPortROSLink).filter[it.mixedportros == sub]»
				COMP->«activity.name.toFirstLower»->«sub.name»_cb(msg);
				«ENDFOR»
			«ENDFOR»
			
		}
		
	«ENDFOR»
	
	«FOR srvClient: comp.allROSPorts.filter[it.port instanceof RosSrvServer]»
		bool «comp.name»RosPortCallbacks::«srvClient.name»_cb («srvClient.packageString»::«srvClient.messageString»::Request &req, «srvClient.packageString»::«srvClient.messageString»::Response &res)
		{
			// for implementing this method, you can use the "COMP->" macro to access the component's class members
			«FOR activity: comp.elements.filter(Activity)»
			«FOR link: activity.links.filter(MixedPortROSLink).filter[it.mixedportros == srvClient]»
			 res= COMP->«activity.name.toFirstLower»->«srvClient.name»_callServiceQuery(req);
			«ENDFOR»
			«ENDFOR»
			return true;
		}
		
	«ENDFOR»
	'''
}
