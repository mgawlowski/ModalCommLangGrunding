package com.pwr.zpi.language;

import com.pwr.zpi.*;

import java.util.List;

/**
 *
 */
public abstract class Formula {

    public abstract List<Trait> getTraits();

    public abstract IndividualModel getModel();

    public abstract Type getType();

    public abstract List<State> getStates();

    /**
     * Gives list of successive states. Classic case will contains states which describe whether parts of formula[traits]
     * (in case of complex formula - simple formula is special case and contains one part) occur with or without negation.
     * The order of returned states is respective to order of traits returned by getTraits().
     * @return List of states.
     */

    public enum Type {
        SIMPLE_MODALITY,
        MODAL_CONJUNCTION
    }

    abstract boolean equals(Formula other);

/*    abstract com.pwr.zpi.Object getAffectedObject();

    *//**
     * Returns traits affected in formula - only one trait in case of SimpleFormula; two or more in case of ComplexFormula.
     * @return Collections of affected traits.
     *//*
    abstract Collection<Trait> getAffectedTraits();*/
}
