package it.cefriel.chimera.processor;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StopProcessor implements Processor {
    private Logger log = LoggerFactory.getLogger(StopProcessor.class); 


    /**
     * A Camel processor which stop routes.
     */

    private String routeId;

    public String getRouteId() {
        return this.routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public void process(Exchange exchange) throws Exception {
        CamelContext camelContext = exchange.getContext();
        // remove myself from the in flight registry so we can stop this route without trouble
        Throwable caused = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
        if (caused !=null )
        	log.error(stack_to_string(caused));
        camelContext.getInflightRepository().remove(exchange);
        // stop the route
        camelContext.stop();
    }
    
    
    private String stack_to_string(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String sStackTrace = sw.toString(); // stack trace as a string
        return sStackTrace;
    }       
}