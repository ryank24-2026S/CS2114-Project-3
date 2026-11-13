Poker Night — CS 2114 Project 3
Poker Night is a local, multiplayer Texas Hold'em game written in Java. Players join a table, receive two private cards, and compete by checking, calling, betting, raising, going all-in, or folding. The program manages blinds, betting turns, community cards, hand evaluation, and chip payouts. This repository contains our first working model-and-console implementation for Deliverable 3.
Running the project
The source files are in src/poker/ and use the poker package. Java is required. From the repository root, compile the source files:
javac -d out src/poker/*.java
Run the model independently (no view):
java -cp out poker.ModelDriver
ModelDriver creates a PokerModel with new Random(42), joins three sample players, starts a hand, and automatically calls or checks until the hand finishes. It prints the board, players' cards, results, and final chip balances. This demonstrates that model logic can be exercised without ConsoleView or PokerController.

Run the minimal console view:
java -cp out poker.ConsoleView
ConsoleView creates a PokerModel, passes it to PokerController, and reads commands from standard input. Example:
join Alex
join Jordan
start
status
hand
call
check
help
quit
The correct betting command depends on whose turn it is and the amount owed. raise 6 means raise to a total of 6 chips for that betting street, not raise by 6 chips. Supported commands are join NAME, start, next, status, hand, check, call, fold, bet AMOUNT, raise TOTAL, allin, help, and quit. Default starting chips are 100; blinds are 1/2; up to six players can join before play starts.

The repository also contains Eclipse project files, so it can be imported into Eclipse and poker.ModelDriver or poker.ConsoleView can be run as Java applications.
Current architecture and working slice
- Model: PokerModel coordinates player seating, game states, blinds, betting, community-card dealing, turn order, showdowns, and payouts. Player, Card, Deck, TurnQueue, and HandEvaluator are supporting model classes. HandEvaluator.HandValue represents comparable poker hand values. BettingRound exists as a separate helper class, although the current PokerModel manages most betting-round state directly.
- Queue: TurnQueue wraps a LinkedList-backed Queue<String> with enqueue, dequeue, peek, isEmpty, clear, and size. PokerModel.buildTurnQueue() queues eligible players who need to act in table order. The game dequeues a player after completing their action and rebuilds the waiting order as betting conditions change.
- Controller: PokerController parses text commands, checks basic command syntax and numeric amounts, invokes model methods, and converts results/errors to text replies.
- View: ConsoleView reads lines from the console and prints controller replies. It does not implement poker rules. The model does not depend on the console view.
This is a console MVP slice, not an online or graphical poker table. The source contains code for multiple betting streets, best-hand evaluation, and pot distribution, but comprehensive automated tests and full end-to-end validation are not yet included in the repository.
Reflection
What changed from the specification, and why?
Our specification proposed a PokerGame model, a PokerView, a PokerController, and additional model-support classes. The implementation instead uses PokerModel, ConsoleView, and PokerController. A command-line interface provides a simpler first view, allowing us to verify model behavior before investing time in a fuller user interface. The controller uses execute(String) and returns a Reply rather than the specification's handleJoin, handleAction, and refresh methods with typed ActionResult and GameSnapshot objects.

The original design separated betting into BettingRound, turn management into PlayerTurnQueue, hand values into HandValue, and payouts into PotResolver. The current implementation uses BettingRound as a separate class but keeps most active betting state and pot/side-pot payout calculations in PokerModel. The queue is named TurnQueue, and HandValue is nested inside HandEvaluator. This reduced the number of moving pieces for the first integrated slice, but leaves PokerModel larger and more responsible for coordinating game rules than we initially intended. The implementation also starts players with 100 chips by default rather than the 1,000 chips used in the specification's example test plan.

Input validation also changed: PokerController handles text parsing and returns a readable error when a user supplies an invalid command or nonnumeric amount, while PokerModel rejects invalid game actions and state transitions using exceptions. TurnQueue, Card, Deck, and Player validate their own inputs. These decisions keep rule enforcement inside the model while making console input easier to handle.
Did the model really stand alone?
Yes, structurally: ModelDriver constructs and uses PokerModel directly without creating a controller or view. PokerModel, TurnQueue, and the other model classes do not import or call ConsoleView. The view is responsible only for input and output, and the controller mediates model operations. One remaining design concern is that PokerController reads Player objects from PokerModel.getPlayers(), and getCurrentPlayer() returns a Player directly. This exposes more internal model detail to the controller than the planned viewer-specific snapshot boundary would have allowed. We should revisit that if we extend the interface, especially to protect private hole cards.
What happened to our unknowns?
Our original scope identified computer-controlled opponents, side pots, and player statistics/history as unknowns. We have not implemented computer opponents or persistent player statistics/history. We did add code inside PokerModel for all-in betting, uncalled-chip refunds, side-pot levels, ties, and pot awards. Those mechanics still need more focused edge-case tests before we can consider them fully verified.
What doesn't work yet, or still needs verification?
The current repository contains ModelDriver as a demonstration, but no dedicated JUnit test classes are present. We still need automated tests of normal and invalid player actions, queue behavior, card uniqueness and deck exhaustion, hand rankings and tie-breaks, heads-up and multi-player betting, blind rotation, short all-in raises, side pots, odd-chip splits, and repeated hands. We also need to run and document a complete interactive console session, including errors and replay, before claiming that all MVP cases work reliably. The interface is minimal and has no private per-player login, graphical table, online rooms, computer players, or persistent statistics.
Next Steps — Deliverable 4
Finish and verify the MVP
1. Compile and run both entry points (ModelDriver and ConsoleView) on the team's development setup; document any build or runtime failures.
2. Add JUnit tests for TurnQueue, Deck, Card, Player, HandEvaluator, and PokerModel. Test empty queue operations, invalid cards, exhausted deck, wrong-turn actions, illegal raises, and insufficient chips, and verify failed operations leave state unchanged where expected.
3. Exercise full poker hands with two and several players, across preflop, flop, turn, river, showdown, early folds, all-ins, and a second hand. Check that no chips are created or lost during payouts.
4. Fix discovered edge cases in betting order, blind rotation, hand comparison, refunds, ties, and side pots; then repeat the automated and console tests.
5. Improve player handoff/privacy in the console so that players cannot casually view someone else's hole cards during a shared-screen game; consider replacing direct Player exposure with limited, viewer-specific display data.
Planned non-trivial change feature: player statistics
For Deliverable 4, we plan to add session player statistics (games/hands played, hands won, and net chips won/lost) and a stats command to view them. This feature changes model state and is reachable through the controller and console view, so it is more than a cosmetic interface change. It also advances the player-statistics stretch goal, although we are not committing to persistent statistics across program restarts in this iteration.

Predicted classes affected: PokerModel and/or Player for recording outcomes, PokerController for the stats command, and ConsoleView for displaying results. A small new PlayerStats model class may be useful. Predicted classes unchanged: TurnQueue, Deck, Card, and HandEvaluator, because statistics should not alter turn ordering, dealing, or hand evaluation. We will compare these predictions with actual changes in Deliverable 4.
Stretch-goal decisions
- Attempt: Session-level player statistics and match summaries (the change feature described above).
- Continue verifying: Advanced all-in/side-pot handling already started inside PokerModel.
- Defer/drop for Deliverable 4: Online multiplayer rooms and computer-controlled opponents, which would add substantial networking or decision-making work beyond the current local-console MVP.
New tests needed for the change feature
Tests should verify that a completed hand increments the correct players' participation counts, winners receive the correct win count, folds and ties are handled consistently, and chip gains/losses match actual payouts. Stats should not change when a command is invalid or a hand has not completed. The controller should return a useful error for an unknown player's statistics request, and the console's stats output should match the model state. Regression tests should confirm that the new counters do not change dealing, queues, or payouts.
Proposed remaining ownership
Team member	Planned work for Deliverable 4
Mayank	Core model fixes, game-state transitions, and blind/betting integration tests
Ryan	TurnQueue, hand evaluation, payout edge cases, and model tests
Aaron	Console view, player handoff/privacy, and end-to-end gameplay checks
Elias	Controller command updates, stats interface, README coordination, and integration checks
All members	Review changes together, run the final integration test, and verify the MVP


The assignments above are a proposed plan and should be confirmed by the group before submission.
Work Attribution
The assignment requires attribution for each class and test file, including substantive implementation contributions. File authorship and code-review history cannot be reliably assigned from the present source alone, so each team member should complete or verify the following table before the group submits. Do not use the earlier specification's section-attribution table as a substitute for implementation attribution.
Source file	Team member(s)	Contribution (written / revised / reviewed / tested / GenAI-assisted)
src/poker/PokerModel.java	[verify]	[verify]
src/poker/Player.java	[verify]	[verify]
src/poker/Card.java	[verify]	[verify]
src/poker/Deck.java	[verify]	[verify]
src/poker/BettingRound.java	[verify]	[verify]
src/poker/TurnQueue.java	[verify]	[verify]
src/poker/HandEvaluator.java	[verify]	[verify]
src/poker/PokerController.java	[verify]	[verify]
src/poker/ConsoleView.java	[verify]	[verify]
src/poker/ModelDriver.java	[verify]	[verify]
Test files	None currently present; add rows when created	N/A


GenAI note: GenAI (ChatGPT) was used to examine the repository and assignment requirements and to draft/revise this README, including the reflection and next-steps language. The team should review the technical claims, adjust the plan to decisions actually made, and disclose any additional GenAI use in source code or tests accurately before submission.
