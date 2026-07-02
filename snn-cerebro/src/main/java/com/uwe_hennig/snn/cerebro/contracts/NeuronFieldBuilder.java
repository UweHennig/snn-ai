/**
 * @(#)NeuronFieldBuilder.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.contracts;

import java.util.function.Consumer;

/**
 * NeuronFieldBuilder
 *
 * @author Uwe Hennig
 */
public interface NeuronFieldBuilder {
    NeuronFieldBuilder withAfferent   (int count, Consumer<NeuronFieldBuilder> each);
    NeuronFieldBuilder withAssociative(int count, Consumer<NeuronFieldBuilder> each);
    NeuronFieldBuilder withEfferent   (int count, Consumer<NeuronFieldBuilder> each);
    NeuronFieldBuilder withFeedback   (int count, Consumer<NeuronFieldBuilder> each);

    FieldGraph build();
}
