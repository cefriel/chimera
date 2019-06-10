package be.ugent.rml.functions;

import java.io.IOException;

import be.ugent.rml.records.Record;

public interface SingleRecordFunctionExecutor {

    Object execute(Record record) throws IOException;
}
