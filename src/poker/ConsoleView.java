package poker;

import java.util.Random;
import java.util.Scanner;

public class ConsoleView {
    public static void main(String[] args) {
        PokerModel model = new PokerModel(new Random());
        PokerController controller = new PokerController(model);
        Scanner sc = new Scanner(System.in);

        System.out.println(PokerController.getHelp());

        boolean running = true;

        while (running) {
            System.out.print("> ");

            String command = null;

            if (sc.hasNextLine()) {
                command = sc.nextLine();
            }

            PokerController.Reply reply = controller.execute(command);

            System.out.println(reply.getText());

            if (reply.shouldQuit()) {
                running = false;
            }
        }
    }
}
