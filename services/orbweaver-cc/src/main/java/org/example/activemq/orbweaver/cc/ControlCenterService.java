/**
 */
package org.example.activemq.orbweaver.cc;

import org.example.activemq.orbweaver.api.Broker;
import org.example.activemq.orbweaver.api.Brokers;
import org.example.activemq.orbweaver.api.ControlCenter;


/**
 * JAX-RS ControlCenter root resource
 */
public class ControlCenterService implements ControlCenter {
	// Dummy implementation to test service connectivity

	public Brokers showBrokers() {
		Broker broker;
		Brokers answer = new Brokers();

		broker = new Broker();
		broker.setId("abcd");
		answer.getBrokers().add(broker);
		broker = new Broker();
		broker.setId("1234");
		answer.getBrokers().add(broker);

		return answer;
	}

	public Broker showBroker(String brokerid) {
		Broker answer = new Broker();
		answer.setId("abcd");
		return answer;
	}

	public void updateBroker(String brokerid, Broker brokertype) {
		// TODO Auto-generated method stub
		
	}

	public void deleteBroker(String brokerid) {
		// TODO Auto-generated method stub
		
	}

}
