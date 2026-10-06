/// @(#)MetaDataAccessor.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/// MetaDataAccessor
///  MetaInfo (Header Layout)
///   00: magic           : int    // Magic number for identifying the file type
///   04: version         : int    // File format version
///   08: metaSize        : int    // Size of this header in bytes
///   12: entrySize       : int    // Fixed size of a single entry in bytes
///   16: totalSize       : int    // Total physical size in bytes
///   20: maxEntries      : int    // Maximum number of entries
///   24: entryCount      : int    // Current number of entries written
///   28: endOffset       : int    // Byte pointer to the current end of the payload
///
/// @author Uwe Hennig
///
public abstract class MetaDataAccessor {
    static final int META_SIZE  = 32;
    abstract MemorySegment getSegment();

    public int getMagic() {
        return getSegment().get(ValueLayout.JAVA_INT, 0L);
    }

    public void setMagic(int magic) {
        getSegment().set(ValueLayout.JAVA_INT, 0L, magic);
    }

    public int getVersion() {
        return getSegment().get(ValueLayout.JAVA_INT, 4L);
    }

    public void setVersion(int version) {
        getSegment().set(ValueLayout.JAVA_INT, 4L, version);
    }

    public int getMetaSize() {
        return getSegment().get(ValueLayout.JAVA_INT, 8L);
    }

    public void setMetaSize(int metaSize) {
        getSegment().set(ValueLayout.JAVA_INT, 8L, metaSize);
    }

    public int getEntrySize() {
        return getSegment().get(ValueLayout.JAVA_INT, 12L);
    }

    public void setEntrySize(int entrySize) {
        getSegment().set(ValueLayout.JAVA_INT, 12L, entrySize);
    }

    public int getTotalSize() {
        return getSegment().get(ValueLayout.JAVA_INT, 16L);
    }

    public void setTotalSize(int entrySize) {
        getSegment().set(ValueLayout.JAVA_INT, 16L, entrySize);
    }

    public int getMaxEntries() {
        return getSegment().get(ValueLayout.JAVA_INT, 20L);
    }

    public void setMaxEntries(int entrySize) {
        getSegment().set(ValueLayout.JAVA_INT, 20L, entrySize);
    }

    public int getEntryCount() {
        return getSegment().get(ValueLayout.JAVA_INT, 24L);
    }

    public void setEntryCount(int entrySize) {
        getSegment().set(ValueLayout.JAVA_INT, 24L, entrySize);
    }

    public int getEndOffset() {
        return getSegment().get(ValueLayout.JAVA_INT, 28L);
    }

    public void setEndOffset(int endOffset) {
        getSegment().set(ValueLayout.JAVA_INT, 28L, endOffset);
    }

    public int offsetFromIndex(int index) {
        int maxEntries = getMaxEntries();
        assert index >=0 && index <= maxEntries : "invalid index";

        return getMetaSize() + index * getEntrySize();
    }

    public int claimSlot() {
        // lock not required!
        int newOffset = getEndOffset();
        assert newOffset >= META_SIZE : "invalid initialisation!";

        int entrySize = getEntrySize();
        setEndOffset(newOffset + entrySize);

        int entryCount = getEntryCount();
        setEntryCount(entryCount + 1);

        return newOffset;
    }

}
