/*
 * generated by Xtext 2.12.0
 */
package de.seronet_projekt.ros.interfaces.pool.serializer;

import com.google.inject.Inject;
import de.seronet_projekt.ros.interfaces.pool.services.RosInterfacesPoolGrammarAccess;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtext.Action;
import org.eclipse.xtext.Parameter;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.serializer.ISerializationContext;
import org.eclipse.xtext.serializer.acceptor.SequenceFeeder;
import org.eclipse.xtext.serializer.sequencer.AbstractDelegatingSemanticSequencer;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService.ValueTransient;
import rosInterfacesPool.RosInterfacesPool;
import rosInterfacesPool.RosInterfacesPoolPackage;
import rosInterfacesPool.RosPublisher;
import rosInterfacesPool.RosSrvClient;
import rosInterfacesPool.RosSrvServer;
import rosInterfacesPool.RosSubscriber;

@SuppressWarnings("all")
public class RosInterfacesPoolSemanticSequencer extends AbstractDelegatingSemanticSequencer {

	@Inject
	private RosInterfacesPoolGrammarAccess grammarAccess;
	
	@Override
	public void sequence(ISerializationContext context, EObject semanticObject) {
		EPackage epackage = semanticObject.eClass().getEPackage();
		ParserRule rule = context.getParserRule();
		Action action = context.getAssignedAction();
		Set<Parameter> parameters = context.getEnabledBooleanParameters();
		if (epackage == RosInterfacesPoolPackage.eINSTANCE)
			switch (semanticObject.eClass().getClassifierID()) {
			case RosInterfacesPoolPackage.ROS_INTERFACES_POOL:
				sequence_RosInterfacesPool(context, (RosInterfacesPool) semanticObject); 
				return; 
			case RosInterfacesPoolPackage.ROS_PUBLISHER:
				sequence_RosPublisher(context, (RosPublisher) semanticObject); 
				return; 
			case RosInterfacesPoolPackage.ROS_SRV_CLIENT:
				sequence_RosSrvClient(context, (RosSrvClient) semanticObject); 
				return; 
			case RosInterfacesPoolPackage.ROS_SRV_SERVER:
				sequence_RosSrvServer(context, (RosSrvServer) semanticObject); 
				return; 
			case RosInterfacesPoolPackage.ROS_SUBSCRIBER:
				sequence_RosSubscriber(context, (RosSubscriber) semanticObject); 
				return; 
			}
		if (errorAcceptor != null)
			errorAcceptor.accept(diagnosticProvider.createInvalidContextOrTypeDiagnostic(semanticObject, context));
	}
	
	/**
	 * Contexts:
	 *     RosInterfacesPool returns RosInterfacesPool
	 *
	 * Constraint:
	 *     interfaces+=RosInterface*
	 */
	protected void sequence_RosInterfacesPool(ISerializationContext context, RosInterfacesPool semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     RosInterface returns RosPublisher
	 *     RosTopic returns RosPublisher
	 *     RosPublisher returns RosPublisher
	 *
	 * Constraint:
	 *     (name=ID topicName=EString type=EString)
	 */
	protected void sequence_RosPublisher(ISerializationContext context, RosPublisher semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, RosInterfacesPoolPackage.Literals.ROS_INTERFACE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, RosInterfacesPoolPackage.Literals.ROS_INTERFACE__NAME));
			if (transientValues.isValueTransient(semanticObject, RosInterfacesPoolPackage.Literals.ROS_TOPIC__TOPIC_NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, RosInterfacesPoolPackage.Literals.ROS_TOPIC__TOPIC_NAME));
			if (transientValues.isValueTransient(semanticObject, RosInterfacesPoolPackage.Literals.ROS_TOPIC__TYPE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, RosInterfacesPoolPackage.Literals.ROS_TOPIC__TYPE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getRosPublisherAccess().getNameIDTerminalRuleCall_1_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getRosPublisherAccess().getTopicNameEStringParserRuleCall_4_0(), semanticObject.getTopicName());
		feeder.accept(grammarAccess.getRosPublisherAccess().getTypeEStringParserRuleCall_6_0(), semanticObject.getType());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     RosInterface returns RosSrvClient
	 *     RosService returns RosSrvClient
	 *     RosSrvClient returns RosSrvClient
	 *
	 * Constraint:
	 *     (name=ID srvName=EString type=EString)
	 */
	protected void sequence_RosSrvClient(ISerializationContext context, RosSrvClient semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, RosInterfacesPoolPackage.Literals.ROS_INTERFACE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, RosInterfacesPoolPackage.Literals.ROS_INTERFACE__NAME));
			if (transientValues.isValueTransient(semanticObject, RosInterfacesPoolPackage.Literals.ROS_SERVICE__SRV_NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, RosInterfacesPoolPackage.Literals.ROS_SERVICE__SRV_NAME));
			if (transientValues.isValueTransient(semanticObject, RosInterfacesPoolPackage.Literals.ROS_SERVICE__TYPE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, RosInterfacesPoolPackage.Literals.ROS_SERVICE__TYPE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getRosSrvClientAccess().getNameIDTerminalRuleCall_1_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getRosSrvClientAccess().getSrvNameEStringParserRuleCall_4_0(), semanticObject.getSrvName());
		feeder.accept(grammarAccess.getRosSrvClientAccess().getTypeEStringParserRuleCall_6_0(), semanticObject.getType());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     RosInterface returns RosSrvServer
	 *     RosService returns RosSrvServer
	 *     RosSrvServer returns RosSrvServer
	 *
	 * Constraint:
	 *     (name=ID srvName=EString type=EString)
	 */
	protected void sequence_RosSrvServer(ISerializationContext context, RosSrvServer semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, RosInterfacesPoolPackage.Literals.ROS_INTERFACE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, RosInterfacesPoolPackage.Literals.ROS_INTERFACE__NAME));
			if (transientValues.isValueTransient(semanticObject, RosInterfacesPoolPackage.Literals.ROS_SERVICE__SRV_NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, RosInterfacesPoolPackage.Literals.ROS_SERVICE__SRV_NAME));
			if (transientValues.isValueTransient(semanticObject, RosInterfacesPoolPackage.Literals.ROS_SERVICE__TYPE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, RosInterfacesPoolPackage.Literals.ROS_SERVICE__TYPE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getRosSrvServerAccess().getNameIDTerminalRuleCall_1_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getRosSrvServerAccess().getSrvNameEStringParserRuleCall_4_0(), semanticObject.getSrvName());
		feeder.accept(grammarAccess.getRosSrvServerAccess().getTypeEStringParserRuleCall_6_0(), semanticObject.getType());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     RosInterface returns RosSubscriber
	 *     RosTopic returns RosSubscriber
	 *     RosSubscriber returns RosSubscriber
	 *
	 * Constraint:
	 *     (name=ID topicName=EString type=EString)
	 */
	protected void sequence_RosSubscriber(ISerializationContext context, RosSubscriber semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, RosInterfacesPoolPackage.Literals.ROS_INTERFACE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, RosInterfacesPoolPackage.Literals.ROS_INTERFACE__NAME));
			if (transientValues.isValueTransient(semanticObject, RosInterfacesPoolPackage.Literals.ROS_TOPIC__TOPIC_NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, RosInterfacesPoolPackage.Literals.ROS_TOPIC__TOPIC_NAME));
			if (transientValues.isValueTransient(semanticObject, RosInterfacesPoolPackage.Literals.ROS_TOPIC__TYPE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, RosInterfacesPoolPackage.Literals.ROS_TOPIC__TYPE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getRosSubscriberAccess().getNameIDTerminalRuleCall_1_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getRosSubscriberAccess().getTopicNameEStringParserRuleCall_4_0(), semanticObject.getTopicName());
		feeder.accept(grammarAccess.getRosSubscriberAccess().getTypeEStringParserRuleCall_6_0(), semanticObject.getType());
		feeder.finish();
	}
	
	
}
