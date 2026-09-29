/// @(#)MetaDataAccessor.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/// MetaDataAccessor
///
/// @author Uwe Hennig
public abstract class MetaDataAccessor extends SnnLockSupport {
    int getMagic() {
        return getSegment().get(ValueLayout.JAVA_INT, 0L);
    }

    void setMagic(int magic) {
        getSegment().set(ValueLayout.JAVA_INT, 0L, magic);
    }

    int getVersion() {
        return getSegment().get(ValueLayout.JAVA_INT, 4L);
    }

    void setVersion(int version) {
        getSegment().set(ValueLayout.JAVA_INT, 4L, version);
    }

    int getMetaSize() {
        return getSegment().get(ValueLayout.JAVA_INT, 8L);
    }

    void setMetaSize(int metaSize) {
        getSegment().set(ValueLayout.JAVA_INT, 8L, metaSize);
    }

    int getCapacity() {
        return getSegment().get(ValueLayout.JAVA_INT, 12L);
    }

    void setCapacity(int capacity) {
        getSegment().set(ValueLayout.JAVA_INT, 12L, capacity);
    }

    int getEntrySize() {
        return getSegment().get(ValueLayout.JAVA_INT, 16L);
    }

    void setEntrySize(int entrySize) {
        getSegment().set(ValueLayout.JAVA_INT, 16L, entrySize);
    }

    int getNumEntries() {
        return getSegment().get(ValueLayout.JAVA_INT, 20L);
    }

    void setNumEntries(int numEntries) {
        getSegment().set(ValueLayout.JAVA_INT, 20L, numEntries);
    }

    int getEndOffset() {
        return getSegment().get(ValueLayout.JAVA_INT, 24L);
    }

    void setEndOffset(int endOffset) {
        getSegment().set(ValueLayout.JAVA_INT, 24L, endOffset);
    }
}
