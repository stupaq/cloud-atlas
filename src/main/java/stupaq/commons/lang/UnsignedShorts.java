package stupaq.commons.lang;

public final class UnsignedShorts {
  private UnsignedShorts() {
  }

  public static int toInt(short x) {
    return x & 0xffff;
  }
}
