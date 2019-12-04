package be.ugent.rml.termgenerator;

import be.ugent.rml.Executor;
import be.ugent.rml.functions.SingleRecordFunctionExecutor;
import be.ugent.rml.records.Record;
import be.ugent.rml.term.BlankNode;
import be.ugent.rml.term.Term;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlankNodeGenerator extends TermGenerator {

    public BlankNodeGenerator() {
        this(null);
    }

    public BlankNodeGenerator(SingleRecordFunctionExecutor functionExecutor) {
        super(functionExecutor);
    }

    @Override
    public List<Term> generate(Record record) throws IOException {
        ArrayList<Term> nodes = new ArrayList<>();

        if (this.functionExecutor != null) {
            List<String> objectStrings = (List<String>) this.functionExecutor.execute(record);

            objectStrings.forEach(object -> {
                nodes.add(new BlankNode(object));
            });
        } else {
            nodes.add(new BlankNode("" + Executor.getNewBlankNodeID()));
        }

        return nodes;
    }
}
