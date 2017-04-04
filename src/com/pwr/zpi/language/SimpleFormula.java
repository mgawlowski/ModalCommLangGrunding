package com.pwr.zpi.language;

import com.pwr.zpi.Object;
import com.pwr.zpi.State;
import com.pwr.zpi.Trait;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SimpleFormula is understood as atomic formula and it's used as part of ComplexFormula.
 */
public class SimpleFormula extends Formula {

    Object object;
    Trait trait;
    boolean isNegated;

    public SimpleFormula(Object object, Trait trait, boolean isNegated) {
        this.object = object;
        this.trait = trait;
        this.isNegated = isNegated;
    }

    public SimpleFormula(Object object, Trait trait) {
        this(object, trait, false);
    }

    public SimpleFormula(Object o, Set<Trait> traits, List<State> statesSeq) {
        /*build formula like: state1(trait1(o))*/ //todo
    }

    @Override
    public Set<Trait> getTraits() {
        return new HashSet<Trait>(Arrays.asList(trait));
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public Type getType() {
        return null;
    }

    /**
     * Used to determine whether given object has given trait
     * by returning its State - trait IS or IS_NOT occurring in object.
     *
     * @return State of trait's occurrence in object (IS, IS_NOT).
     */
    public State evaluate() {
        State result = object.hasTrait(trait);

        if(isNegated)  // when isNegated is true reverse result
            switch (result) {
                case IS:
                    return State.IS_NOT;
                case IS_NOT:
                    return State.IS;
            }

        return result;
    }

}
