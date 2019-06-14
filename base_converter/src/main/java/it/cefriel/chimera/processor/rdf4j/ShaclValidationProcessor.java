package it.cefriel.chimera.processor.rdf4j;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.NotifyingSail;
import org.eclipse.rdf4j.sail.shacl.ShaclSail;
import org.eclipse.rdf4j.sail.shacl.ShaclSailValidationException;
import org.eclipse.rdf4j.sail.shacl.results.ValidationReport;

import it.cefriel.chimera.context.RDFGraph;
import it.cefriel.chimera.util.ProcessorConstants;
import it.cefriel.chimera.util.SemanticLoader;

public class ShaclValidationProcessor implements Processor {
	private List<String> shaclRulesUrls=null;

	public void process(Exchange exchange) throws Exception {
		Model current_ruleset=null;
		ValueFactory vf = SimpleValueFactory.getInstance();


		//Logger root = (Logger) LoggerFactory.getLogger(ShaclSail.class.getName());
		//root.setLevel(Level.INFO);

		//shaclSail.setLogValidationPlans(true);
		//shaclSail.setGlobalLogValidationExecution(true);
		//shaclSail.setLogValidationViolations(true);
		Message in = exchange.getIn();

		RDFGraph graph=in.getHeader(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class);
		NotifyingSail data=(NotifyingSail)graph.getData();
		ShaclSail shaclSail = new ShaclSail(data);
		shaclSail.setIgnoreNoShapesLoadedException(true);
		SailRepository sailRepository = new SailRepository(shaclSail);
		sailRepository.init();


		if (shaclRulesUrls==null)
			shaclRulesUrls=in.getHeader(ProcessorConstants.SHACL_RULES, List.class);

		try (SailRepositoryConnection connection = sailRepository.getConnection()) {

			try {

				connection.begin();
				for (String url: shaclRulesUrls) {
					current_ruleset=SemanticLoader.load_data(url);    
					connection.add(current_ruleset, vf.createIRI(url));
				}
				connection.commit();

			} catch (RepositoryException exception) {
				Throwable cause = exception.getCause();
				if (cause instanceof ShaclSailValidationException) {
					ValidationReport validationReport = ((ShaclSailValidationException) cause).getValidationReport();
					Model validationReportModel = ((ShaclSailValidationException) cause).validationReportAsModel();
					// use validationReport or validationReportModel to understand validation violations

					Rio.write(validationReportModel, System.out, RDFFormat.TURTLE);
				}
				throw exception;
			}
		}
	}

	public List<String> getShaclRulesUrls() {
		return shaclRulesUrls;
	}

	public void setShaclRulesUrls(List<String> shaclRulesUrls) {
		this.shaclRulesUrls = shaclRulesUrls;
	}
}