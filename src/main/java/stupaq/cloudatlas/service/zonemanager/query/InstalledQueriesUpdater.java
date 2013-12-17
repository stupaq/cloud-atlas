package stupaq.cloudatlas.service.zonemanager.query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.values.CAQuery;
import stupaq.cloudatlas.query.errors.InterpreterException;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.cloudatlas.service.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.service.zonemanager.hierarchy.ZoneHierarchy.InPlaceAggregator;

public class InstalledQueriesUpdater extends InPlaceAggregator<ZoneManagementInfo> {
  private static final Log LOG = LogFactory.getLog(InstalledQueriesUpdater.class);

  @SuppressWarnings("unchecked")
  @Override
  public void process(Iterable<ZoneManagementInfo> children, final ZoneManagementInfo current) {
    for (Attribute attribute : current.getPrivateAttributes()) {
      AttributeValue value = attribute.getValue();
      if (!value.isNull() && value.type().equals(TypeInfo.of(CAQuery.class))) {
        CAQuery query = (CAQuery) value;
        try {
          new SingleQueryUpdater(query).process(children, current);
        } catch (InterpreterException e) {
          LOG.error(
              "In local context: " + current.localName() + ", while processing query: " + query
              + ", encountered exception: " + e.getMessage() + ", exception type: " + e.getClass()
                  .getSimpleName());
        } catch (Exception e) {
          LOG.fatal(
              "In local context: " + current.localName() + ", while processing query: " + query, e);
        }
      }
    }
  }
}
