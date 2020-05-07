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

import org.ecore.component.componentDefinition.ComponentDefinition
import org.ecore.component.seronetExtension.MixedPortROS
import rosInterfacesPool.RosTopic
import rosInterfacesPool.RosService
import rosInterfacesPool.RosAction
import rosInterfacesPool.RosActionClient
import rosInterfacesPool.RosActionServer
import rosInterfacesPool.RosPublisher
import rosInterfacesPool.RosSubscriber
import rosInterfacesPool.RosSrvClient
import rosInterfacesPool.RosSrvServer
import java.util.ArrayList

class MixedPortROSGenHelpers {
	
	ArrayList<String> unique_dependencies = new ArrayList<String>();
	
	def Iterable<MixedPortROS> getAllROSPorts(ComponentDefinition comp) {
		return comp.elements.filter(MixedPortROS).sortBy[name]
	}
	
	def String getTypeString(MixedPortROS mpr) {
		val p = mpr.port
		switch(p) {
			RosTopic: p.type
			RosService: p.type
			RosAction: p.type
		}
	}
	
	def String getMessageString(MixedPortROS mpr) {
		val typeString = mpr.typeString
		val dot_idx = typeString.indexOf('.')
		if(dot_idx > 0) {
			return typeString.substring(dot_idx+1)
		}
		return typeString
	}
	
	def String getPackageString(MixedPortROS mpr) {
		val typeString = mpr.typeString
		val dot_idx = typeString.indexOf('.')
		if(dot_idx > 0) {
			return typeString.substring(0, dot_idx)
		}
		return typeString
	}
	
	def Iterable<String> getAllPackageStrings(ComponentDefinition comp) {		
   		for (String element:comp.allROSPorts.map[it.packageString]){
   			if(!unique_dependencies.contains(element)){
   				unique_dependencies.add(element);
   				}
   		}
		return unique_dependencies;
	}

	def Boolean hasRosPublishers(ComponentDefinition comp){
		return comp.allROSPorts.exists[it.port instanceof RosPublisher]
	}

	def Boolean hasRosSubscribers(ComponentDefinition comp){
		return comp.allROSPorts.exists[it.port instanceof RosSubscriber]
	}

	def Boolean hasRosSvrClients(ComponentDefinition comp){
		return comp.allROSPorts.exists[it.port instanceof RosSrvClient]
	}

	def Boolean hasRosSvrServers(ComponentDefinition comp){
		return comp.allROSPorts.exists[it.port instanceof RosSrvServer]
	}
	
	def Boolean hasActionClients(ComponentDefinition comp) {
		return comp.allROSPorts.exists[it.port instanceof RosActionClient]
	}

	def Boolean hasActionServers(ComponentDefinition comp) {
		return comp.allROSPorts.exists[it.port instanceof RosActionServer]
	}

	def Boolean isROSPublisher(MixedPortROS mpr) {
		return (mpr.port instanceof RosPublisher) 
	}

	def Boolean isROSSubscriber(MixedPortROS mpr) {
		return (mpr.port instanceof RosSubscriber) 
	}

	def Boolean isROSSvrClient(MixedPortROS mpr) {
		return (mpr.port instanceof RosSrvClient) 
	}

	def Boolean isROSSvrServer(MixedPortROS mpr) {
		return (mpr.port instanceof RosSrvServer) 
	}

	def Boolean isROSActionClient(MixedPortROS mpr) {
		return (mpr.port instanceof RosSrvClient) 
	}

	def Boolean isROSActionServer(MixedPortROS mpr) {
		return (mpr.port instanceof RosSrvServer) 
	}
	
	def Iterable<MixedPortROS> getROSPublishers(ComponentDefinition comp) {
		return comp.allROSPorts.filter[it.isROSPublisher]
	}

	def Iterable<MixedPortROS> getROSSubscribers(ComponentDefinition comp) {
		return comp.allROSPorts.filter[it.isROSSubscriber]
	}

	def Iterable<MixedPortROS> getROSSrvClient(ComponentDefinition comp) {
		return comp.allROSPorts.filter[it.isROSSvrClient]
	}

	def Iterable<MixedPortROS> getROSSrvServer(ComponentDefinition comp) {
		return comp.allROSPorts.filter[it.isROSSvrServer]
	}
	
	def Iterable<MixedPortROS> getROSActionClient(ComponentDefinition comp) {
		return comp.allROSPorts.filter[it.isROSActionClient]
	}

	def Iterable<MixedPortROS> getROSActionServer(ComponentDefinition comp) {
		return comp.allROSPorts.filter[it.isROSActionServer]
	}
}