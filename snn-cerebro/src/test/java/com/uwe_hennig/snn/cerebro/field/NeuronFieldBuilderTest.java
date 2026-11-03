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
        NeuronFieldBuilderImpl nfbi = new NeuronFieldBuilderImpl();
        FieldCompileResult compiled = nfbi.withReceptorFields(1, 0, 0.1f)
            .connectReceptorFields(2)
            .withAssociativeFields(2, 2, 1f)
            .connectAssociativeFields(1)
            .withEffectorFields(3, 1, 1f)
            .connectEffectorFields(1)
            .withFeedbackFields(4, 2, 1f)
            .connectFeedbackFields(10)
            .compile();

        assertTrue(compiled.isSuccess(), "invalad field compilations");

        String dotString = compiled.toDot("Test");
        assertTrue(dotString != null && !dotString.isBlank(), "dto string not generated");
        convertDotStringToPng(dotString, new File(PNG_FILE));

        System.out.println(dotString);

        assertEquals(10, compiled.numFields());

        // TODO not yet implemented
        //        System.out.println(compiled.totalBytes());
        //        assertTrue(compiled.totalBytes() > 130, "invalid totalBytes");
        //
        //        assertEquals(136, compiled.maxNeighbours());
    }

    public static void convertDotFileToPng(File dotFile, File pngPath) {
        try {
            Parser parser = new Parser();
            Graphviz.fromGraph(parser.read(dotFile))
                .render(guru.nidi.graphviz.engine.Format.PNG)
                .toFile(pngPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void convertDotStringToPng(String dotString, File pngFile) {
        try {
            Graphviz.fromString(dotString)
                .render(Format.PNG)
                .toFile(pngFile);
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
