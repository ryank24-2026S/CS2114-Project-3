package poker;

import java.util.Locale;

public class PokerController {
    private PokerModel model;

    public PokerController(PokerModel model) {
        if (model == null) {
            throw new IllegalArgumentException("The model can't be null.");
        }

        this.model = model;
    }


    public Reply execute(String input) {
        if (input == null) {
            return new Reply("Goodbye.", true);
        }

        String commandLine = input.trim();

        if (commandLine.isEmpty()) {
            return new Reply("Enter a command. Try help.", false);
        }

        String[] parts = commandLine.split("\\s+", 2);
        String command = parts[0].toLowerCase(Locale.ROOT);
        String argument = "";

        if (parts.length == 2) {
            argument = parts[1].trim();
        }

        try {
            if (!"join".equals(command) && !argument.isEmpty()) {
                throw new IllegalArgumentException(
                    "Unexpected argument.  Try again.");
            }

            switch (command) {
                case "join":
                    model.addPlayer(argument);
                    break;
                case "start":
                    model.startRound();
                    break;
                case "check":
                    model.check(requireCurrentPlayer());
                    break;
                case "fold":
                    model.fold(requireCurrentPlayer());
                    break;
                case "status":
                    break;
                case "help":
                    return new Reply(getHelp(), false);
                case "quit":
                    return new Reply("Goodbye.", true);
                default:
                    return new Reply("Unknown command. Try help.", false);
            }

            return new Reply(getStatus(), false);
        }
        catch (IllegalArgumentException | IllegalStateException e) {
            return new Reply("Error: " + e.getMessage(), false);
        }
    }


    private String requireCurrentPlayer() {
        String player = model.getCurrentPlayer();

        if (player == null) {
            throw new IllegalStateException("No turn is active.");
        }

        return player;
    }


    public String getStatus() {
        StringBuilder text = new StringBuilder();

        text.append("State: ").append(model.getState());
        text.append("\nPlayer: ").append(model.getPlayers());

        if (model.getState() == PokerModel.State.PLAYING) {
            String player = requireCurrentPlayer();

            text.append("\nNext: ").append(player);
            text.append("\nHand: ").append(model.getHand(player));
            text.append("\nQueue: ").append(model.getPendingTurns());
        }
        else if (model.getState() == PokerModel.State.FINISHED) {
            for (String player : model.getActivePlayers()) {
                HandEvaluator.HandValue value = HandEvaluator.evaluate(model
                    .getHand(player));

                text.append("\n").append(player).append(": ");
                text.append(model.getHand(player));
                text.append(" (").append(value.getLabel()).append(")");
            }

            text.append("\nWinner(s): ").append(model.getWinners());
        }

        return text.toString();
    }


    public static String getHelp() {
        return "Command: join NAME | start | check | fold "
            + "| status | help | quit\n"
            + "Prototype: 2-6 players, five cards each, "
            + "one check/fold turn each; no betting or drawing.";
    }
    
    //---------------------------------
    public static class Reply{
        private final String text;
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
