/*
 * generated by Xtext 2.12.0
 */
package de.seronet_projekt.ros.interfaces.pool.ide.contentassist.antlr;

import com.google.inject.Inject;
import de.seronet_projekt.ros.interfaces.pool.ide.contentassist.antlr.internal.InternalRosInterfacesPoolParser;
import de.seronet_projekt.ros.interfaces.pool.services.RosInterfacesPoolGrammarAccess;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.ide.editor.contentassist.antlr.AbstractContentAssistParser;

public class RosInterfacesPoolParser extends AbstractContentAssistParser {

	@Inject
	private RosInterfacesPoolGrammarAccess grammarAccess;

	private Map<AbstractElement, String> nameMappings;

	@Override
	protected InternalRosInterfacesPoolParser createParser() {
		InternalRosInterfacesPoolParser result = new InternalRosInterfacesPoolParser(null);
		result.setGrammarAccess(grammarAccess);
		return result;
	}

	@Override
	protected String getRuleName(AbstractElement element) {
		if (nameMappings == null) {
			nameMappings = new HashMap<AbstractElement, String>() {
				private static final long serialVersionUID = 1L;
				{
					put(grammarAccess.getRosInterfaceAccess().getAlternatives(), "rule__RosInterface__Alternatives");
					put(grammarAccess.getRosTopicAccess().getAlternatives(), "rule__RosTopic__Alternatives");
					put(grammarAccess.getRosServiceAccess().getAlternatives(), "rule__RosService__Alternatives");
					put(grammarAccess.getRosInterfacesPoolAccess().getGroup(), "rule__RosInterfacesPool__Group__0");
					put(grammarAccess.getRosPublisherAccess().getGroup(), "rule__RosPublisher__Group__0");
					put(grammarAccess.getRosSubscriberAccess().getGroup(), "rule__RosSubscriber__Group__0");
					put(grammarAccess.getRosSrvServerAccess().getGroup(), "rule__RosSrvServer__Group__0");
					put(grammarAccess.getRosSrvClientAccess().getGroup(), "rule__RosSrvClient__Group__0");
					put(grammarAccess.getRosInterfacesPoolAccess().getInterfacesAssignment_3(), "rule__RosInterfacesPool__InterfacesAssignment_3");
					put(grammarAccess.getRosPublisherAccess().getNameAssignment_1(), "rule__RosPublisher__NameAssignment_1");
					put(grammarAccess.getRosPublisherAccess().getTopicNameAssignment_4(), "rule__RosPublisher__TopicNameAssignment_4");
					put(grammarAccess.getRosPublisherAccess().getTypeAssignment_6(), "rule__RosPublisher__TypeAssignment_6");
					put(grammarAccess.getRosSubscriberAccess().getNameAssignment_1(), "rule__RosSubscriber__NameAssignment_1");
					put(grammarAccess.getRosSubscriberAccess().getTopicNameAssignment_4(), "rule__RosSubscriber__TopicNameAssignment_4");
					put(grammarAccess.getRosSubscriberAccess().getTypeAssignment_6(), "rule__RosSubscriber__TypeAssignment_6");
					put(grammarAccess.getRosSrvServerAccess().getNameAssignment_1(), "rule__RosSrvServer__NameAssignment_1");
					put(grammarAccess.getRosSrvServerAccess().getSrvNameAssignment_4(), "rule__RosSrvServer__SrvNameAssignment_4");
					put(grammarAccess.getRosSrvServerAccess().getTypeAssignment_6(), "rule__RosSrvServer__TypeAssignment_6");
					put(grammarAccess.getRosSrvClientAccess().getNameAssignment_1(), "rule__RosSrvClient__NameAssignment_1");
					put(grammarAccess.getRosSrvClientAccess().getSrvNameAssignment_4(), "rule__RosSrvClient__SrvNameAssignment_4");
					put(grammarAccess.getRosSrvClientAccess().getTypeAssignment_6(), "rule__RosSrvClient__TypeAssignment_6");
				}
			};
		}
		return nameMappings.get(element);
	}
			
	@Override
	protected String[] getInitialHiddenTokens() {
		return new String[] { "RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT" };
	}

	public RosInterfacesPoolGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}

	public void setGrammarAccess(RosInterfacesPoolGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}
