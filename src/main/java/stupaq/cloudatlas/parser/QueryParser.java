package stupaq.cloudatlas.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import stupaq.cloudatlas.parser.QueryLanguage.Absyn.XProgram;
import stupaq.cloudatlas.parser.QueryLanguage.Yylex;
import stupaq.cloudatlas.parser.QueryLanguage.parser;

public class QueryParser implements AutoCloseable {
  private parser parser;

  public QueryParser(String str) {
    this(new StringReader(str));
  }

  public QueryParser(InputStream inputStream) {
    this(new InputStreamReader(inputStream));
  }

  public QueryParser(Reader reader) {
    Yylex lexer = new Yylex(reader);
    parser = new parser(lexer);
  }

  public XProgram parseProgram() throws Exception {
    return parser.pXProgram();
  }

  @Override
  public void close() throws Exception {
  }
}
