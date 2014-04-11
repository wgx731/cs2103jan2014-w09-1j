MINA add task integration test story

Narrative:
In order to use MINA to track tasks
As a user
I would like to type command to add new tasks to MINA

Scenario:  Add a new todo task

Given empty command input field
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should contains <task>

Examples:
|command|feedback|type|task|
|add 'Mina's laundry' priority H|Operation completed|todo|Mina's laundry|
|add 'do homework' priority L|Operation completed|todo|do homework|

Scenario:  Add a new deadline task

Given empty command input field
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should contains <task>

Examples:
|command|feedback|type|task|
|add 'CS2103T Project Manual' -end 11/2/2014|Operation completed|deadline|Tuesday, 11 Feb 2014\n\t1. CS2103T Project Manual by 11:59 pm|
|add 'CS2103T User Guide' -end 2014/2/13 2200|Operation completed|deadline|Thursday, 13 Feb 2014\n\t2. CS2103T User Guide by 10:00 pm|

Scenario:  Add a new event task

Given empty command input field
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should contains <task>

Examples:
|command|feedback|type|task|
|add 'CS2103T Meeting' -start 11/2/2014 1230 -end 11/2/2014 1400|Operation completed|event|Tuesday, 11 Feb 2014\n\t1. CS2103T Meeting\n\tstart: 12:30 pm\n\tend: 2:00 pm|
|add 'CS2101 Meeting' -from 12/2/2014 0800 -to 12/2/2014 1000|Operation completed|event|Wednesday, 12 Feb 2014\n\t2. CS2101 Meeting\n\tstart: 8:00 am\n\tend: 10:00 am|