Narrative:
In order for MINA to keep track tasks
As a user
I would like to add a new task by entering correct command

Scenario:  Add a new todo task

Given command input field is empty
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should contains <task>

Examples:
|command|feedback|type|task|
|add make MINA better|Operation completed|todo|make MINA better|
|add Mina's laundry -priority H|Operation completed|todo|Mina's laundry|

Scenario:  Add a new deadline task

Given command input field is empty
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should contains <task>

Examples:
|command|feedback|type|task|
|add CS2103T Project Manual -end 11/2/2014|Operation completed|deadline|11/2/2014 1.CS2103T Project Manual by 11:59 p.m.|
|add CS2103T User Guide -end 2014/2/13 2359|Operation completed|deadline|13/2/2014 2.CS2103T User Guide by 11:59 p.m.|

Scenario:  Add a new event task

Given command input field is empty
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should contains <task>

Examples:
|command|feedback|type|task|
|add CS2103T Meeting -start 11/2/2014 1230 -end 11/2/2014 1400|Operation completed|event|CS2103T Meeting |