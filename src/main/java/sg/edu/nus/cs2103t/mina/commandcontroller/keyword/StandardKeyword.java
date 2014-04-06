package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

/**
 * This class is for command keywords. Keywords are special words that signal
 * MINA that it needs special processing
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */

public interface StandardKeyword {
    public static final String DELIMITER = "-";
    public String getFilePrefix();
    public String getFormattedKeyword();
    public String getKeyword();
}
