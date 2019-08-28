package de.seronet_projekt.ros.communicationObjectsTransformation.generator

import de.seronet_projekt.ros.componentGateway.generator.CustomOutputProvider
import java.util.Arrays
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext
import primitives.impl.ArrayTopicSpecRefImpl
import primitives.impl.TopicSpecRefImpl
import primitives.impl.boolArrayImpl
import primitives.impl.boolImpl
import primitives.impl.float32ArrayImpl
import primitives.impl.float32Impl
import primitives.impl.float64ArrayImpl
import primitives.impl.float64Impl
import primitives.impl.int16ArrayImpl
import primitives.impl.int16Impl
import primitives.impl.int32ArrayImpl
import primitives.impl.int32Impl
import primitives.impl.int64ArrayImpl
import primitives.impl.int64Impl
import primitives.impl.int8ArrayImpl
import primitives.impl.int8Impl
import primitives.impl.stringArrayImpl
import primitives.impl.stringImpl
import primitives.impl.uint16ArrayImpl
import primitives.impl.uint16Impl
import primitives.impl.uint32ArrayImpl
import primitives.impl.uint32Impl
import primitives.impl.uint64ArrayImpl
import primitives.impl.uint64Impl
import primitives.impl.uint8ArrayImpl
import primitives.impl.uint8Impl
import primitives.impl.ByteImpl
import primitives.impl.ByteArrayImpl
import primitives.impl.timeImpl
import primitives.impl.durationImpl
import primitives.impl.HeaderImpl
import ros.PackageSet
import ros.impl.ActionSpecImpl
import ros.impl.PackageSetImpl
import ros.impl.ServiceSpecImpl
import ros.impl.TopicSpecImpl
import java.util.List
import java.util.ArrayList

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
class CommunicationObjectsTransformationGenerator extends AbstractGenerator {
 	String resourcepath
 	String repositoryName
 	//String pkg_name
	String pkg_crossref
	String name_crossref
	String data_name
	Class<? extends EObject> rostypeClass
	String crossref_container
	String crossref_repository_name
	int i
	List<String> EnumerationList
	



 	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
 		//if (resource.allContents.toString.contains("spec")){ 			
 			for (packages : resource.allContents.toIterable.filter(PackageSet)){
 				resourcepath = resource.URI.toString()
 				repositoryName = resourcepath.substring(resourcepath.lastIndexOf("/")+1,resourcepath.lastIndexOf(".ros"))
 				//for (package : packages.package){
				fsa.generateFile("ROS"+capitalize(repositoryName)+".types",CustomOutputProvider::DEFAULT_OUTPUT,compile_communication_objects_to_type(packages, capitalize(repositoryName)))
				//}
				fsa.generateFile("ROS"+capitalize(repositoryName)+".services",CustomOutputProvider::DEFAULT_OUTPUT,compile_communication_objects_to_services(packages, capitalize(repositoryName)))
				
			}
 		}//}

		
	def compile_communication_objects_to_type (PackageSet rosPackages, String repositoryName)
	'''
CommObjectsRepository ROS«capitalize(repositoryName)» version 1.0.0 {

«FOR rosPackage:rosPackages.package»
«FOR Spec:rosPackage.spec»
«IF !Arrays.asList("UInt8","UInt16","UInt64","Int8","Int16","UInt32","Int32","Int64","Float","Double","String","Boolean").contains(Spec.name)»
	
«IF Spec.class==TopicSpecImpl && Spec.eContents.length >0»
CommObject «capitalize(rosPackage.name)»_«Spec.name» {
	«FOR message:Spec.eContents()»«clear_enumeration()»
	«FOR msg_part:message.eContents»
	«IF ((getData(msg_part.toString())).length > 0) && (mapROStoSR2(msg_part.eContents().get(0), rosPackage.name, repositoryName).length > 0)»
	«IF msg_part.toString().contains("=")»«addEnum(getData(msg_part.toString()))»
«ELSE»
«getData(msg_part.toString())» : «mapROStoSR2(msg_part.eContents().get(0), rosPackage.name,repositoryName)»
«ENDIF»
«ENDIF»
«ENDFOR»
«ENDFOR»
}
«ENDIF»	
«IF Spec.class==ServiceSpecImpl && Spec.eContents.length >0»
CommObject «capitalize(rosPackage.name)»_«Spec.name»Request {
	«FOR msg_part:Spec.eContents.get(0).eContents»
	«IF ((getData(msg_part.toString())).length > 0) && (mapROStoSR2(msg_part.eContents().get(0), rosPackage.name,repositoryName).length > 0 )»
	«getData(msg_part.toString())» : «mapROStoSR2(msg_part.eContents().get(0), rosPackage.name,repositoryName)»
«ENDIF»
«ENDFOR»
}

CommObject «capitalize(rosPackage.name)»_«Spec.name»Response {
	«FOR msg_part:Spec.eContents.get(1).eContents»
	«IF ((getData(msg_part.toString())).length > 0) && (mapROStoSR2(msg_part.eContents().get(0), rosPackage.name,repositoryName).length > 0 )»
	«getData(msg_part.toString())» : «mapROStoSR2(msg_part.eContents().get(0), rosPackage.name,repositoryName)»
«ENDIF»
«ENDFOR»
}
«ENDIF»
«IF Spec.class==ActionSpecImpl»
«ENDIF»
«IF ! EnumerationList.empty»
Enumeration «capitalize(rosPackage.name)»_«Spec.name»Type {
	«FOR Enum:EnumerationList»
	«Enum» = «i++»
«ENDFOR»
}
	«ENDIF»
«ENDIF»
«ENDFOR»
«ENDFOR»
}
'''

	def void clear_enumeration(){
		EnumerationList = new ArrayList
		i = 0
	}

	def void addEnum(String item){
		EnumerationList.add(item)
	}

	def String getData(String msg_part){
		data_name = msg_part.substring(msg_part.toString().indexOf("Data:")+6,msg_part.toString().length-1)
		if (!data_name.contains("=")){
			return data_name
		} else{
			return data_name.substring(0,data_name.indexOf("="));
		}
	}
	def String mapROStoSR2 (EObject rostype, String pkg_name, String repositoryName){
		rostypeClass = rostype.class
		if (rostypeClass == uint8Impl){
			return "UInt8"
		}
		if (rostypeClass == ByteImpl){
			return "UInt8 //attribute generated from a deprecated ROS Byte type"
		}
		if (rostypeClass == uint16Impl){
			return "UInt16"
		}
		if (rostypeClass == uint64Impl){
			return "UInt64"
		}
		if (rostypeClass == int8Impl){
			return "Int8"
		}
		if (rostypeClass == int16Impl){
			return "Int16"
		}
		if (rostypeClass == uint32Impl){
			return "UInt32"
		}
		if (rostypeClass == int32Impl){
			return "Int32"
		}
		if (rostypeClass == int64Impl){
			return "Int64"
		}
		if (rostypeClass == float32Impl){
			return "Float"
		}
		if (rostypeClass == float64Impl){
			return "Double"
		}
		if (rostypeClass == stringImpl){
			return "String"
		}
		if (rostypeClass == boolImpl){
			return "Boolean"
		}
		if (rostypeClass == uint8ArrayImpl  || rostypeClass == ByteArrayImpl){
			return "UInt8[*]"
		}
		if (rostypeClass == uint16ArrayImpl){
			return "UInt16[*]"
		}
		if (rostypeClass == uint64ArrayImpl){
			return "UInt64[*]"
		}
		if (rostypeClass == int8ArrayImpl){
			return "Int8[*]"
		}
		if (rostypeClass == int16ArrayImpl){
			return "Int16[*]"
		}
		if (rostypeClass == uint32ArrayImpl){
			return "UInt32[*]"
		}
		if (rostypeClass == int32ArrayImpl){
			return "Int32[*]"
		}
		if (rostypeClass == int64ArrayImpl){
			return "Int64[*]"
		}
		if (rostypeClass == float32ArrayImpl){
			return "Float[*]"
		}
		if (rostypeClass == float64ArrayImpl){
			return "Double[*]"
		}
		if (rostypeClass == stringArrayImpl){
			return "String[*]"
		}
		if (rostypeClass == boolArrayImpl){
			return "Boolean[*]"
		}
		if (rostypeClass == HeaderImpl){
			return "ROSRos_core.Std_msgs_Header"
		}
		if (rostypeClass == timeImpl || rostypeClass == durationImpl){
			return "CommBasicObjects.CommTimeStamp"
		}
		
		if (rostypeClass == TopicSpecRefImpl){
			pkg_crossref = rostype.eCrossReferences.get(0).eContainer.toString()
			crossref_container = EcoreUtil.getURI(rostype.eCrossReferences.get(0).eContainer.eContainer as PackageSetImpl).toString
			crossref_repository_name = "ROS"+capitalize((crossref_container.substring((crossref_container.substring(0,crossref_container.indexOf("#/"))).lastIndexOf("/")+1 ,crossref_container.lastIndexOf(".ros"))));
			name_crossref = rostype.eCrossReferences.toString()
			if (("ROS"+repositoryName) == crossref_repository_name){
				return capitalize(pkg_crossref.substring(pkg_crossref.indexOf("name:")+6,pkg_crossref.indexOf(")")))+"_"+name_crossref.substring(name_crossref.indexOf("name:")+6,name_crossref.indexOf(")]"))
			} else{
				return crossref_repository_name+"."+capitalize(pkg_crossref.substring(pkg_crossref.indexOf("name:")+6,pkg_crossref.indexOf(")")))+"_"+name_crossref.substring(name_crossref.indexOf("name:")+6,name_crossref.indexOf(")]"))
			}
		}
		if (rostypeClass == ArrayTopicSpecRefImpl){
			pkg_crossref = rostype.eCrossReferences.get(0).eContainer.toString()
			crossref_container = EcoreUtil.getURI(rostype.eCrossReferences.get(0).eContainer.eContainer as PackageSetImpl).toString
			crossref_repository_name = "ROS"+capitalize((crossref_container.substring((crossref_container.substring(0,crossref_container.indexOf("#/"))).lastIndexOf("/")+1 ,crossref_container.lastIndexOf(".ros"))));
			name_crossref = rostype.eCrossReferences.toString()
			if (("ROS"+repositoryName) == crossref_repository_name){
				return capitalize(pkg_crossref.substring(pkg_crossref.indexOf("name:")+6,pkg_crossref.indexOf(")")))+"_"+name_crossref.substring(name_crossref.indexOf("name:")+6,name_crossref.indexOf(")]"))+"[*]"
			} else{
				return crossref_repository_name+"."+capitalize(pkg_crossref.substring(pkg_crossref.indexOf("name:")+6,pkg_crossref.indexOf(")")))+"_"+name_crossref.substring(name_crossref.indexOf("name:")+6,name_crossref.indexOf(")]"))+"[*]"
			}
		} else {
			return ""
		}}
		
	def String capitalize(String line) {
	   return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}

	def compile_communication_objects_to_services(PackageSet rosPackages, String RepoName)
		'''
ServiceDefRepository ROS«RepoName» version 1.0 {

	«FOR rosPackage:rosPackages.package»
	«FOR Spec:rosPackage.spec»
	«IF !Arrays.asList("UInt8","UInt16","UInt64","Int8","Int16","UInt32","Int32","Int64","Float","Double","String","Boolean").contains(Spec.name)»
	«IF Spec.class==TopicSpecImpl  && Spec.eContents.length >0»
	ForkingServiceDefinition  «Spec.name»Service {
		PushPattern <DataType=«RepoName».«capitalize(rosPackage.name)»_«Spec.name»>
	}

	«ENDIF»
	«IF Spec.class==ServiceSpecImpl  && Spec.eContents.length >0»
	RequestAnswerServiceDefinition «Spec.name»QueryService {
		QueryPattern <
			RequestType = «RepoName».«capitalize(rosPackage.name)»_«Spec.name»Request
			AnswerType = «RepoName».«capitalize(rosPackage.name)»_«Spec.name»Response
		>
	}

			«ENDIF»
			«ENDIF»
			«ENDFOR»
		«ENDFOR»

}
		'''
}
