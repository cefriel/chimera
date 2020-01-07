package it.cefriel.chimera.assetmanager;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import it.cefriel.chimera.util.ProcessorConstants;

public class JWTHeaderAggregationStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange original, Exchange resource) {
        Object originalBody = original.getIn().getBody();
        AccessResponseToken resourceResponse = resource.getIn().getBody(AccessResponseToken.class);
        
        if (original.getPattern().isOutCapable()) {
        	System.out.println("original out: "+original.getOut()+" - "+resource.getIn().getBody());
        	if (originalBody!=null)
        		original.getOut().setBody(originalBody);
        	original.setProperty(ProcessorConstants.JWT_TOKEN, resourceResponse.getAccess());
        } else {
        	original.setProperty(ProcessorConstants.JWT_TOKEN, resourceResponse.getAccess());
        	if (originalBody!=null)
        		original.getIn().setBody(originalBody);
        }
        return original;
    }
    
}