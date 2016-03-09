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
package org.apache.camel.component.ignite.idgen;

import java.net.URI;
import java.util.Map;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.component.ignite.AbstractIgniteEndpoint;
import org.apache.camel.component.ignite.IgniteComponent;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.util.ObjectHelper;
import org.apache.ignite.IgniteAtomicSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ignite ID Generator endpoint.
 */
@UriEndpoint(scheme = "ignite:idgen", title = "Ignite ID Generator", syntax = "ignite:idgen:[name]", label = "nosql,cache,compute", producerOnly = true)
public class IgniteIdGenEndpoint extends AbstractIgniteEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(IgniteIdGenEndpoint.class);

    @UriParam
    @Metadata(required = "true")
    private String name;

    @UriParam
    private Integer batchSize;

    @UriParam(defaultValue = "0")
    private Long initialValue = 0L;

    @UriParam
    private IgniteIdGenOperation operation;

    public IgniteIdGenEndpoint(String endpointUri, URI remainingUri, Map<String, Object> parameters, IgniteComponent igniteComponent) throws Exception {
        super(endpointUri, igniteComponent);
        name = remainingUri.getHost();

        ObjectHelper.notNull(name, "ID Generator name");
    }

    @Override
    public Producer createProducer() throws Exception {
        IgniteAtomicSequence atomicSeq = ignite().atomicSequence(name, initialValue, false);

        if (atomicSeq == null) {
            atomicSeq = ignite().atomicSequence(name, initialValue, true);
            LOG.info("Created AtomicSequence of ID Generator with name {}.", name);
        }

        if (batchSize != null) {
            atomicSeq.batchSize(batchSize);
        }

        return new IgniteIdGenProducer(this, atomicSeq);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("The Ignite Id Generator endpoint doesn't support consumers.");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(Long initialValue) {
        this.initialValue = initialValue;
    }

    public IgniteIdGenOperation getOperation() {
        return operation;
    }

    public void setOperation(IgniteIdGenOperation operation) {
        this.operation = operation;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

}