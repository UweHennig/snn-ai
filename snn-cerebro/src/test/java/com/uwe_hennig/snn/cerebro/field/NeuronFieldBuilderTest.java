/// @(#)NeuronFieldBuilderTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.cerebro.contracts.FieldCompileResult;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.parse.Parser;

/// NeuronFieldBuilderTest
///
/// @author Uwe Hennig
///
public class NeuronFieldBuilderTest {
    private static final String PNG_FILE = "NeuronFieldBuilderTest.png";

    @Test
    public void compileTest() {
        final int numReceptorFields = 2;
        final int numAssociativeFields = 3;
        final int numEffectorFields = 1;
        final int numFeedbackFields = 5;
        final int totalFields = numReceptorFields + numAssociativeFields + numEffectorFields + numFeedbackFields;

        NeuronFieldBuilderImpl nfbi = new NeuronFieldBuilderImpl();
        FieldCompileResult compiled = nfbi
            .withReceptorFields(numReceptorFields, 2, 0.1f).connectReceptor(2)
            .withAssociativeFields(numAssociativeFields, 1, 0.2f).connectAssociative(2, 1)
            .withEffectorFields(numEffectorFields, 0, 0.3f).connectEffector(1)
            .withFeedbackFields(numFeedbackFields, 2, 0.4f).connectFeedback(10)
            .compile();

        if (!compiled.isSuccess()) {
            System.out.println(compiled.getErrorMessage());
            assertTrue(compiled.isSuccess(), "invalad field compilations");
        }
        System.out.println("fields = " + compiled.numFields());
        System.out.println("max neighbours = " + compiled.maxNeighbours());
        System.out.println("bytes = " + compiled.totalBytes());

        String dotString = compiled.toDot("Test");

        assertTrue(dotString != null && !dotString.isBlank(), "dto string not generated");
        convertDotStringToPng(dotString, new File(PNG_FILE));

        assertEquals(totalFields, compiled.numFields(), "invalid number of fields");

        assertTrue(compiled.totalBytes() > 100, "invalid totalBytes");
        assertEquals(10, compiled.maxNeighbours());
    }

    public static void convertDotFileToPng(File dotFile, File pngPath) {
        try {
            Parser parser = new Parser();
            Graphviz.fromGraph(parser.read(dotFile)).render(guru.nidi.graphviz.engine.Format.PNG).toFile(pngPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void convertDotStringToPng(String dotString, File pngFile) {
        try {
            Graphviz.fromString(dotString).render(Format.PNG).toFile(pngFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));

        File pngFile = new File(PNG_FILE);
        if (pngFile.exists()) {
            pngFile.delete();
        }
    }

    @AfterEach
    public void clearEach() {
    }

    @BeforeAll
    public static void beforeAll() {
        System.setProperty("snn.logging", "true");
    }

    @AfterAll
    public static void afterAll() {
        System.setProperty("snn.logging", "false");
    }
}
