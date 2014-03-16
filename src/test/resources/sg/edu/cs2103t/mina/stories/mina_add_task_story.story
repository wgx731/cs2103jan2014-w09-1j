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
|add test|TODO task test has been added.|todo|test|