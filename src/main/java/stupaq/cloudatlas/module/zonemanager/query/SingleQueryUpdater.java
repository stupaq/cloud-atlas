package stupaq.cloudatlas.module.zonemanager.query;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import java.util.Collection;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.types.CAQuery;
import stupaq.cloudatlas.query.errors.ParsingException;
import stupaq.cloudatlas.query.interpreter.EvalVisitor;
import stupaq.cloudatlas.query.evaluation.context.OutputContext;
import stupaq.cloudatlas.query.evaluation.data.AttributesTable;
import stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XProgram;
import stupaq.cloudatlas.query.parser.QueryParser;
import stupaq.cloudatlas.module.zonemanager.hierarchy.ZoneHierarchy.InPlaceAggregator;
import stupaq.cloudatlas.module.zonemanager.ZoneManagementInfo;

public class SingleQueryUpdater extends InPlaceAggregator<ZoneManagementInfo> {
  private final XProgram program;

  public SingleQueryUpdater(CAQuery query) throws ParsingException {
    program = new QueryParser(query.getQueryString()).parseProgram();
  }

  @Override
  public void process(Iterable<ZoneManagementInfo> children, final ZoneManagementInfo current) {
    if (!Iterables.isEmpty(children)) {
      AttributesTable table = new AttributesTable(FluentIterable.from(children)
          .transform(new Function<ZoneManagementInfo, Iterable<Attribute>>() {
            @Override
            public Collection<Attribute> apply(ZoneManagementInfo managementInfo) {
              return managementInfo.getPublicAttributes();
            }
          }));
      // Run query for non-leaf zones
      OutputContext outputContext = new ZMIUpdaterOutputContext(current);
      new EvalVisitor(table).eval(program, outputContext);
      // Commit after successful execution
      outputContext.commit();
    }
  }
}