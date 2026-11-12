package poker;

import java.util.Random;

public class ModelDriver {
    public static void main(String[] args) {
        PokerModel game = new PokerModel(new Random(42));
        
        game.addPlayer("Ada");
        game.addPlayer("Grace");
        game.startRound();
        
        while(game.getState() == PokerModel.State.PLAYING) {
            String player = game.getCurrentPlayer();
            
            System.out.println(player + "checks with " + game.getHand(player));
            
            game.check(player);
        }
        
        System.out.println("Winner(s): " + game.getWinners());
    }

}
