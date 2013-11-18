package stupaq.cloudatlas.interpreter;

import com.google.common.collect.Iterables;

import stupaq.cloudatlas.attribute.types.CAQuery;
import stupaq.cloudatlas.interpreter.context.OutputContext;
import stupaq.cloudatlas.interpreter.context.OutputContext.ZMIUpdaterOutputContext;
import stupaq.cloudatlas.interpreter.data.AttributesTable;
import stupaq.cloudatlas.parser.QueryLanguage.Absyn.XProgram;
import stupaq.cloudatlas.parser.QueryParser;
import stupaq.cloudatlas.zone.ZoneManagementInfo;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy.InPlaceAggregator;

public class SingleQueryUpdater extends InPlaceAggregator<ZoneManagementInfo> {
  private final XProgram program;

  public SingleQueryUpdater(CAQuery query) throws Exception {
    program = new QueryParser(query.getQueryString()).parseProgram();
  }

  @Override
  public void process(Iterable<ZoneManagementInfo> children, final ZoneManagementInfo current) {
    if (!Iterables.isEmpty(children)) {
      AttributesTable table = new AttributesTable(children);
      // Run query for non-leaf zones
      OutputContext outputContext = new ZMIUpdaterOutputContext(current);
      new EvalVisitor(table).eval(program, outputContext);
    }
  }
}
