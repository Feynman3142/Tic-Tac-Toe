import java.util.*;

public class Main {
    /**
     * Class to start and finish a game of tic-tac-toe
     * Receives inputs to instantiate an object of TicTacToe
     * Game continues with repeated calls to nextTurn() method
     * Game ends once state of game is decided as WIN or DRAW
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("WELCOME TO TIC-TAC-TOE!");
        TicTacToe game = null;
        boolean isAlright;
        do {
            isAlright = false;
            try {
                System.out.print("Enter the size of the square board: ");
                int size = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter the number of players: ");
                int numPlayers = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter the number of consecutive symbols to win: ");
                int numToWin = Integer.parseInt(scanner.nextLine());
                game = TicTacToe.createGame(size, numPlayers, numToWin);
                if (game != null) {
                    isAlright = true;
                    System.out.println("Game created successfully!");
                } else {
                    System.out.println("Returning to start of game creation!\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("You should enter numbers!");
            }
        } while (!isAlright);

        game.displayField();
        Status status;
        do {
            status = game.nextTurn(scanner);
        } while (status.state == State.UNFINISHED);

        switch (status.state) {
            case WIN:
                System.out.printf("\nGAME RESULT: %c wins\n", status.player);
                break;
            case DRAW:
                System.out.println("\nGAME RESULT: Draw");
                break;
            default:
                System.out.println("Error in determining state");
                break;
        }
    }
}