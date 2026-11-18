package poker;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ConsoleView {
    private static final String BORDER =
        "================================================================";

    private static final String DIVIDER =
        "----------------------------------------------------------------";

    private final PokerModel model;
    private final PokerController controller;
    private final Scanner sc;

    public ConsoleView(
        PokerModel model,
        PokerController controller,
        Scanner sc) {
        if (model == null || controller == null || sc == null) {
            throw new IllegalArgumentException(
                "Model, controller, and input are required.");
        }

        this.model = model;
        this.controller = controller;
        this.sc = sc;
    }


    public static void main(String[] args) {
        PokerModel model = new PokerModel(new Random());
        PokerController controller = new PokerController(model);
        Scanner sc = new Scanner(System.in);

        ConsoleView view = new ConsoleView(model, controller, sc);
        view.run();
    }


    public void run() {
        String message = "Welcome! Add at least two players to begin.";

        while (true) {
            displayScreen(message);

            String selection = readLine("> Enter choice or command: ");

            if (selection == null) {
                System.out.println("\nGoodbye.");
                return;
            }

            String command = resolveCommand(selection.trim());

            if (command == null) {
                System.out.println("\nGoodbye.");
                return;
            }

            if ("hand".equalsIgnoreCase(command)) {
                if (!showCurrentHand()) {
                    System.out.println("\nGoodbye.");
                    return;
                }

                message = "";
                continue;
            }

            PokerController.Reply reply = controller.execute(command);

            if (reply.shouldQuit()) {
                System.out.println(reply.getText());
                return;
            }

            if (reply.getText().startsWith("State: ")) {
                message = "";
            }
            else {
                message = reply.getText();
            }
        }
    }


    private void displayScreen(String message) {
        System.out.println();
        System.out.println(BORDER);
        System.out.println("                         POKER TABLE");
        System.out.println(BORDER);

        if (model.getState() == PokerModel.State.LOBBY) {
            displayLobby();
        }
        else {
            displayTable();
        }

        if (message != null && !message.isEmpty()) {
            System.out.println();
            System.out.println(message);
        }

        System.out.println();
        displayMenu();
        System.out.println();
    }


    private void displayLobby() {
        System.out.println("LOBBY");
        System.out.println("Players: " + model.getPlayers().size() + "/6");
        System.out.println("Starting chips: 100 | Blinds: 1/2");
        System.out.println(DIVIDER);

        List<Player> players = model.getPlayers();

        if (players.isEmpty()) {
            System.out.println("No players yet.");
        }
        else {
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);

                System.out.printf(" [%d] %-20s %6d chips%n", i + 1, player
                    .getName(), player.getChips());
            }
        }

        System.out.println(DIVIDER);
        System.out.println("Add 2-6 players, then start the game.");
    }


    private void displayTable() {
        System.out.println("Hand: " + model.getHandNumber() + " | Stage: "
            + model.getStage() + " | Dealer: " + model.getDealerName());

        System.out.println("State: " + model.getState());

        if (model.getState() == PokerModel.State.PLAYING) {
            System.out.println("Pot: " + model.getPot()
                + " chips | Current bet: " + model.getCurrentBet());
        }
        else {
            System.out.println(
                "Hand complete. Payouts are included in the chip stacks.");
        }

        System.out.println(DIVIDER);
        System.out.println("COMMUNITY CARDS");
        System.out.println();
        System.out.println(CardRenderer.render(model.getCommunityCards(), 5));
        System.out.println();
        System.out.println("Suits: C = Clubs, D = Diamonds, "
            + "H = Hearts, S = Spades");
        System.out.println();

        displayPlayers();

        if (model.getState() == PokerModel.State.PLAYING) {
            displayTurn();
        }
        else {
            displayResults();
        }
    }


    private void displayPlayers() {
        Player current = model.getCurrentPlayer();

        System.out.println(DIVIDER);
        System.out.printf("   %-20s %7s %11s  %s%n", "Player", "Chips",
            "Street bet", "Status");
        System.out.println(DIVIDER);

        for (Player player : model.getPlayers()) {
            String marker = player == current ? ">" : " ";

            System.out.printf(" %s %-20s %7d %11d  %s%n", marker, player
                .getName(), player.getChips(), player.getRoundBet(),
                getPlayerStatus(player, current));
        }

        System.out.println(DIVIDER);
    }


    private String getPlayerStatus(Player player, Player current) {
        if (player.isFolded()) {
            return "Folded";
        }

        if (!player.isInHand()) {
            return "Not dealt in";
        }

        if (model.getState() != PokerModel.State.PLAYING) {
            return "Hand complete";
        }

        if (player.isAllIn()) {
            return "All-in";
        }

        if (player == current) {
            return "Acting";
        }

        return "In hand";
    }


    private void displayTurn() {
        Player player = model.getCurrentPlayer();

        if (player == null) {
            return;
        }

        System.out.println();
        System.out.println("TURN: " + player.getName());
        System.out.println("Amount to call: " + model.getAmountToCall()
            + " chips");
        System.out.println("Minimum full bet/raise total: " + model
            .getMinimumRaiseTo() + " chips");
        System.out.println("A smaller increase is allowed only when the model "
            + "permits an all-in.");
        System.out.println(
            "Use View cards only when the current player is ready.");
    }


    private void displayResults() {
        System.out.println();
        System.out.println("RESULTS");

        for (String result : model.getResults()) {
            System.out.println(" - " + result);
        }

        if (model.getCommunityCards().size() == 5) {
            System.out.println();
            System.out.println("SHOWDOWN");

            for (Player player : model.getPlayers()) {
                if (player.isActive()) {
                    System.out.println();
                    System.out.println(player.getName() + " - " + model
                        .getHandValue(player).getLabel());

                    System.out.println(CardRenderer.render(player.getCards()));
                }
            }
        }

        if (model.getState() == PokerModel.State.GAME_OVER) {
            System.out.println();
            System.out.println("Game over.");
        }
    }


    private void displayMenu() {
        if (model.getState() == PokerModel.State.LOBBY) {
            System.out.println("[1] Add player");
            System.out.println("[2] Start game");
        }
        else if (model.getState() == PokerModel.State.PLAYING) {
            System.out.println("[1] View cards");
            System.out.println("[2] Check");
            System.out.println("[3] Call");
            System.out.println("[4] Bet");
            System.out.println("[5] Raise");
            System.out.println("[6] Fold");
            System.out.println("[7] All-in");
        }
        else if (model.getState() == PokerModel.State.HAND_FINISHED) {
            System.out.println("[1] Start next hand");
        }

        System.out.println("[8] Help");
        System.out.println("[9] Refresh table");
        System.out.println("[0] Quit");
        System.out.println("You may also type an existing game command.");
    }


    /**
     * Converts menu choices into existing controller commands.
     * Non-menu input is passed through unchanged.
     */
    private String resolveCommand(String selection) {
        if ("0".equals(selection)) {
            return "quit";
        }

        if ("8".equals(selection)) {
            return "help";
        }

        if ("9".equals(selection)) {
            return "status";
        }

        if (model.getState() == PokerModel.State.LOBBY) {
            if ("1".equals(selection)) {
                return promptCommand("join", "Player name: ");
            }

            if ("2".equals(selection)) {
                return "start";
            }
        }
        else if (model.getState() == PokerModel.State.PLAYING) {
            switch (selection) {
                case "1":
                    return "hand";

                case "2":
                    return "check";

                case "3":
                    return "call";

                case "4":
                    return promptCommand("bet", "Bet total for this street: ");

                case "5":
                    return promptCommand("raise",
                        "New total bet for this street: ");

                case "6":
                    return "fold";

                case "7":
                    return "allin";

                default:
                    break;
            }
        }
        else if (model.getState() == PokerModel.State.HAND_FINISHED && "1"
            .equals(selection)) {

            return "next";
        }

        return selection;
    }


    private String promptCommand(String command, String prompt) {
        String argument = readLine(prompt);

        if (argument == null) {
            return null;
        }

        return command + " " + argument.trim();
    }


    /**
     * Requests the current hand through the controller before displaying it.
     *
     * @return false if input has closed; otherwise true
     */
    private boolean showCurrentHand() {
        PokerController.Reply reply = controller.execute("hand");

        Player player = model.getCurrentPlayer();

        if (player == null) {
            System.out.println(reply.getText());
            return readLine("Press Enter to return...") != null;
        }

        System.out.println();
        System.out.println(DIVIDER);
        System.out.println(player.getName() + "'s cards");
        System.out.println(DIVIDER);
        System.out.println(CardRenderer.render(player.getCards()));
        System.out.println();

        String response = readLine(
            "Press Enter to hide this display and return...");

        if (response == null) {
            return false;
        }

        for (int i = 0; i < 35; i++) {
            System.out.println();
        }

        return true;
    }


    private String readLine(String prompt) {
        System.out.print(prompt);

        if (!sc.hasNextLine()) {
            return null;
        }

        return sc.nextLine();
    }
}