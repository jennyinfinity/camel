/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jms2.consumer;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms2.Jms2Component;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class Jms11DurableTopicTest extends CamelTestSupport {

    @Test
    public void testDurableTopic() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World");

        MockEndpoint mock2 = getMockEndpoint("mock:result2");
        mock2.expectedBodiesReceived("Hello World");

        // wait a bit and send the message
        Thread.sleep(1000);

        template.sendBody("jms2:topic:foo", "Hello World");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://broker?broker.persistent=false&broker.useJmx=false");
                Jms2Component component = new Jms2Component();
                component.setConnectionFactory(connectionFactory);
                getContext().addComponent("jms2", component);

                from("jms2:topic:foo?clientId=123&durableSubscriptionName=bar")
                    .to("mock:result");

                from("jms2:topic:foo?clientId=456&durableSubscriptionName=bar")
                    .to("mock:result2");
            }
        };
    }

}
