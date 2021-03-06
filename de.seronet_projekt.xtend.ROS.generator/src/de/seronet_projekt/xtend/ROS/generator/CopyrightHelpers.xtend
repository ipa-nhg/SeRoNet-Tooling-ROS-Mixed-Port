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

import org.eclipse.core.runtime.Platform

class CopyrightHelpers {
	def private String getVersion() {
		return Platform.getBundle("org.xtend.smartsoft.generator").version.toString
	}
	
	def String getToolchainVersionFileString()
	'''
	«copyright»
	
	// Generated with SmartSoft MDSD Toolchain Version «version»
	'''
	
	
	def String copyrightHelper() '''
		//--------------------------------------------------------------------------
		// This file is generated by the SeRoNet Tooling. The SeRoNet Tooling is 
		// developed by the SeRoNet Project consortium: 
		// http://www.seronet-projekt.de
		//
		// The ROS Mixed-Port Component is developed by:
		// Service Robotics Research Center of Ulm University of Applied Sciences
		// Fraunhofer Institute for Manufacturing Engineering and Automation IPA
		//
		// This code-generator uses infrastructure of the SmartMDSD Toolchain on
		// which the SeRoNet Tooling is based on.
		//
		// CAUTION: 
		// This software is a preview for the purpose of technology demonstration. 
		// It is experimental and comes with no support. Use at your own risk.
	'''

	// returns copyright for source files
	def String getCopyright() '''
		«copyrightHelper()»
		// Please do not modify this file. It will be re-generated
		// running the code generator.
		//--------------------------------------------------------------------------
	'''

	// returns copyright for source files which are generated once
	def String getCopyrightWriteOnce() '''
		«copyrightHelper()» 
		// This file is generated once. Modify this file to your needs. 
		// If you want the toolchain to re-generate this file, please 
		// delete it before running the code generator.
		//--------------------------------------------------------------------------
	'''
	
	
	// returns copyright for files which need #
	def String getCopyrightHash() {
		return getCopyright().replaceAll("//","#");
	}
	
	
	// returns copyright for files which need # and are generated once
	def String getCopyrightWriteOnceHash() {
		getCopyrightWriteOnce().replaceAll("//","#");
	}
}
