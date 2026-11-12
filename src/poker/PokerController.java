package poker;

public class PokerController {

    private PokerModel model;

    public PokerController(PokerModel model) {
        if (model == null) {
            throw new IllegalArgumentException(
                "Model cannot be null.");
        }

        this.model = model;
    }

    public Reply execute(String input) {
        if (input == null) {
            return new Reply("Goodbye.", true);
        }

        String line = input.trim();

        if (line.isEmpty()) {
            return new Reply(
                "Enter a command. Try help.", false);
        }

        String[] parts = line.split("\\s+", 2);

        String command = parts[0].toLowerCase();
        String argument = "";

        if (parts.length == 2) {
            argument = parts[1].trim();
        }

        try {
            boolean allowsArgument =
                command.equals("join")
                || command.equals("bet")
                || command.equals("raise");

            if (!allowsArgument && !argument.isEmpty()) {
                throw new IllegalArgumentException(
                    "Unexpected argument.");
            }

            if (command.equals("join")) {
                model.addPlayer(argument);
            }
            else if (command.equals("start")) {
                if (model.getState() != PokerModel.State.LOBBY) {
                    throw new IllegalStateException(
                        "Use next for another hand.");
                }

                model.startRound();
            }
            else if (command.equals("next")) {
                model.startNextHand();
            }
            else if (command.equals("check")) {
                model.check();
            }
            else if (command.equals("call")) {
                model.call();
            }
            else if (command.equals("fold")) {
                model.fold();
            }
            else if (command.equals("bet")) {
                model.bet(readAmount(argument));
            }
            else if (command.equals("raise")) {
                model.raiseTo(readAmount(argument));
            }
            else if (command.equals("allin")) {
                model.allIn();
            }
            else if (command.equals("hand")) {
                Player player = model.getCurrentPlayer();

                if (player == null) {
                    throw new IllegalStateException(
                        "No turn is active.");
                }

                return new Reply(
                    player.getName() + ": " + player.getCards(),
                    false);
            }
            else if (command.equals("help")) {
                return new Reply(getHelp(), false);
            }
            else if (command.equals("quit")) {
                return new Reply("Goodbye.", true);
            }
            else if (!command.equals("status")) {
                return new Reply(
                    "Unknown command. Try help.", false);
            }

            return new Reply(getStatus(), false);
        }
        catch (IllegalArgumentException | IllegalStateException error) {
            return new Reply(
                "Error: " + error.getMessage(), false);
        }
    }

    private int readAmount(String text) {
        try {
            int amount = Integer.parseInt(text);

            if (amount <= 0) {
                throw new NumberFormatException();
            }

            return amount;
        }
        catch (NumberFormatException error) {
            throw new IllegalArgumentException(
                "Enter a positive whole-chip amount.");
        }
    }

    public String getStatus() {
        StringBuilder text = new StringBuilder();

        text.append("State: ").append(model.getState());
        text.append("\nHand: ").append(model.getHandNumber());
        text.append("\nDealer: ").append(model.getDealerName());
        text.append("\nStage: ").append(model.getStage());
        text.append("\nBoard: ").append(model.getCommunityCards());
        text.append("\nPot: ").append(model.getPot());

        for (Player player : model.getPlayers()) {
            text.append("\n").append(player.getName())
                .append(": ").append(player.getChips())
                .append(" chips");

            if (model.getState() == PokerModel.State.PLAYING) {
                text.append(", round bet: ")
                    .append(player.getRoundBet());

                if (player.isFolded()) {
                    text.append(" [folded]");
                }
                else if (player.isActive()
                    && player.getChips() == 0) {

                    text.append(" [all-in]");
                }
                else if (!player.isInHand()) {
                    text.append(" [not dealt in]");
                }
            }
        }

        if (model.getState() == PokerModel.State.PLAYING) {
            Player player = model.getCurrentPlayer();

            if (player != null) {
                text.append("\nNext: ").append(player.getName());

                text.append("\nCurrent bet: ")
                    .append(model.getCurrentBet());

                text.append("\nAmount to call: ")
                    .append(model.getAmountToCall());

                text.append("\nMinimum full bet/raise total: ")
                    .append(model.getMinimumRaiseTo());

                text.append("\nUse hand to see this player's cards.");
            }
            else {
                text.append(
                    "\nNo player is currently required to act.");
            }
        }

        if (model.getState() == PokerModel.State.HAND_FINISHED
            || model.getState() == PokerModel.State.GAME_OVER) {

            if (model.getCommunityCards().size() == 5) {
                for (Player player : model.getPlayers()) {
                    if (player.isActive()) {
                        text.append("\n")
                            .append(player.getName())
                            .append(": ")
                            .append(player.getCards());

                        text.append(" (")
                            .append(model.getHandValue(player).getLabel())
                            .append(")");
                    }
                }
            }

            for (String result : model.getResults()) {
                text.append("\n").append(result);
            }

            if (model.getState() == PokerModel.State.HAND_FINISHED) {
                text.append(
                    "\nUse next to start another hand.");
            }
            else {
                text.append("\nGame over.");
            }
        }

        return text.toString();
    }

    public static String getHelp() {
        return "Commands:\n"
            + "join NAME | start | next | status | hand\n"
            + "check | call | fold | bet AMOUNT\n"
            + "raise TOTAL | allin | help | quit\n"
            + "Starting chips: 100. Blinds: 1/2.\n"
            + "Raise TOTAL means your total bet for this street.";
    }

    public static class Reply {
        private String text;
        private boolean quit;

        public Reply(String text, boolean quit) {
            this.text = text;
            this.quit = quit;
        }

        public String getText() {
            return text;
        }

        public boolean shouldQuit() {
            return quit;
        }
    }
}