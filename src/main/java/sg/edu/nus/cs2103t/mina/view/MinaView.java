package sg.edu.nus.cs2103t.mina.view;


/**
 * 
 * Method of possible view components for MINA
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public interface MinaView {

    /**
     * Get user input
     * 
     * @return user input string
     */
    public String getUserInput();

    /**
     * Display output to user
     * 
     * @param message output message to be displayed
     */
    public void displayOutput(String message);

}
