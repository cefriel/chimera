package com.complexible.pinto;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;

public class EnumReader <T extends Enum> {
    /**
     * Serialize the given value as RDF.  This should produce a round-trippable serialization, that is, the output of
     * this method should return an object that is {@code .equals} to the result of passing the result to
     * {@link #readValue(Model, Value)}.
     *
     * @param theValue  the value to serialize
     *
     * @return          the value represented as RDF
     */
    public Value writeValue(final T theValue){
        return null;
    }

    /**
     * Deserialize the object denoted by the given resource from the graph into the original Java object
     * @param theGraph  the graph
     * @param theObj    the resource to deserialize
     * @return          the object, or null if the data is incomplete
     *
     * @throws RDFMappingException if there is an error while deserializing
     */
    public static <T> T readValue(final Model theGraph, final Value theObj, Class<T> enum_type) {
        return (T)(Enum.valueOf((Class<? extends Enum>)enum_type, theObj.stringValue()));
    }
}
