package com.pwr.zpi;

import com.pwr.zpi.io.XMLDAO;

import java.util.Collection;
import java.util.List;

/**
 * Represents types of observations existed in world, which observations are processed in system.
 * It determines allowed set of traits for certain object (and its observation).
 */
public class ObjectType {
    private String typeId;
    private List<Trait> traits;

    public ObjectType(String id, List<Trait> traits) {
        this.typeId = id;
        this.traits = traits;
    }

    public String getTypeId() {
        return typeId;
    }

    public List<Trait> getTraits() {
        return traits;
    }


    /**
     * Method that return a trait based on a given name
     * @param name name of Trait
     * @return Trait with a given name or null if object doesn't contain such trait
     */
    public Trait findTraitByName(String name)
    {
        for(Trait trait: traits)
            if(trait.equals(name))
                return trait;
        return null;
    }

    public static Collection<ObjectType> getObjectTypes() {
        return new XMLDAO<>().loadTypesDefinitions();
    }

}
