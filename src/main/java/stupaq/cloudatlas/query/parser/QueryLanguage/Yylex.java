// This JLex file was machine-generated by the BNF converter
package stupaq.cloudatlas.query.parser.QueryLanguage;

import java_cup.runtime.Symbol;


public class Yylex implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 65536;
	private final int YY_EOF = 65537;

  String pstring = new String();
  public int line_num() { return (yyline+1); }
  public String buff() { return new String(yy_buffer,yy_buffer_index,10).trim(); }
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	public Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	public Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int STRING = 5;
	private final int ESCAPED = 6;
	private final int YYINITIAL = 0;
	private final int COMMENT = 1;
	private final int CHAREND = 4;
	private final int CHARESC = 3;
	private final int CHAR = 2;
	private final int yy_state_dtrans[] = {
		0,
		53,
		53,
		53,
		53,
		55,
		57
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NOT_ACCEPT,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NOT_ACCEPT,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NOT_ACCEPT,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NOT_ACCEPT,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NOT_ACCEPT,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NOT_ACCEPT,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,65538,
"47:9,50,51,47,50,51,47:18,50,47,45,47:2,9,42,47,4,5,7,6,2,3,49,8,43:10,47,1" +
",11,10,12,47:2,13,18,17,15,20,21,28,32,22,41:2,25,41,14,26,30,41,23,16,24,2" +
"7,41,31,29,19,41,47,46,47:2,44,47,34,41:3,37,33,41:5,35,41,48,41:3,39,36,38" +
",40,41:5,47:69,41:23,47,41:31,47,41:8,47:65280,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,95,
"0,1:11,2,3,4,5,1:5,6,7,8,7:3,9,7:10,1:8,10,11,12,13,14,12,15,16,17,18,19,20" +
",21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45" +
",46,47,48,49,7,50,51,52,53,54,55,56")[0];

	private int yy_nxt[][] = unpackFromString(57,52,
"1,2,3,4,5,6,7,8,9,10,11,12,13,14,47,80,84,87,50,87:2,89,87,90,87,91,52,87:4" +
",92,87,93,87:4,94,87:4,15,-1,16,-1:2,87,-1,17:2,-1:62,18,-1,19,-1:49,20,-1:" +
"54,87,54,87,21,87:25,-1,87:2,-1:3,87,-1:46,15,-1:5,46,-1:15,87:4,25,87:24,-" +
"1,87:2,-1:3,87,-1:16,87:29,-1,87:2,-1:3,87,-1:16,87:2,88,87:26,-1,87:2,-1:3" +
",87,-1:40,49,-1:5,27,-1:51,27,-1:21,87:13,56,58,87:14,-1,87:2,-1:3,87,-1:46" +
",48,-1:11,51,-1:39,48,-1:21,87:6,22,87:22,-1,87:2,-1:3,87,-1:16,87:10,23,87" +
":18,-1,87:2,-1:3,87,-1:3,1,-1:64,87:2,24,87:26,-1,87:2,-1:3,87,-1:3,1,38:44" +
",39,40,38:4,-1:14,87:11,26,87:17,-1,87:2,-1:3,87,-1:3,1,41:37,42,41:6,43,44" +
",41,45,41:2,-1:14,87:12,85,87:16,-1,87:2,-1:3,87,-1:16,87:3,65,87:25,-1,87:" +
"2,-1:3,87,-1:16,87:10,66,87:18,-1,87:2,-1:3,87,-1:16,87:15,86,87:13,-1,87:2" +
",-1:3,87,-1:16,87:7,68,87:21,-1,87:2,-1:3,87,-1:16,87:22,69,87:6,-1,87:2,-1" +
":3,87,-1:16,87:27,70,87,-1,87:2,-1:3,87,-1:16,87:4,28,87:24,-1,87:2,-1:3,87" +
",-1:16,87:3,73,87:25,-1,87:2,-1:3,87,-1:16,87:11,29,87:17,-1,87:2,-1:3,87,-" +
"1:16,87:10,76,87:18,-1,87:2,-1:3,87,-1:16,87:23,77,87:5,-1,87:2,-1:3,87,-1:" +
"16,87:24,30,87:4,-1,87:2,-1:3,87,-1:16,87:3,31,87:25,-1,87:2,-1:3,87,-1:16," +
"87:4,78,87:24,-1,87:2,-1:3,87,-1:16,87:11,32,87:17,-1,87:2,-1:3,87,-1:16,87" +
":16,79,87:12,-1,87:2,-1:3,87,-1:16,87:10,33,87:18,-1,87:2,-1:3,87,-1:16,87:" +
"7,34,87:21,-1,87:2,-1:3,87,-1:16,87:24,35,87:4,-1,87:2,-1:3,87,-1:16,87:11," +
"36,87:17,-1,87:2,-1:3,87,-1:16,87:17,37,87:11,-1,87:2,-1:3,87,-1:16,87:7,59" +
",87:21,-1,87:2,-1:3,87,-1:16,87:12,83,87:16,-1,87:2,-1:3,87,-1:16,87:3,67,8" +
"7:25,-1,87:2,-1:3,87,-1:16,87:7,72,87:21,-1,87:2,-1:3,87,-1:16,87:7,81,87:2" +
"1,-1,87:2,-1:3,87,-1:16,87:12,71,87:16,-1,87:2,-1:3,87,-1:16,87:7,74,87:21," +
"-1,87:2,-1:3,87,-1:16,87:7,75,87:21,-1,87:2,-1:3,87,-1:16,87:9,60,87:19,-1," +
"87:2,-1:3,87,-1:16,87:7,61,87:21,-1,87:2,-1:3,87,-1:16,82,87:28,-1,87:2,-1:" +
"3,87,-1:16,87:19,62,87:9,-1,87:2,-1:3,87,-1:16,87:21,63,87:7,-1,87:2,-1:3,8" +
"7,-1:16,87:26,64,87:2,-1,87:2,-1:3,87,-1:3");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {
				return null;
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ return new Symbol(sym._SYMB_0); }
					case -3:
						break;
					case 3:
						{ return new Symbol(sym._SYMB_1); }
					case -4:
						break;
					case 4:
						{ return new Symbol(sym._SYMB_2); }
					case -5:
						break;
					case 5:
						{ return new Symbol(sym._SYMB_3); }
					case -6:
						break;
					case 6:
						{ return new Symbol(sym._SYMB_4); }
					case -7:
						break;
					case 7:
						{ return new Symbol(sym._SYMB_5); }
					case -8:
						break;
					case 8:
						{ return new Symbol(sym._SYMB_6); }
					case -9:
						break;
					case 9:
						{ return new Symbol(sym._SYMB_7); }
					case -10:
						break;
					case 10:
						{ return new Symbol(sym._SYMB_8); }
					case -11:
						break;
					case 11:
						{ return new Symbol(sym._SYMB_9); }
					case -12:
						break;
					case 12:
						{ return new Symbol(sym._SYMB_13); }
					case -13:
						break;
					case 13:
						{ return new Symbol(sym._SYMB_11); }
					case -14:
						break;
					case 14:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -15:
						break;
					case 15:
						{ return new Symbol(sym._INTEGER_, new Integer(yytext())); }
					case -16:
						break;
					case 16:
						{ yybegin(STRING); }
					case -17:
						break;
					case 17:
						{ /* ignore white space. */ }
					case -18:
						break;
					case 18:
						{ return new Symbol(sym._SYMB_14); }
					case -19:
						break;
					case 19:
						{ return new Symbol(sym._SYMB_10); }
					case -20:
						break;
					case 20:
						{ return new Symbol(sym._SYMB_12); }
					case -21:
						break;
					case 21:
						{ return new Symbol(sym._SYMB_16); }
					case -22:
						break;
					case 22:
						{ return new Symbol(sym._SYMB_18); }
					case -23:
						break;
					case 23:
						{ return new Symbol(sym._SYMB_24); }
					case -24:
						break;
					case 24:
						{ return new Symbol(sym._SYMB_15); }
					case -25:
						break;
					case 25:
						{ return new Symbol(sym._SYMB_17); }
					case -26:
						break;
					case 26:
						{ return new Symbol(sym._SYMB_22); }
					case -27:
						break;
					case 27:
						{ return new Symbol(sym._DOUBLE_, new Double(yytext())); }
					case -28:
						break;
					case 28:
						{ return new Symbol(sym._SYMB_19); }
					case -29:
						break;
					case 29:
						{ return new Symbol(sym._SYMB_21); }
					case -30:
						break;
					case 30:
						{ return new Symbol(sym._SYMB_30); }
					case -31:
						break;
					case 31:
						{ return new Symbol(sym._SYMB_23); }
					case -32:
						break;
					case 32:
						{ return new Symbol(sym._SYMB_20); }
					case -33:
						break;
					case 33:
						{ return new Symbol(sym._SYMB_25); }
					case -34:
						break;
					case 34:
						{ return new Symbol(sym._SYMB_28); }
					case -35:
						break;
					case 35:
						{ return new Symbol(sym._SYMB_29); }
					case -36:
						break;
					case 36:
						{ return new Symbol(sym._SYMB_27); }
					case -37:
						break;
					case 37:
						{ return new Symbol(sym._SYMB_26); }
					case -38:
						break;
					case 38:
						{ pstring += yytext(); }
					case -39:
						break;
					case 39:
						{ String foo = pstring; pstring = new String(); yybegin(YYINITIAL); return new Symbol(sym._STRING_, foo.intern()); }
					case -40:
						break;
					case 40:
						{ yybegin(ESCAPED); }
					case -41:
						break;
					case 41:
						{ pstring += yytext(); yybegin(STRING); }
					case -42:
						break;
					case 42:
						{ pstring += "\t"; yybegin(STRING); }
					case -43:
						break;
					case 43:
						{ pstring += "\""; yybegin(STRING); }
					case -44:
						break;
					case 44:
						{ pstring += "\\"; yybegin(STRING); }
					case -45:
						break;
					case 45:
						{ pstring +=  "\n"; yybegin(STRING); }
					case -46:
						break;
					case 47:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -47:
						break;
					case 48:
						{ return new Symbol(sym._DOUBLE_, new Double(yytext())); }
					case -48:
						break;
					case 50:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -49:
						break;
					case 52:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -50:
						break;
					case 54:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -51:
						break;
					case 56:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -52:
						break;
					case 58:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -53:
						break;
					case 59:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -54:
						break;
					case 60:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -55:
						break;
					case 61:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -56:
						break;
					case 62:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -57:
						break;
					case 63:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -58:
						break;
					case 64:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -59:
						break;
					case 65:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -60:
						break;
					case 66:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -61:
						break;
					case 67:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -62:
						break;
					case 68:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -63:
						break;
					case 69:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -64:
						break;
					case 70:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -65:
						break;
					case 71:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -66:
						break;
					case 72:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -67:
						break;
					case 73:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -68:
						break;
					case 74:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -69:
						break;
					case 75:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -70:
						break;
					case 76:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -71:
						break;
					case 77:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -72:
						break;
					case 78:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -73:
						break;
					case 79:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -74:
						break;
					case 80:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -75:
						break;
					case 81:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -76:
						break;
					case 82:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -77:
						break;
					case 83:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -78:
						break;
					case 84:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -79:
						break;
					case 85:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -80:
						break;
					case 86:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -81:
						break;
					case 87:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -82:
						break;
					case 88:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -83:
						break;
					case 89:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -84:
						break;
					case 90:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -85:
						break;
					case 91:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -86:
						break;
					case 92:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -87:
						break;
					case 93:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -88:
						break;
					case 94:
						{ return new Symbol(sym.XIdent, yytext().intern()); }
					case -89:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
