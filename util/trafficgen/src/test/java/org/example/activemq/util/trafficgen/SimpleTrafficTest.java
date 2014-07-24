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
package org.example.activemq.util.trafficgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.apache.camel.component.jms.JmsComponent.jmsComponentAutoAcknowledge;
import static org.example.activemq.util.trafficgen.TrafficHelper.copies;

public class SimpleTrafficTest extends CamelTestSupport {
    private static final boolean LOCAL_BROKER = true;
    private static BrokerService broker;
    private static String bindAddress = ActiveMQConnectionFactory.DEFAULT_BROKER_BIND_URL;

    private static final int QCOUNT = 20;
    private static final String[] QUEUES = { "Q1", "Q2", "Q3", "Q4", "Q5" };


    @BeforeClass
    public static void startBroker() throws Exception {
        broker = createBroker();
    }

    @AfterClass
    public static void stopBroker() throws Exception {
        if (broker != null) {
            broker.stop();
        }
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();
        camelContext.addComponent("jms", jmsComponentAutoAcknowledge(createConnectionFactory()));
        return camelContext;
    }


    @Test
    public void testGenerateTestData() throws Exception {
        getMockEndpoint("mock:result").expectedMinimumMessageCount(1);

        // template.sendBody("jms:queue:example", "Hello World");
        template.sendBody("direct:traffic", null);

        // assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        final Processor oblivion = new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                // send into oblivion
            }
        };
        return new RouteBuilder() {
            public void configure() {
                // produce
                from("direct:traffic").split(initQueues())
                    .setProperty("MQSTrafficGenerator", subqueuesDistribution(body()))
                    .to("direct:next1");
                from("direct:next1").split(extendQueues(body(), QCOUNT))
                    .setProperty("MQSTrafficGenerator", subqueuesDistribution(body()))
                    .to("direct:next");

                // produce
                from("direct:next").split(initSubQueues(body()))
                    .to("direct:follow");

                // from("direct:follow")
                from("direct:follow").split(copies(distribution(body())))
                    // .to("log:org.example.activemq")
                    .setHeader("CamelJmsDestinationName", body())
                    .setBody(constant("Hello World"))
                    .to("jms:queue:example");

                // listen on all queues
                from("jms:queue:>")
                    .process(oblivion);
                    // .to("mock:result");
            }
        };
    }

    public static Expression initQueues() {
        return new Expression()  {
            @SuppressWarnings("unchecked")
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                return (T)Arrays.asList(QUEUES);
            }
        };
    }

    public static Expression extendQueues(final Expression eval, final int count) {
        return new Expression()  {
            @SuppressWarnings("unchecked")
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                String q = eval.evaluate(exchange, String.class);
                List<Object> destinations = new ArrayList<Object>(count);
                for (int i = 0; i < count; i++) {
                    destinations.add(String.format("%s%04d", q, i));
                }
                return (T)destinations;
            }
        };
    }

    public static Expression initSubQueues(final Expression eval) {
        return new Expression()  {
            @SuppressWarnings("unchecked")
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                String q = eval.evaluate(exchange, String.class);
                int count = exchange.getProperty("MQSTrafficGenerator", Integer.class);
                List<Object> destinations = new ArrayList<Object>(count);
                for (int i = 0; i < count; i++) {
                    destinations.add(String.format("%s.S%03d", q, i + 1));
                }
                return (T)destinations;
            }
        };
    }

    public static Expression subqueuesDistribution(final Expression eval) {
        return new Expression()  {
            @SuppressWarnings("unchecked")
            @Override
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                String key = eval.evaluate(exchange, String.class);
                return (T)(Integer)(key.length() > 1 ? (byte)key.charAt(1) - 0x30 : 0);
            }
        };
    }
    public static Expression distribution(final Expression eval) {
        return new Expression()  {
            @SuppressWarnings("unchecked")
            @Override
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                String key = eval.evaluate(exchange, String.class);
                Integer count = key.startsWith("Q1") ? 8
                    : key.startsWith("Q2") ? 4
                    : key.startsWith("Q3") ? 2 : 0;
                return (T)count;
            }
        };
    }

    protected ActiveMQConnectionFactory createConnectionFactory() throws Exception {
        return new ActiveMQConnectionFactory(bindAddress);
    }

    public static BrokerService createBroker() throws Exception {
        BrokerService b = null;
        if (LOCAL_BROKER) {
            b = BrokerFactory.createBroker("broker:(" + bindAddress + ")/EXAMPLE?persistent=false&useJmx=false&deleteAllMessagesOnStartup=true");
            b.start();
        }
        return b;
    }

}
