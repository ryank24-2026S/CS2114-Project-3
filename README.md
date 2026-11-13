
# Poker Night — CS 2114 Project 3

Poker Night is a local, multiplayer Texas Hold'em game written in Java. Players join a table, receive two private cards, and compete by checking, calling, betting, raising, going all-in, or folding. The program manages blinds, betting turns, community cards, hand evaluation, and chip payouts. This repository contains our first working model-and-console implementation for Deliverable 3.

## Running the Project

The source files are in `src/poker/` and use the `poker` package. Java is required.

From the repository root, compile the source files:

```bash
javac -d out src/poker/*.java
```

### Run the model independently

```bash
java -cp out poker.ModelDriver
```

`ModelDriver` creates a `PokerModel` with a fixed random seed, adds three sample players, starts a hand, and automatically calls or checks until the hand finishes. It prints the board, players' cards, results, and final chip balances.

This demonstrates that the model can be exercised without the controller or view.

### Run the console view

```bash
java -cp out poker.ConsoleView
```

The console view accepts commands such as:

```text
join Alex
join Jordan
start
status
hand
call
check
help
quit
```

Other supported commands include `next`, `fold`, `bet AMOUNT`, `raise TOTAL`, and `allin`.

The correct action depends on the current betting situation. A raise amount represents the total bet for that betting round.

Players start with 100 chips by default, blinds are 1/2, and up to six players can participate.

The project can also be imported into Eclipse, where `ModelDriver` and `ConsoleView` can be run as Java applications.

## Current Implementation

The project follows the Model-View-Controller (MVC) structure.

### Model

`PokerModel` handles the main game logic, including player management, betting, blinds, dealing community cards, turn progression, hand completion, and pot distribution.

Supporting model classes include:

- `Player`: stores player information, chip balances, cards, and betting state.
- `Card`: represents a playing card with a rank and suit.
- `Deck`: creates, shuffles, and deals cards.
- `TurnQueue`: manages player turns using a queue.
- `HandEvaluator`: evaluates poker hands and compares their rankings.
- `BettingRound`: represents betting-round information.

### Controller

`PokerController` receives user commands, validates their format, calls the appropriate model methods, and returns responses or error messages.

### View

`ConsoleView` provides a simple text-based interface. It displays game information and accepts user input without implementing poker rules itself.

### Queue Implementation

`TurnQueue` uses Java's `Queue` interface with a `LinkedList` implementation.

Players are enqueued according to betting order. After a player acts, that player is removed from the queue, and the model determines which players still need to act.

This allows turn management to remain separate from the user interface.

## Reflection

### What changed from our specification?

Our original specification proposed classes such as `PokerGame`, `PokerView`, `PokerController`, `PlayerTurnQueue`, and `PotResolver`.

During implementation, we made several adjustments.

We used `PokerModel` instead of `PokerGame` and created `ConsoleView` as our initial interface. This allowed us to focus on getting the game logic working before developing a more advanced view.

We also implemented `TurnQueue` as our queue class and placed the hand-value representation inside `HandEvaluator`.

Although `BettingRound` exists as a separate class, much of the active betting logic is currently handled directly by `PokerModel`. Pot distribution and side-pot calculations are also handled inside the main model instead of a separate `PotResolver`.

The controller implementation also differs from the specification. Instead of separate methods for each user interaction, it uses `execute(String)` to interpret commands and return responses.

These changes simplified the first implementation and made it easier to connect the different components. However, they also made `PokerModel` responsible for more functionality than originally planned.

The default starting chip count was also changed to 100.

### Did the model really stand alone?

The current design keeps the model independent of the view.

`ModelDriver` creates and interacts with `PokerModel` directly without using `ConsoleView` or `PokerController`.

The model classes do not depend on the console interface. The controller communicates with the model, while the view handles displaying information and collecting commands.

One area we may improve is how player information is exposed to the controller. Currently, the controller can access `Player` objects directly. A more restricted representation of game information could improve separation and protect private player cards.

### What happened to our unknowns?

Our original scope identified several areas that required additional research or implementation, including computer-controlled opponents, side pots, and player statistics.

We have implemented logic for all-in betting, side pots, ties, and pot distribution, although these features still require additional testing.

Computer-controlled opponents and persistent player statistics have not been implemented.

We also gained a better understanding of how to coordinate player turns using a queue and how to connect the model, controller, and view.

### What doesn't work yet?

The current implementation includes the main game classes and a console interface, but several areas still require testing and improvement.

The repository does not currently contain dedicated JUnit test classes.

We still need to test invalid player actions, betting restrictions, turn order, hand evaluation, blind rotation, side pots, and repeated hands.

We also need to verify that complete interactive games run correctly under different situations.

The current interface is text-based and does not include a graphical poker table, online multiplayer, computer opponents, or persistent player statistics.

## Next Steps — Deliverable 4

### What is left to finish the MVP?

Our first priority is to verify that the current model and console interface work correctly.

We plan to:

1. Compile and run both `ModelDriver` and `ConsoleView`.
2. Add JUnit tests for the main model classes.
3. Test complete games with different numbers of players.
4. Verify betting, folding, all-in situations, and hand evaluation.
5. Test pot distribution, ties, and side pots.
6. Fix any bugs discovered during testing.
7. Improve the console interface and player handoff process.
8. Run final integration tests before submission.

### Non-Trivial Change Feature

For Deliverable 4, our proposed change feature is session-level player statistics.

This feature would track information such as:

- Number of hands played
- Number of hands won
- Total chips won or lost

Players would be able to view their statistics using a new `stats` command.

This feature changes the model's stored information and requires interaction through the controller and view, making it more than a visual change.

We predict that the following classes will need modifications:

- `Player` and/or `PokerModel`: track and update player statistics.
- `PokerController`: process the new statistics command.
- `ConsoleView`: display the statistics.

We may also introduce a `PlayerStats` class.

We do not expect `TurnQueue`, `Deck`, `Card`, or `HandEvaluator` to require changes because player statistics should not affect turn order, card dealing, or hand evaluation.

We will compare these predictions with the actual implementation in Deliverable 4.

### Stretch Goals

We plan to attempt session-level player statistics as our main additional feature.

We will also continue improving and testing advanced betting and side-pot behavior.

Online multiplayer and computer-controlled opponents will be deferred because they require additional functionality beyond the current local implementation.

### New Tests Needed

We will need tests to verify that:

- Player statistics update correctly after completed hands.
- Winners receive the correct win counts.
- Chip gains and losses are recorded correctly.
- Ties and folds are handled properly.
- Invalid commands do not change statistics.
- The controller displays the correct statistics.
- The new feature does not interfere with existing game logic.

We will also add regression tests for the queue, betting system, and pot distribution.

### Division of Work

| Team Member | Planned Responsibilities |
| --- | --- |
| Mayank | Model fixes, game-state transitions, and betting integration tests |
| Ryan | Turn queue, hand evaluation, payout edge cases, and model testing |
| Aaron | Console view improvements, player handoff, and gameplay testing |
| Elias | Controller updates, statistics command, README coordination, and integration testing |

All team members will review the final implementation and help verify that the MVP works correctly.

These responsibilities are proposed and will be confirmed by the group.

## Work Attribution

The following table should be completed using each team member's actual contributions before submission.

| Class or Test File | Team Member(s) | Contribution |
| --- | --- | --- |
| `PokerModel.java` | [Verify] | [Written / Revised / Reviewed / Tested] |
| `Player.java` | [Verify] | [Written / Revised / Reviewed / Tested] |
| `Card.java` | [Verify] | [Written / Revised / Reviewed / Tested] |
| `Deck.java` | [Verify] | [Written / Revised / Reviewed / Tested] |
| `BettingRound.java` | [Verify] | [Written / Revised / Reviewed / Tested] |
| `TurnQueue.java` | [Verify] | [Written / Revised / Reviewed / Tested] |
| `HandEvaluator.java` | [Verify] | [Written / Revised / Reviewed / Tested] |
| `PokerController.java` | [Verify] | [Written / Revised / Reviewed / Tested] |
| `ConsoleView.java` | [Verify] | [Written / Revised / Reviewed / Tested] |
| `ModelDriver.java` | [Verify] | [Written / Revised / Reviewed / Tested] |

No dedicated JUnit test files are currently present in the repository.

### GenAI Usage

GenAI (ChatGPT) was used to review the repository and assignment requirements and assist with drafting and organizing the README, including the reflection and next-steps sections.

The team should verify the documentation and disclose any additional GenAI assistance used in developing or testing the source code.

---

**Verification Note:** This README is based on an inspection of the current repository source code. The application has not been independently compiled or executed as part of this review, so runtime behavior and test results still need to be verified.
