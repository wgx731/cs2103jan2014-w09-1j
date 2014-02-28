package sg.edu.nus.cs2103t.mina.controller;

public class CommandController {

    private static String _inputString;

    enum CommandType {
        ADD, DELETE, EDIT, FILTER, SEARCH, EXIT, INVALID
    };

    // This operation is used to get input from the user and execute it till
    // exit
    public static void processUserInput(String userInput) {
        CommandType command;
        _inputString = userInput;
        command = determineCommand();
        processUserCommand(command);
    }

    // This operation is used to get the user input and extract the command from
    // inputString
    private static CommandType determineCommand() {
        CommandType command = null;
        return command;
    }

    // This operation is used to process the extracted command and call the
    // respective functions
    private static void processUserCommand(CommandType command) {
        switch (command) {
            case ADD: {
                // process inputString here
                // TaskDataManager.addTask(addparams);
                // addparams: (String des) (String des, Char prir) (String des,
                // Date dead) (String des, Date start, Date end)
                // ArrayList<ArrayList<String>> display =
                // TaskFilterManager.getAllTask();
                // call GUI display here
                break;
            }
            case DELETE: {
                // GUI: ask user confirmation
                // isConfirmedDelete()
                // process inputString here
                // TaskDataManager.deleteTask(deleteparams);
                // deleteparams: (int id)
                // ArrayList<ArrayList<String>> display =
                // TaskFilterManager.getAllTask();
                // call GUI display here
                break;
            }
            case EDIT: {
                // process inputString here
                // TaskDataManager.editTask(addparams);
                // editparams: (int id, String des) (int id, String des, Char
                // prir) (int id, Char prir)
                // (int id, String des, Date dead) (int id, Date dead)
                // (int id, String des, Date start, Date end) (int id, Date
                // start, Date end) // what happen if user want to change start
                // or end only?
                // ArrayList<ArrayList<String>> display =
                // TaskFilterManager.getAllTask();
                // call GUI display here
                break;
            }
            case FILTER: {
                // process inputString here
                // ArrayList<Task> display =
                // TaskFilterManager.filterTask(filterparams);
                // filterparams: (Filter object)
                // call GUI display here
                break;
            }
            case SEARCH: {
                // process inputString here
                // ArrayList<ArrayList<String>> display =
                // TaskFilterManager.searchTask(searchparams);
                // searchparams: (String word), (String word1, String word2,
                // String word1 + word2), ...
                // (algorithm: combination of words)
                // call GUI display here
                break;
            }
            case EXIT: {
                System.exit(0);
                break;
            }
            default: {
                break;
            }
        }
    }
}
