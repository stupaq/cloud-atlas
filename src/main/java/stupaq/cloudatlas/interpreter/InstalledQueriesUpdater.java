package stupaq.cloudatlas.interpreter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CAQuery;
import stupaq.cloudatlas.interpreter.typecheck.TypeInfo;
import stupaq.cloudatlas.zone.ZoneManagementInfo;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy.InPlaceAggregator;

public class InstalledQueriesUpdater extends InPlaceAggregator<ZoneManagementInfo> {
  private static final Log LOG = LogFactory.getLog(InstalledQueriesUpdater.class);

  @SuppressWarnings("unchecked")
  @Override
  public void process(Iterable<ZoneManagementInfo> children, final ZoneManagementInfo current) {
    for (Attribute attribute : current.getPrivateAttributes()) {
      AttributeValue value = attribute.value();
      if (!value.isNull() && value.getType().equals(new TypeInfo<>(CAQuery.class))) {
        CAQuery query = (CAQuery) value;
        try {
          new SingleQueryUpdater(query).process(children, current);
        } catch (Exception e) {
          LOG.error(
              "In local context: " + current.localName() + ", while processing query: " + query
              + ", encountered exception: " + e.getMessage() + ", exception type: " + e.getClass()
                  .getSimpleName());
        }
      }
    }
  }
}
