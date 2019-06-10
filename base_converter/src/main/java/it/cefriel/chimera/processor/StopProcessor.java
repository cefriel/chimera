package it.cefriel.chimera.processor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;


public class StopProcessor implements Processor {


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
        camelContext.getInflightRepository().remove(exchange);
        // stop the route
        camelContext.stopRoute(routeId);
    }

}