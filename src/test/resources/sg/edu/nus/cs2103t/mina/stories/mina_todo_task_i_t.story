MINA todo task integration test story

Narrative:
In order to use MINA to track tasks
As a user
I want to use todo task to manage my floating tasks

Scenario:  Add new todo tasks without duplicates

Given empty command input field
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should contains <task> at line number <line>
And the <type> list at line number <colorline> should be in <color> color

Examples:
|command|feedback|type|task|line|color|colorline|
|add 'Mina's laundry' priority H|Operation completed.|todo|1. Mina's laundry|1|orange|1|
|create submit assignment -urgent|Operation completed.|todo|2. submit assignment|2|orange|2|
|make submit assignment -urgent|Operation failed. Please try again.|todo|2. submit assignment|2|orange|2|
|new 'do homework'|Operation completed.|todo|3. do homework|3|yellow|3|
|+ walk the dog -unimportant|Operation completed.|todo|4. walk the dog|4|green|4|

Scenario:  Modify existing todo task

Given empty command input field
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should contains <task> at line number <line>
And the <type> list at line number <colorline> should be in <color> color

Examples:
|command|feedback|type|task|line|color|colorline|
|modify todo 1 Test modify|Operation completed.|todo|1. Test modify|1|orange|1|
|change td1 -trivial|Operation completed.|todo|3. Test modify|3|green|3|
|edit td3 'Mina's laundry' -urgent|Operation completed.|todo|1. Mina's laundry|1|orange|1|

Scenario:  Delete existing todo task

Given empty command input field
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should not contains <task>

Examples:
|command|feedback|type|task|
|delete todo 1|Operation completed.|todo|Mina's laundry|
|remove td1|Operation completed.|todo|submit assignment|
|rm td1|Operation completed.|todo|do homework|
|rm td3|Invalid command. Please re-enter.|todo|2. |
|- td1|Operation completed.|todo|walk the dog|
|- td1|Invalid command. Please re-enter.|todo|1. |