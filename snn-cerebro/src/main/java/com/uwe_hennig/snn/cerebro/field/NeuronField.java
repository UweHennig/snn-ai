/**
 * @(#)NeuronField.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.field;

/**
 * NeuronField
 *
 * @author Uwe Hennig
 */
public class NeuronField {

    private NeuronField() {
    }

//    public void visit(Consumer<NeuronField> visitor) {
//        Deque<Integer> queue = new ArrayDeque<>();
//        Set<Integer> visited = new HashSet<>();
//
//        queue.add(this.getViewId());
//
//        while (!queue.isEmpty()) {
//            int currentIdx = queue.poll();
//
//            if (visited.add(currentIdx)) {
//                NeuronField current = createWrapper(currentIdx);
//                visitor.accept(current);
//
//                int[] all = getAllNeighbourIds(current);
//
//                for (int next : all) {
//                    queue.add(next);
//                }
//            }
//        }
//    }
}
