package de.seronet_projekt.xtend.ROS.generator

import com.google.inject.Inject
import org.ecore.component.componentDefinition.ComponentDefinition

class ROS_CMake {
	@Inject extension CopyrightHelpers;
	@Inject extension ROS_ComponentExtension;
	@Inject extension ROS_Callbacks;
	@Inject extension MixedPortROSGenHelpers;
	
	def compileRosCMake(ComponentDefinition comp) 
	'''
	«copyrightHash»
	# %Tag(FULLTEXT)%
	cmake_minimum_required(VERSION 2.8.3)
	project(ros_«comp.name.toLowerCase»)
	
	find_package(catkin REQUIRED COMPONENTS roscpp «FOR pack: comp.allPackageStrings» «pack»«ENDFOR»)
	
	catkin_package()

	LIST(APPEND LOCAL_INCLUDES ${catkin_INCLUDE_DIRS})
	LIST(APPEND LOCAL_INCLUDES ${CMAKE_CURRENT_SOURCE_DIR})
	LIST(APPEND LOCAL_INCLUDES ${CMAKE_CURRENT_SOURCE_DIR}/../src)
	set(ROS_INCLUDES ${LOCAL_INCLUDES} PARENT_SCOPE)
	
	LIST(APPEND SRCS ${CMAKE_CURRENT_SOURCE_DIR}/«comp.rosPortExtensionSourceFilename»)
	«IF comp.hasRosSubscribers || comp.hasRosSvrServers»
	LIST(APPEND SRCS ${CMAKE_CURRENT_SOURCE_DIR}/../src/«comp.rosPortCallbacksUserClassSourceFile»)
	«ENDIF»
	
	set(ROS_SRCS ${SRCS} PARENT_SCOPE)
	
	set(ROS_LIBS ${catkin_LIBRARIES} PARENT_SCOPE)
	
	# %EndTag(FULLTEXT)%
	'''
}