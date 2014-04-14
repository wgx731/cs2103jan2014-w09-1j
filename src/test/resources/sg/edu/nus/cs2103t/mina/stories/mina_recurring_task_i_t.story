MINA recurring task integration test story

Narrative:
In order to use MINA to create routine
As a user
I want to have a way to create recurring tasks so that I don't have to add them one by one.

Scenario:  Add new recurring tasks without duplicates

Given empty command input field
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should contains <task> at line number <line>
And the <type> list at line number <recurline> should be <recur>

Examples:
|command|feedback|type|recur|recurline|task|line|
|add one recur -by today -every day -until next 2 days|Operation completed.|deadline|\t\tRECUR|3|\t1. one recur by 11:59 pm|2|

Scenario:  Modify existing recurrung task

Given empty command input field
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should contains <task> at line number <line>
And the <type> list at line number <recurline> should be <recur>

Examples:
|command|feedback|type|task|line|recur|recurline|
|modify d1 two recur -all|Operation completed.|deadline|\t2. two recur by 11:59 pm|5|\t\tRECUR|6|
|modify d1 no recur|Operation completed.|deadline|\t1. no recur by 11:59 pm|2|\t\tRECUR|5|

Scenario:  Delete existing deadline task

Given empty command input field
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should not contains <task>

Examples:
|command|feedback|type|task|
|delete d1|Operation completed.|deadline|no recur|