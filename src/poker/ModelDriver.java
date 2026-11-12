package poker;

import java.util.Random;

public class ModelDriver {
    public static void main(String[] args) {
        PokerModel game = new PokerModel(new Random(42));

        game.addPlayer("Greta Thunberg");
        game.addPlayer("Yo");
        game.addPlayer("Gurt");

        game.startRound();

        while (game.getState() == PokerModel.State.PLAYING) {

            Player player = game.getCurrentPlayer();

            System.out.println("\nStage: " + game.getStage());

            System.out.println("Board: " + game.getCommunityCards());

            System.out.println(player.getName() + " has " + player.getCards());

            if (game.getAmountToCall() > 0) {
                System.out.println(player.getName() + " calls.");

                game.call();
            }
            else {
                System.out.println(player.getName() + " checks.");

                game.check();
            }
        }

        System.out.println("\nFinal board: " + game.getCommunityCards());

        for (String result : game.getResults()) {
            System.out.println(result);
        }

        System.out.println("\nChip stacks:");

        for (Player player : game.getPlayers()) {
            System.out.println(player.getName() + ": " + player.getChips());
        }
    }
}
