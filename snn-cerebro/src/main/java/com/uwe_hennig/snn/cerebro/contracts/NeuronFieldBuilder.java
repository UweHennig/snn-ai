/**
 * @(#)NeuronFieldBuilder.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.contracts;

import com.uwe_hennig.snn.contracts.graph.GraphGenerator;

/**
 * NeuronFieldBuilder
 *
 * @author Uwe Hennig
 */
public interface NeuronFieldBuilder {
    interface Start {
        AfferentStage start();
    }

    interface AfferentStage {
        AssociativeStage withAfferent(GraphGenerator generator);
    }

    interface AssociativeStage {
        EfferentStage withAssociative(GraphGenerator generator);
    }

    interface EfferentStage {
        FeedbackStage withEfferent(GraphGenerator generator);
    }

    interface FeedbackStage {
        BuildStage withFeedback(GraphGenerator generator);
    }

    interface BuildStage {
        FieldGraph build();
    }
}