package sg.edu.nus.cs2103t.mina.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.controller.CommandManager;
import sg.edu.nus.cs2103t.mina.model.TaskView;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

/**
 * 
 * A console based implementation of view for MINA
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public class ConsoleUI extends MinaView {

    private static final String CLASS_NAME = ConsoleUI.class.getName();

    private BufferedReader _inputReader;
    private BufferedWriter _outputWriter;
    private InputStream _input;
    private OutputStream _output;

    // TODO: fix console UI
    private TaskView _taskView;

    public ConsoleUI(InputStream input, OutputStream output,
            CommandManager commandController) {
        super(commandController);
        _input = input;
        _output = output;
        _inputReader = new BufferedReader(new InputStreamReader(_input));
        _outputWriter = new BufferedWriter(new OutputStreamWriter(_output));
        _taskView = _commandController.getTaskView();
    }

    @Override
    public String getUserInput() {
        try {
            return _inputReader.readLine();
        } catch (IOException e) {
            LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
        }
        return null;
    }

    @Override
    public void displayOutput() {
        try {
            _outputWriter.write(_taskView.getStatus() + "\n" +
                    _taskView.toString());
            _outputWriter.flush();
        } catch (IOException e) {
            LogHelper.log(CLASS_NAME, Level.ERROR, e.getMessage());
        }
    }

    @Override
    public void updateLists() {
        return;
    }

    public void loop() {
        _taskView = _commandController
                .processUserInput(getUserInput(), 1, 1, 1);
        displayOutput();
    }
}
