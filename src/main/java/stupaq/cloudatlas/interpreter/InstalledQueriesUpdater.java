package stupaq.cloudatlas.interpreter;

import com.google.common.base.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CAQuery;
import stupaq.cloudatlas.zone.ZoneManagementInfo;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy.InPlaceAggregator;

public class InstalledQueriesUpdater extends InPlaceAggregator<ZoneManagementInfo> {
  private static final Log LOG = LogFactory.getLog(InstalledQueriesUpdater.class);

  @SuppressWarnings("unchecked")
  @Override
  public void process(Iterable<ZoneManagementInfo> children, final ZoneManagementInfo current) {
    for (Attribute attribute : current.getPrivateAttributes()) {
      Optional<AttributeValue> value = attribute.getValue();
      if (value.isPresent() || value.get() instanceof CAQuery) {
        CAQuery query = (CAQuery) value.get();
        try {
          new SingleQueryUpdater(query).process(children, current);
        } catch (Exception e) {
          LOG.error("Exception while processing query: " + query, e);
        }
      }
    }
  }
}
