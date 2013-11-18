package stupaq.cloudatlas.interpreter;

import stupaq.cloudatlas.attribute.types.CAQuery;
import stupaq.cloudatlas.interpreter.context.OutputContext;
import stupaq.cloudatlas.interpreter.context.OutputContext.ZMIUpdaterOutputContext;
import stupaq.cloudatlas.interpreter.data.AttributesTable;
import stupaq.cloudatlas.parser.QueryLanguage.Absyn.XProgram;
import stupaq.cloudatlas.parser.QueryParser;
import stupaq.cloudatlas.zone.ZoneManagementInfo;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy.InPlaceAggregator;

public class QueryZMIUpdater extends InPlaceAggregator<ZoneManagementInfo> {
  private final XProgram program;

  public QueryZMIUpdater(CAQuery query) throws Exception {
    program = new QueryParser(query.getQueryString()).parseProgram();
  }

  @Override
  public void process(Iterable<ZoneManagementInfo> children, final ZoneManagementInfo current) {
    AttributesTable table = new AttributesTable(children);
    // Run query for non-leaf zones
    if (!table.isEmpty()) {
      OutputContext outputContext = new ZMIUpdaterOutputContext(current);
      new EvalVisitor(table).eval(program, outputContext);
    }
  }
}
