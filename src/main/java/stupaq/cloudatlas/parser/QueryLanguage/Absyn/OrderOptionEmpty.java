package stupaq.cloudatlas.parser.QueryLanguage.Absyn; // Java Package generated by the BNF Converter.

public class OrderOptionEmpty extends XOrderOption {

  public OrderOptionEmpty() { }

  public <R,A> R accept(stupaq.cloudatlas.parser.QueryLanguage.Absyn.XOrderOption.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderOptionEmpty) {
      return true;
    }
    return false;
  }

  public int hashCode() {
    return 37;
  }


}
