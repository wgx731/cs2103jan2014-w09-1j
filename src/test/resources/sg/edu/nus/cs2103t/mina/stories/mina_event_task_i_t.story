MINA event task integration test story

Narrative:
In order to use MINA to track events
As a user
I want to use event task to manage my events

Scenario:  Add new event tasks without duplicates

Given empty command input field
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should contains <task> at line number <line>
And the <type> list at line number <colorline> should be in <color> color
And the <type> list at line number <dateline> should be <date>
And the <type> list at line number <daterangeline> should be a time range of <daterange>

Examples:
|command|feedback|type|task|line|color|colorline|date|dateline|daterange|daterangeline|
|add mina's laundry event -start 9am -end 9pm|Operation completed.|event|\t1. mina's laundry event|2|orange|1|Today|1|\t\t9:00 am - 9:00 pm|3|
|add meet friends -from tmr 0900 -to 2100 tmr|Operation completed.|event|\t2. meet friends|5|yellow|4|Tomorrow|4|\t\t9:00 am - 9:00 pm|6|
|add -from next monday 9:00 this is going a long event -to next tuesday 21:00|Operation completed.|event|\t3. this is going a long event|8|green|7|Next Monday|7|\t\t9:00 am - Next Tuesday 9:00 pm|9|
|add -due 12/12 9.00am back to the future! -starting next tue|Operation completed.|event|\t1. back to the future!|2|green|1|Next Tuesday|1|\t\t12:00 am - Friday, 12 Dec 2014 9:00 am|3|
|add -start 12/12 time travel -end today|Invalid command. Please re-enter.|event|\t1. back to the future!|2|green|1|Next Tuesday|1|\t\t12:00 am - Friday, 12 Dec 2014 9:00 am|3|

Scenario:  Delete existing event task

Given empty command input field
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should not contains <task>

Examples:
|command|feedback|type|task|
|delete event 1|Operation completed.|event|back to the future!|
|delete d999|Invalid command. Please re-enter.|deadline|back to the future!|
|delete e1|Operation completed.|event|mina's laundry event|

Scenario:  Modify existing event task

Given empty command input field
When I enter <command>
Then the status bar should show <feedback>
And the <type> list should contains <task> at line number <line>
And the <type> list at line number <colorline> should be in <color> color
And the <type> list at line number <dateline> should be <date>
And the <type> list at line number <daterangeline> should be a time range of <daterange>

Examples:
|command|feedback|type|task|line|color|colorline|date|dateline|daterange|daterangeline|
|modify e1 meet imaginary friends|Operation completed.|event|\t1. meet imaginary friends|2|yellow|1|Tomorrow|1|\t\t9:00 am - 9:00 pm|3|
|modify e2 -start friday|Operation completed.|event|\t2. this is going a long event|5|green|4|Friday|4|\t\t12:00 am - Next Tuesday 9:00 pm|6|
|modify e2 -start thursday 1200 -end friday 1300|Operation completed.|event|\t2. this is going a long event|5|green|4|Thursday|4|\t\t12:00 pm - Friday 1:00 pm|6|