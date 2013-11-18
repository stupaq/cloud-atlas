package stupaq.cloudatlas.interpreter;

import stupaq.cloudatlas.attribute.types.CAQuery;
import stupaq.cloudatlas.interpreter.context.OutputContext;
import stupaq.cloudatlas.interpreter.context.OutputContext.ZMIUpdaterOutputContext;
import stupaq.cloudatlas.interpreter.data.AttributesTable;
import stupaq.cloudatlas.parser.QueryLanguage.Absyn.XProgram;
import stupaq.cloudatlas.parser.QueryParser;
import stupaq.cloudatlas.zone.ZoneManagementInfo;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy.Aggregator;

public class QueryZMIUpdater extends Aggregator<ZoneManagementInfo> {
  private final XProgram program;

  public QueryZMIUpdater(CAQuery query) throws Exception {
    program = new QueryParser(query.getQueryString()).parseProgram();
  }

  @Override
  public ZoneManagementInfo apply(Iterable<ZoneManagementInfo> children,
      final ZoneManagementInfo current) {
    AttributesTable table = new AttributesTable(children);
    OutputContext outputContext = new ZMIUpdaterOutputContext(current);
    new EvalVisitor(table).eval(program, outputContext);
    return current;
  }
}
