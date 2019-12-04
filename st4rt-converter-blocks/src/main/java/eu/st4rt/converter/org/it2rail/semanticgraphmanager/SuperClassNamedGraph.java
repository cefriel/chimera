package eu.st4rt.converter.org.it2rail.semanticgraphmanager;

import st4rt.convertor.empire.annotation.NamedGraph;
import st4rt.convertor.empire.annotation.SuperIt2RailConcept;

public final class SuperClassNamedGraph {
	
	
	
	private static SuperClassNamedGraph instance = null;
	
	private SuperClassNamedGraph()  {
		
	}
	
	public static boolean hasMappedSuperClassSpecified(Class<?> theObj) {
		if(theObj==null) return false;
		SuperIt2RailConcept aAnnotation = theObj.getAnnotation(SuperIt2RailConcept.class);
		return aAnnotation != null && (aAnnotation!=null ? aAnnotation.enabled() : false);
	}
	
	public static boolean hasNamedGraphSpecified(Class<?> theObj) {
		NamedGraph aAnnotation = theObj.getAnnotation(NamedGraph.class);

		return aAnnotation != null &&
			   (aAnnotation.type() == NamedGraph.NamedGraphType.Instance || (aAnnotation.type() == NamedGraph.NamedGraphType.Static
																			 && !aAnnotation.value().equals("")));
	}

}
