// This class is not auto-generated.
package st4rt.convertor.eu.start.standards.FSM;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import st4rt.convertor.empire.annotation.NamedGraph;
import st4rt.convertor.empire.annotation.Namespaces;
import st4rt.convertor.empire.annotation.RdfProperty;
import st4rt.convertor.empire.annotation.RdfsClass;
import st4rt.convertor.empire.annotation.SupportsRdfId;
import st4rt.convertor.empire.annotation.SupportsRdfIdImpl;

/**
 * @author Mohammad Mehdi Pourhashem Kallehbasti
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@NamedGraph(type = NamedGraph.NamedGraphType.Static)
@Namespaces({"st4rt","http://st4rt.eu/ontologies/st4rt#",
	"it2r","http://www.it2rail.eu/ontology#",
	"foaf","http://xmlns.com/foaf/0.1/#",
	"travel", "http://www.it2rail.eu/ontology/travel#",
	"infrastructure","http://www.it2rail.eu/ontology/infrastructure#",
	"customer", "http://www.it2rail.eu/ontology/customer#"})
@RdfsClass("infrastructure:RouteLink")
public class RouteLink implements SupportsRdfId {

	@XmlTransient
	private SupportsRdfId mIdSupport = new SupportsRdfIdImpl();

	public RdfKey getRdfId() {
		return mIdSupport.getRdfId();
	}

	public void setRdfId(RdfKey theId) {
		mIdSupport.setRdfId(theId);
	}

	@RdfProperty(propertyName = "it2r:isStartingAt")
	protected StopPlace isStartingAt;

	@RdfProperty(propertyName = "it2r:isEndingAt")
	protected StopPlace isEndingAt;

	public StopPlace getIsStartingAt() {
		return isStartingAt;
	}

	public StopPlace getIsEndingAt() {
		return isEndingAt;
	}

	public void setIsStartingAt(StopPlace value) {
		this.isStartingAt = value;
	}

	public void setIsEndingAt(StopPlace value) {
		this.isEndingAt = value;
	}

}
