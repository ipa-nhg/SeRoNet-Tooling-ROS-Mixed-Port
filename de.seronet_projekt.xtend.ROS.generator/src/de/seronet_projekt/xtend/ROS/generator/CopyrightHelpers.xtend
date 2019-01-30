package de.seronet_projekt.xtend.ROS.generator

import org.eclipse.core.runtime.Platform

class CopyrightHelpers {
	def private String getVersion() {
		return Platform.getBundle("org.xtend.SeRoNetSDK.generator").version.toString
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
