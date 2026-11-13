/// @(#)CerebroBuilderImpl.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.builder;

import java.util.Optional;

import com.uwe_hennig.snn.cerebro.contracts.Cerebro;
import com.uwe_hennig.snn.cerebro.contracts.CerebroBuilder;
import com.uwe_hennig.snn.cerebro.contracts.CompileResult;
import com.uwe_hennig.snn.cerebro.contracts.FieldStage;

/// CerebroBuilderImpl
///
/// @author Uwe Hennig
///
public class CerebroBuilderImpl implements  CerebroBuilder {
    private CompileResult compileResult = new CompileResult();

    /// @see com.uwe_hennig.snn.cerebro.contracts.CerebroBuilder#fields()
    @Override
    public FieldStage fields() {
        // TODO Cerebro
        return new FieldStageImpl(this, compileResult);
    }

    /// @see com.uwe_hennig.snn.cerebro.contracts.CerebroBuilder#compile()
    @Override
    public CompileResult compile() {
        // TODO
        return compileResult;
    }

    /// @see com.uwe_hennig.snn.cerebro.contracts.CerebroBuilder#reset()
    @Override
    public CerebroBuilder reset() {
        compileResult.reset();
        // TODO Auto-generated method stub class CerebroBuilder
        return null;
    }

    /// @see com.uwe_hennig.snn.cerebro.contracts.CerebroBuilder#build()
    @Override
    public Optional<Cerebro> build() {
        // TODO Auto-generated method stub class CerebroBuilder
        return Optional.empty();
    }

}
