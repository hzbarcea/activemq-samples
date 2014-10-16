/**
 */
package org.example.activemq.orbweaver.cc;

import org.example.activemq.orbweaver.api.BrokerType;
import org.example.activemq.orbweaver.api.BrokersType;
import org.example.activemq.orbweaver.api.ControlCenter;


/**
 * JAX-RS ControlCenter root resource
 */
public class ControlCenterService implements ControlCenter {
	// Dummy implementation to test service connectivity

	public BrokersType showBrokers() {
		BrokerType broker;
		BrokersType answer = new BrokersType();

		broker = new BrokerType();
		broker.setId("abcd");
		answer.getBroker().add(broker);
		broker = new BrokerType();
		broker.setId("1234");
		answer.getBroker().add(broker);

		return answer;
	}

	public BrokerType showBroker(String brokerid) {
		BrokerType answer = new BrokerType();
		answer.setId("abcd");
		return answer;
	}

	public void updateBroker(String brokerid, BrokerType brokertype) {
		// TODO Auto-generated method stub
		
	}

	public void deleteBroker(String brokerid) {
		// TODO Auto-generated method stub
		
	}

}
