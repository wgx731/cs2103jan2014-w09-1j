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

    private List<String> _eventList;
    private List<String> _todoList;
    private List<String> _deadlineList;

    public ConsoleUI(InputStream input, OutputStream output,
            CommandController commandController) {
        super(commandController);
        _input = input;
        _output = output;
        _inputReader = new BufferedReader(new InputStreamReader(_input));
        _outputWriter = new BufferedWriter(new OutputStreamWriter(_output));
        _eventList = new ArrayList<String>();
        _todoList = new ArrayList<String>();
        _deadlineList = new ArrayList<String>();
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

    @Override
    public void updateLists(ArrayList<String> allEventTasks,
            ArrayList<String> allDeadlineTasks, ArrayList<String> allTodoTasks) {
        _eventList.clear();
        _eventList.addAll(allEventTasks);
        _deadlineList.clear();
        _deadlineList.addAll(allDeadlineTasks);
        _todoList.clear();
        _todoList.addAll(allTodoTasks);
    }

    public void loop() {
        String feedback = _commandController.processUserInput(getUserInput());
        displayOutput(feedback);
    }
}
