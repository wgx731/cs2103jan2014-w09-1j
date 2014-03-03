package sg.edu.nus.cs2103t.mina.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * A console based implementation of view for MINA
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public class ConsoleUI implements MinaView {

    private static Logger logger = LogManager.getLogger(ConsoleUI.class
            .getName());

    private BufferedReader _inputReader;
    private BufferedWriter _outputWriter;

    public ConsoleUI(InputStream input, OutputStream output) {
        super();
        _inputReader = new BufferedReader(new InputStreamReader(input));
        _outputWriter = new BufferedWriter(new OutputStreamWriter(output));
    }

    @Override
    public String getUserInput() {
        try {
            return _inputReader.readLine();
        } catch (IOException e) {
            logger.error(e, e);
        }
        return null;
    }

    @Override
    public void displayOutput(String message) {
        try {
            _outputWriter.write(message);
            _outputWriter.flush();
        } catch (IOException e) {
            logger.error(e, e);
        }
    }

}
