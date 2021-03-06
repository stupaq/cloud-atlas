package stupaq.cloudatlas.query.parser;

import java.io.Reader;
import java.io.StringReader;

import stupaq.cloudatlas.query.errors.ParsingException;
import stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XProgram;
import stupaq.cloudatlas.query.parser.QueryLanguage.Yylex;
import stupaq.cloudatlas.query.parser.QueryLanguage.parser;

public class QueryParser implements AutoCloseable {
  private final parser parser;

  public QueryParser(String str) {
    this(new StringReader(str));
  }

  public QueryParser(Reader reader) {
    Yylex lexer = new Yylex(reader);
    parser = new parser(lexer);
  }

  public XProgram parseProgram() throws ParsingException {
    try {
      // Apparently authors of Yylex think that it's perfectly fine to throw
      // Errors in case parsing fails... I don't.
      return parser.pXProgram();
    } catch (Throwable t) {
      throw new ParsingException(t);
    }
  }

  @Override
  public void close() {
  }
}
