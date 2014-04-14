MINA deadline task integration test story

Narrative:
In order to use MINA to track deadlines
As a user
I want to use dealine task to manage my deadlines

Scenario:  Add new deadline tasks without duplicates

Given empty command input field
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should contains <task> at line number <line>
And the <type> list at line number <colorline> should be in <color> color
And the <type> list at line number <dateline> should be <date>

Examples:
|command|feedback|type|task|line|colorline|color|dateline|date|
|add 'Mina's laundry' -by 2-3-2014 2234|Operation completed.|deadline|\t1. Mina's laundry by 10:34 pm|2|1|gray|1|Sunday, 02 Mar 2014|
|add Assignment -by 10:34am today|Operation completed.|deadline|\t2. Assignment by 10:34 am|4|3|orange|3|Today|
|add Assignment no 2 -by next friday|Operation completed.|deadline|\t3. Assignment no 2 by 11:59 pm|6|5|green|5|Next Friday|
|add -by 11.11am next mon Assignment no 3|Operation completed.|deadline|\t3. Assignment no 3 by 11:11 am|6|5|green|5|Next Monday|
|add -by asas next monday Assignment no 3|Invalid command. Please re-enter.|deadline|\t3. Assignment no 3 by 11:11 am|6|5|green|5|Next Monday|
|add -by Assignment no 3|Invalid command. Please re-enter.|deadline|\t3. Assignment no 3 by 11:11 am|6|5|green|5|Next Monday|

Scenario:  Modify existing deadline task

Given empty command input field
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should contains <task> at line number <line>
And the <type> list at line number <colorline> should be in <color> color
And the <type> list at line number <dateline> should be <date>

Examples:
|command|feedback|type|task|line|colorline|color|dateline|date|
|modify deadline 1 Mina|Operation completed.|deadline|\t1. Mina by 10:34 pm|2|1|gray|1|Sunday, 02 Mar 2014|
|modify d 2 -by next Sun|Operation completed.|deadline|\t4. Assignment by 11:59 pm|8|7|green|7|Next Sunday|
|modify d3 -by next friday 11am              'whahaha'|Operation completed.|deadline|\t3. whahaha by 11:00 am|6|5|green|5|Next Friday|
|modify deadlines 2 'start and end done' by tmr|Operation completed.|deadline|\t2. start and end done by 11:59 pm|4|3|yellow|3|Tomorrow|
|modify deadlines 2|Invalid command. Please re-enter.|deadline|\t2. start and end done by 11:59 pm|4|3|yellow|3|Tomorrow|
|modify deadlines 100|Invalid command. Please re-enter.|deadline|\t2. start and end done by 11:59 pm|4|3|yellow|3|Tomorrow|

Scenario:  Delete existing deadline task

Given empty command input field
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should not contains <task>

Examples:
|command|feedback|type|task|
|delete deadline 1|Operation completed.|deadline|Mina by 10:34 pm|
|delete d999|Invalid command. Please re-enter.|deadline|Mina by 10:34 pm|