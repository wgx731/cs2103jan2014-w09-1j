package sg.edu.nus.cs2103t.mina.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.controller.CommandController;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskView;

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

    private static Logger logger = LogManager.getLogger(ConsoleUI.class
            .getName());

    private BufferedReader _inputReader;
    private BufferedWriter _outputWriter;
    private InputStream _input;
    private OutputStream _output;

    private List<Task<?>> _eventList;
    private List<Task<?>> _todoList;
    private List<Task<?>> _deadlineList;

    private TaskView _taskView;

    public ConsoleUI(InputStream input, OutputStream output,
            CommandController commandController) {
        super(commandController);
        _input = input;
        _output = output;
        _inputReader = new BufferedReader(new InputStreamReader(_input));
        _outputWriter = new BufferedWriter(new OutputStreamWriter(_output));
        _eventList = new ArrayList<Task<?>>();
        _todoList = new ArrayList<Task<?>>();
        _deadlineList = new ArrayList<Task<?>>();
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
    public void displayOutput() {
        try {
            _outputWriter.write(_taskView.getStatus());
            _outputWriter.flush();
        } catch (IOException e) {
            logger.error(e, e);
        }
    }

    @Override
    public void updateLists() {
        _eventList = _taskView.getEvents();
        _deadlineList = _taskView.getDeadlines();
        _todoList = _taskView.getTodos();
    }

    public void loop() {
        _taskView = _commandController
                .processUserInput(getUserInput(), 1, 1, 1);
        displayOutput();
    }
}
