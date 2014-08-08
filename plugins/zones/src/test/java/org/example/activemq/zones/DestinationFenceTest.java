/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example.activemq.zones;


import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.apache.activemq.web.RemoteJMXBrokerFacade;
import org.apache.activemq.web.config.SystemPropertiesConfiguration;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class DestinationFenceTest {
    private static final int PORT_START = 61660;
    private static final int JMX_OFFSET = 1000;
    private static ArrayList<BrokerService> BROKERS = new ArrayList<BrokerService>();

    private static final int QCOUNT = 20;
    private static final String[] QUEUES = { "Q1", "Q2", "Q3", "Q4", "Q5" };


    @BeforeClass
    public static void startBroker() throws Exception {
        createNewBroker();
        createNewBroker();
    }

    @AfterClass
    public static void stopBroker() throws Exception {
    	for (BrokerService b : BROKERS) {
        	if (b != null) {
                b.stop();
            }
    	}
    }

    @Test
    public void testGenerateTestData() throws Exception {
    	Assert.assertTrue(BROKERS.size() > 0);
    	String jmxUri = "service:jmx:rmi://localhost:1099/jndi/rmi://localhost:1099/jmxrmi";

        System.out.println("jmx url: " + jmxUri);
        System.setProperty("webconsole.jmx.url", jmxUri);
        RemoteJMXBrokerFacade brokerFacade = new RemoteJMXBrokerFacade();

        SystemPropertiesConfiguration configuration = new SystemPropertiesConfiguration();
        brokerFacade.setConfiguration(configuration);

        Thread.sleep(1000);
        System.out.println(brokerFacade.getBrokerName());

    }

    public static BrokerService createNewBroker() throws Exception {
    	int index = BROKERS.size();
    	int port = PORT_START + index;
    	String bindAddress = "tcp://localhost:" + port;
    	String name = "BROKER-" + index;

    	BrokerService b = BrokerFactory.createBroker("broker:(" + bindAddress + ")/" + name + "?persistent=false&useJmx=true&deleteAllMessagesOnStartup=true");
        BROKERS.add(b);
        b.start();
        return b;
    }

}
