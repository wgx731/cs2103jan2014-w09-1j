package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

/**
 * This class is for command keywords. Keywords are special words that signal
 * MINA that it needs special processing
 */

//@author A0099151B
public interface StandardKeyword {
    public static final String DELIMITER = "-";
    public static final String DELIMITER_ESCAPE =  "\u2010";
    public String getFilePrefix();
    public String getFormattedKeyword();
    public String getKeyword();
}
