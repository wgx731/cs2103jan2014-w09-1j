MINA todo task test story

Narrative:
In order to use MINA to track tasks
As a user
I would like use todo task to manage my float tasks

Scenario:  Add a new todo task

Given empty command input field
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should contains <task>

Examples:
|command|feedback|type|task|
|add 'Mina's laundry' priority H|Operation completed.|todo|Mina's laundry|
|add do homework -priority L|Operation completed.|todo|do homework|
|add submit assignment -urgent|Operation completed.|todo|submit assignment|
