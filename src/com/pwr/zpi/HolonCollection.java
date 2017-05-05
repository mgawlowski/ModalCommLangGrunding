package com.pwr.zpi;
import com.pwr.zpi.exceptions.InvalidFormulaException;
import com.pwr.zpi.language.*;

import java.util.Set;

/**
 * Collection stores all of the holons in agents' memory
 */
public class HolonCollection {

    private Set<Holon> holonCollection;

    public HolonCollection()
    {
        //todo
    }

    /**
     * Constructor which sets a set of Holons in Collection
     * @param holonCollection
     */
    public HolonCollection(Set<Holon> holonCollection)
    {
        this.holonCollection = holonCollection;
    }


    public Holon getHolon(Formula f){
        for(Holon h:holonCollection){
            if(h.getFormula().equals(f)){
                return h;
            }
        }
        return null;
    }

   /* *//**
     * Method adds holon based on Formula
     * @param formula
     * @return
     *//*
    public Holon addHolon(Formula formula, Agent agent, int timestamp)
    {
        try {
            Holon holon = new Holon(formula, agent.getKnowledgeBase().getBaseProfiles(timestamp), timestamp, formula.getModel(), new DistributedKnowledge(agent, formula, timestamp));
            holonCollection.add(holon);
            return holon;
        } catch (InvalidFormulaException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Holon findHolon(Formula formula, Agent agent, int timestamp)
    {
        for(Holon holon : holonCollection)
            if(holon.getFormula().equals(formula))
                return holon;
        return addHolon(formula, agent, timestamp);
    }*/







}
