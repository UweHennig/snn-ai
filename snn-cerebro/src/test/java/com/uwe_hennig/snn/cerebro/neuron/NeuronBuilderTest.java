/**
 * @(#)NeuronBuilderTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * NeuronBuilderTest
 * @author Uwe Hennig
 */
public class NeuronBuilderTest {


  @Test
  @DisplayName("Create neuron test")
  public void buildNeuronTest() {
  }

  @BeforeEach
  public void beforeEach(TestInfo info) {
      headline(info);
  }

    private void headline(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

}
