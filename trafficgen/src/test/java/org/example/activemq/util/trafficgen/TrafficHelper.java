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
import java.util.List;
import java.util.Random;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.builder.ExpressionBuilder;


public final class TrafficHelper {
    private static final Random RANDOM = new Random();

    /*
     * Implementation of a Camel Expression used for generating a random
     *  value between 0 and the result of evaluating another expression
     */
    public static Expression random(final Expression eval) {
        return new Expression()  {
            @SuppressWarnings("unchecked")
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                Integer count = eval.evaluate(exchange, Integer.class);
                return (T)(Integer)RANDOM.nextInt(count);
            }
        };
    }

    /* 
     * Implementation of a Camel Expression that returns a List of copies 
     *  of the input Message body. The number of copies is determined by 
     *  evaluating yet another expression.
     */
    public static Expression copies(final Expression counter) {
        return copies(counter, ExpressionBuilder.bodyExpression());
    }

    /* 
     * Implementation of a Camel Expression that returns a List of copies 
     *  of a source. The source object is determine by evaluating another
     *  Camel Expression, The number of copies is also determined by 
     *  evaluating yet another expression.
     */
    public static Expression copies(final Expression counter, final Expression source) {
        return new Expression()  {
            @SuppressWarnings("unchecked")
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                Integer count = counter.evaluate(exchange, Integer.class);
                if (count != null && type.isAssignableFrom(List.class)) {
                    Object body = source.evaluate(exchange, Object.class);
                    List<Object> copies = new ArrayList<Object>(count);
                    for (int i = 0; i < count; i++) {
                        copies.add(body);
                    }
                    return (T)copies;
                }
                return null;
            }
        };
    }

}
