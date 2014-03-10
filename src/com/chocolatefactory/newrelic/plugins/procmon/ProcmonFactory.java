package com.chocolatefactory.newrelic.plugins.procmon;

import java.util.Map;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.AgentFactory;
import com.newrelic.metrics.publish.configuration.ConfigurationException;

public class ProcmonFactory extends AgentFactory {

	public ProcmonFactory(String agentConfigFileName) {
		super(agentConfigFileName);
		// TODO Auto-generated constructor stub
	}

	public ProcmonFactory() {
		// TODO Auto-generated constructor stub
		super("ProcmonPlugin.config");
	}

	@Override
	public Agent createConfiguredAgent(Map<String, Object> properties)
			throws ConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}
}
