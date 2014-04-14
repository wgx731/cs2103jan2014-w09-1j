package sg.edu.nus.cs2103t.mina.stub;

import sg.edu.nus.cs2103t.mina.commandcontroller.CommandProcessor;
import sg.edu.nus.cs2103t.mina.model.TaskView;

//@author A0099151B

public class CommandProcessorStub extends CommandProcessor{
    
    public CommandProcessorStub() {
        super();
    }
    
    public TaskView processUserInput(String input){
        return new TaskView(input);
    }
    
}
