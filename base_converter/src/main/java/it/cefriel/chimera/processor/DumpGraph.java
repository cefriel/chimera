package it.cefriel.chimera.processor;
import java.nio.charset.StandardCharsets;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cefriel.chimera.context.RDFGraph;
import it.cefriel.chimera.util.ProcessorConstants;


public class DumpGraph implements Processor {
    private Logger log = LoggerFactory.getLogger(DumpGraph.class); 

    public void process(Exchange exchange) throws Exception {
		Repository repo=null;
		String  output=null;
		ByteArrayOutputStream outstream=new ByteArrayOutputStream();
		repo=exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();
		try (RepositoryConnection con = repo.getConnection()) {
			RepositoryResult<Statement> dump = con.getStatements(null, null, null);
			Model dump_model = QueryResults.asModel(dump);

			Rio.write(dump_model, outstream, RDFFormat.TURTLE);
			output=new String(outstream.toByteArray(), StandardCharsets.UTF_8);
			log.info("RDF OUTPUT\r\r"+output);
			exchange.getOut().setBody(output);
			exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "text/turtle");
		}
    }

}