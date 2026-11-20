# Poker Night — CS 2114 Project 3

Poker Night is a local, multiplayer Texas Hold'em game written in Java. Players join a table, receive two private cards, and compete by checking, calling, betting, raising, going all-in, or folding. The program manages blinds, betting turns, community cards, hand evaluation, chip payouts, and session-level player statistics.

This repository contains our completed model-and-console implementation for Deliverable 4, including JUnit tests and a new player statistics feature.

## Running the Project

The source files are located in `src/poker/` and use the `poker` package. Java is required.

From the repository root, compile the application classes using:

```bash
javac -d out src/poker/*.java
```

The JUnit test classes require JUnit 4 on the classpath. In Eclipse, import the project, configure JUnit 4 if necessary, and run the test classes as JUnit tests.

### Run the Model Independently

```bash
java -cp out poker.ModelDriver
```

`ModelDriver` creates a `PokerModel` with a fixed random seed, adds three sample players, starts a hand, and automatically calls or checks until the hand finishes. It prints the board, players' cards, results, and final chip balances.

This demonstrates that the model works independently without requiring the controller or view.

### Run the Console View

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
fold
bet 10
raise 20
allin
stats
stats Alex
next
help
quit
```

The correct action depends on the current betting situation. A raise amount represents the total bet for that betting round.

The `stats` command displays statistics for all players, while `stats NAME` displays statistics for one player.

Players start with 100 chips by default, blinds are 1/2, and up to six players can participate.

The project can also be imported into Eclipse, where `ModelDriver` and `ConsoleView` can be run as Java applications.

## Current Implementation

The project follows the Model-View-Controller (MVC) structure.

### Model

`PokerModel` handles the main game logic, including player management, betting, blinds, dealing community cards, turn progression, hand completion, pot distribution, and player statistics.

Supporting model classes include:

- `Player`: stores player information, chip balances, cards, betting state, and session statistics.
- `Card`: represents a playing card with a rank and suit.
- `Deck`: creates, shuffles, and deals cards.
- `TurnQueue`: manages player turns using a queue.
- `HandEvaluator`: evaluates poker hands and compares their rankings.
- `BettingRound`: represents betting-round information.

### Controller

`PokerController` receives user commands, validates their format, calls the appropriate model methods, and returns responses or error messages.

It also supports the new `stats` command, allowing users to request statistics without directly accessing the model.

### View

`ConsoleView` provides a text-based interface with menus, card displays, player information, and gameplay commands.

The view communicates with the controller and displays the results of model operations without implementing the underlying poker rules.

`CardRenderer` supports the console interface by formatting playing cards for display.

### Queue Implementation

`TurnQueue` uses Java's `Queue` interface with a `LinkedList` implementation.

Players are enqueued according to betting order. After a player acts, that player is removed from the queue, and the model determines which players still need to act.

This keeps turn management separate from the user interface.

### Player Statistics Feature

For Deliverable 4, we implemented session-level player statistics as our non-trivial change feature.

The feature tracks:

- Hands played
- Hands won
- Net chips gained or lost

The `Player` class stores the statistics and provides methods including `recordHandPlayed()`, `recordHandWon()`, `getNetChips()`, and `getStatistics()`.

`PokerModel` updates the statistics when hands finish and provides `getPlayerStats(String playerId)` and `getAllPlayerStats()` to retrieve them.

`PokerController` recognizes `stats` and `stats NAME`, requests the information from the model, and returns the results.

`ConsoleView` makes the feature accessible through the console menu and command input.

Net chips are calculated as the player's current chip balance minus their starting chip balance. Statistics are maintained during the current game session rather than saved between program executions.

## Testing

The project includes JUnit tests for the main model components.

The test files are:

- `BettingRoundTest.java`
- `CardTest.java`
- `DeckTest.java`
- `HandEvaluatorTest.java`
- `PlayerTest.java`
- `PokerModelTest.java`
- `TurnQueueTest.java`

These tests cover normal operations and invalid inputs, including player creation, chip management, card validation, deck operations, hand evaluation, turn queue behavior, betting-round state, and model initialization.

The tests execute successfully, and the application runs correctly through both the independent model driver and console interface.

The statistics feature is also accessible through the completed application.

## Reflection

### How Did Our Design Absorb the Change?

For Deliverable 4, our non-trivial change was adding session-level player statistics.

In our Deliverable 3 Next Steps section, we predicted that the feature would primarily affect `Player`, `PokerModel`, `PokerController`, and `ConsoleView`. This prediction was accurate.

We updated `Player` to track hands played, hands won, and net chips. We also added methods to record completed hands and wins and to retrieve formatted statistics.

`PokerModel` was updated to record statistics during hand completion and pot distribution. It also provides methods for retrieving statistics for individual players or the entire table.

`PokerController` was updated to recognize statistics commands, and `ConsoleView` was updated to make the feature accessible through the user interface.

We originally expected to add a method such as `getPlayerStats(String playerId)` to the model. We ended up implementing that method along with `getAllPlayerStats()`, which made it easier to display information for the entire table.

We also predicted that classes such as `TurnQueue`, `Deck`, `Card`, and `HandEvaluator` would not need statistics-specific changes. The feature did not require changes to their core responsibilities.

Overall, the change was absorbed well because each class already had a defined responsibility. The statistics feature required changes across multiple components, but we did not need to redesign the entire program.

### Did the Change Cross the Model/View Line?

The statistics feature required updates to the model, controller, and view, but it did not break the intended MVC separation.

The model remains responsible for tracking and updating player statistics. The controller handles commands and retrieves the requested information. The view displays the results.

The view needed to support a new command and menu option, but it did not need to calculate statistics or determine which players won a hand.

One area where the implementation could be improved is how player information is exposed. The controller and view can access `Player` objects, which creates some coupling between the interface and the model's internal representation.

A future improvement would be to provide more restricted representations of game information.

However, the statistics feature itself fit into the existing structure without requiring unrelated classes to take on new responsibilities.

### What Changed From Our Deliverable 3 Plan?

Most of our original predictions were accurate, but some implementation details changed.

We originally planned to add fields for hands played and hands won, along with methods for updating them. We implemented these methods and also used the player's starting and current chip balances to calculate net chips.

This avoided the need to separately update a net-chip statistic after every betting action.

We also added support for retrieving statistics for all players, rather than requiring users to request each player's information individually.

The controller recognizes both `stats` and `stats NAME`, which gives users two ways to access the feature.

Our testing also expanded from the Deliverable 3 prototype. We added dedicated JUnit test classes for the major model components and tested normal behavior and invalid inputs.

The overall MVC structure remained consistent with our earlier implementation.

### What Doesn't Work Yet?

The main gameplay features and the new statistics feature are functional, and the existing tests pass.

However, there are still opportunities to improve the program.

The current interface is console-based and does not provide a graphical poker table. The game also does not support online multiplayer or computer-controlled opponents.

Player statistics are maintained during the current session but are not saved between program executions.

Although the model includes logic for all-in betting, side pots, ties, and pot distribution, additional tests for more complicated gameplay situations would improve our confidence in those features.

We could also improve how the controller and view access player information to create stronger separation between the model and interface.

These limitations do not prevent the current MVP from running successfully.

## Next Steps

### What Is Left for the Final Build?

Our main gameplay implementation and player statistics feature are complete.

For the final build, we would prioritize the following improvements:

1. Add more tests for complete hands, repeated hands, ties, and side-pot situations.
2. Expand integration testing to cover the controller and statistics commands.
3. Improve the console interface and player handoff process.
4. Refine error messages and continue checking unusual input combinations.
5. Review the MVC structure and reduce unnecessary dependencies between the model and view.
6. Complete the final demonstration and documentation.

These improvements would make the program more reliable and easier to use without changing its main functionality.

### Stretch Goals

Our main additional feature, session-level player statistics, has been implemented.

We will continue prioritizing improvements to advanced betting behavior and side-pot handling.

We are not committing to online multiplayer, computer-controlled opponents, or a full graphical interface for the final build. These features would require significantly more development and testing.

Persistent statistics could also be added in the future, but the current implementation focuses on tracking information within a single session.

### Three-Minute Demo Plan

Our final demonstration will show the main gameplay flow and the new statistics feature.

**Minute 1: Starting the Game**

We will launch `ConsoleView`, add multiple players, and start a hand. We will briefly show the table, player chip balances, and available commands.

**Minute 2: Gameplay**

We will demonstrate betting actions such as checking, calling, raising, and folding. We will show how the program manages turns, advances through betting rounds, and determines the result of a hand.

**Minute 3: Player Statistics**

After completing a hand, we will use `stats` to display statistics for all players.

We will also use `stats NAME` to show an individual player's hands played, hands won, and net chips.

This will demonstrate that the new feature works through the view and controller while the model manages the underlying information.

### Division of Work

| Team Member | Remaining Responsibilities |
|---|---|
| Mayank | Review model behavior, expand tests, and verify game-state transitions |
| Ryan | Review turn order, hand evaluation, payout edge cases, and model testing |
| Aaron | Review console usability, player handoff, and gameplay scenarios |
| Elias | Finalize README documentation, review statistics command integration, and help prepare the demonstration |

All team members will review the final implementation and participate in verifying that the project meets the deliverable requirements.

## Work Attribution

The following table identifies the contributors to the project's Java classes and test files.

| Class or Test File | Team Member(s) | Contribution |
|---|---|---|
| `PokerModel.java` | Ryan | Wrote main model implementation |
| `PokerModelTest.java` | Mayank | Wrote model tests |
| `Player.java` | Aaron | Wrote player implementation |
| `PlayerTest.java` | Mayank | Wrote player tests |
| `Card.java` | Elias | Wrote card implementation |
| `CardRenderer.java` | Ryan | Wrote card rendering implementation |
| `CardTest.java` | Mayank | Wrote card tests |
| `ConsoleView.java` | Ryan | Wrote console view implementation |
| `Deck.java` | Elias | Wrote deck implementation |
| `DeckTest.java` | Mayank | Wrote deck tests |
| `BettingRound.java` | Mayank | Wrote betting-round implementation |
| `BettingRoundTest.java` | Mayank | Wrote betting-round tests |
| `TurnQueue.java` | Aaron | Wrote turn queue implementation |
| `TurnQueueTest.java` | Elias | Wrote turn queue tests |
| `HandEvaluator.java` | Mayank | Wrote hand evaluation implementation |
| `HandEvaluatorTest.java` | Mayank | Wrote hand evaluation tests |
| `PokerController.java` | Mayank | Wrote controller implementation |
| `ModelDriver.java` | Ryan | Wrote independent model driver |

The team also collaborated on integration, debugging, testing, and the implementation of the player statistics feature.

### GenAI Usage

GenAI (ChatGPT) was used to assist with organizing, revising, and updating the README documentation.
