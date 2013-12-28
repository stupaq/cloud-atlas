package stupaq.cloudatlas.services.zonemanager.query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.values.CAQuery;
import stupaq.cloudatlas.query.errors.InterpreterException;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.InPlaceSynthesizer;

import static stupaq.cloudatlas.query.typecheck.TypeInfo.of;

public class InstalledQueriesUpdater extends InPlaceSynthesizer<ZoneManagementInfo> {
  private static final Log LOG = LogFactory.getLog(InstalledQueriesUpdater.class);

  @Override
  public void process(Iterable<ZoneManagementInfo> children, ZoneManagementInfo current) {
    current.removeComputed();
    // We have to materialize iterable here for future modifications
    for (Attribute attribute : current.specialAttributes().toList()) {
      AttributeValue value = attribute.value();
      if (!value.isNull() && of(CAQuery.class).matches(value)) {
        CAQuery query = (CAQuery) value;
        try {
          new SingleQueryUpdater(query).process(children, current);
        } catch (InterpreterException e) {
          LOG.error(
              "In local context: " + current.localName() + ", while processing query: " + query +
                  ", encountered exception: " + e.getMessage() + ", exception type: " +
                  e.getClass().getSimpleName());
        } catch (Exception e) {
          LOG.fatal(
              "In local context: " + current.localName() + ", while processing query: " + query, e);
        }
      }
    }
  }
}
