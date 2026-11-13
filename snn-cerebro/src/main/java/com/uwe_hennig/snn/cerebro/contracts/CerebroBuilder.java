/// @(#)CerebroBuilder.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.contracts;

import java.util.Optional;

/// CerebroBuilder
///
/// @author Uwe Hennig
///
public interface CerebroBuilder {
    // 1. Main entry point
    static CerebroBuilder create() {
        return InstanceHelper.create(CerebroBuilder.class, "com.uwe_hennig.snn.cerebro.builder.CerebroBuilderImpl");
    }

    // 2. Creating fields with neurons
    FieldStage fields();

    // 3. Check the overall result
    CompileResult compile();

    // 4. Reset if compile fails
    CerebroBuilder reset();

    // 5. Finally, generate everything
    Optional<Cerebro> build();
}
