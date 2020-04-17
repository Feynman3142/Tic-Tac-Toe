import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

enum State {
    /**
     * Enum to determine the state of the game
     * WIN -> one of the players has won the game and it is hence finished
     * DRAW -> the game has finished and neither of the players have won
     * UNFINISHED -> the game is still progressing
     */
    WIN,
    DRAW,
    UNFINISHED
}

class Status {
    /**
     * Class to store the state of the game as described by enum State
     * Extra field to store which player won the game, if at all
     */
    State state;
    char player;

    Status(State state, char player) {
        this.state = state;
        this.player = player;
    }
}

class Track {
    /**
     * Class to keep track of which player is winning the game
     * 'count' stores how many times the variable 'lastSymbol' has appeared consecutively
     * Based on 'count', we can determine if a player has won or not
     */
    char lastSymbol;
    int count;

    Track(char lastSymbol, int count) {
        this.lastSymbol = lastSymbol;
        this.count = count;
    }
}

class TicTacToe {
    /**
     * Main class that starts a game of tic-tac-toe,
     * processes the state of the board after every turn
     * to determine the state of the game
     */

    private char[][] field; // board displayed to the players
    private int size; // board is square and 'size' determines the dimensions
    private char[] symbols; // array of symbols for each character, assigned during instantiation
    private int turn; // variable to determine which player's turn is it
    private int numToWin; // how many consecutive symbols of a player to win (eg: this would be 3 in a standard game)
    private Status status; // to store the status and symbol of the winning player, if at all
    private Map<Integer, Track> rowTrackMap; // map to track if players made a winning pattern along a row
    private Map<Integer, Track> colTrackMap; // map to track if players made a winning pattern along a column
    private Map<Integer, Track> leftRightDiagTrackMap; // map to track if players made a winning pattern along a diagonal from L-R
    private Map<Integer, Track> rightLeftDiagTrackMap; // map to track if players made a winning pattern along a diagonal from R-L

    private static char[][] createField(int size) { // creates the board and initializes all positions to '_'
        char [][] field = new char[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                field[i][j] = '_';
            }
        }
        return field;
    }

    private static char[] createSymbols(int numPlayers) { // assigns symbols to players based on number of players
        if (numPlayers == 2) { // uses common 'X' and 'O' symbols if there are 2 players
            return new char[]{'X', 'O'};
        } else {
            char symbol = '0'; // uses characters '0', '1', ..., '9' for up to 10 players
            char[] symbols = new char[numPlayers];
            for (int i = 0; i < numPlayers; ++i) {
                symbols[i] = symbol;
                ++symbol;
            }
            return symbols;
        }
    }

    private TicTacToe(char[][] field, int size, int numPlayers, int numToWin) {
        this.field = field;
        this.size = size;
        this.numToWin = numToWin;
        turn = 0;
        symbols = createSymbols(numPlayers);
        status = new Status(State.UNFINISHED, '_');
        rowTrackMap = new LinkedHashMap<>();
        colTrackMap = new LinkedHashMap<>();
        leftRightDiagTrackMap = new LinkedHashMap<>();
        rightLeftDiagTrackMap = new LinkedHashMap<>();
    }

    private static boolean checkParams(int size, int numPlayers, int numToWin) { // ensures values sent are valid
        boolean isAlright = false;
        if (size < 2) {
            System.out.printf("Cannot have a grid of size (given: %dx%d) less than 2x2!\n", size, size);
        } else if (numPlayers < 2 || numPlayers > 10) {
            System.out.printf("Cannot have less than 2 or more than 10 (given: %d) players!\n", numPlayers);
        } else if (numToWin < 2) {
            System.out.printf("Cannot have less than 2 (given: %d) consecutive symbols to win!\n", numToWin);
        } else if (numPlayers > size) {
            System.out.printf("Cannot have %d players for a %dx%d field!\n", numPlayers, size, size);
        } else if (numToWin > size) {
            System.out.printf("Cannot have %d consecutive symbols to win in a %dx%d grid!\n", numToWin, size, size);
        } else {
            isAlright = true;
        }
        return isAlright;
    }

    static TicTacToe createGame(int size, int numPlayers, int numToWin) { // static method to create an instance of TicTacToe
        if (checkParams(size, numPlayers, numToWin)) {
            return new TicTacToe(createField(size), size, numPlayers, numToWin);
        } else {
            return null;
        }
    }

    Status nextTurn(Scanner scanner) { // method to handle input from player for the next turn and update the board
        boolean isAlright = false; // turns true only if player enters valid coordinates to an unoccupied position
        do {
            System.out.printf("[PLAYER TURN: %c] Enter the coordinates separated by spaces: ", symbols[turn]);
            try {
                int[] coords = Arrays.stream(scanner.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
                int x = coords[0]; // uses Cartesian coordinates for player to place his symbol on board
                int y = coords[1];
                if (!(x >= 1 && x <= size && y >= 1 && y <= size)){
                    System.out.printf("Coordinates should be from 1 to %d\n", size);
                } else if (field[size - y][x - 1] != '_') { // maps Cartesian coordinates to valid array indices
                    System.out.println("This cell is occupied! Choose another one!");
                } else {
                    field[size - y][x - 1] = symbols[turn];
                    ++turn;
                    if (turn == symbols.length) { // cycles back to first player after first round is done
                        turn = 0;
                    }
                    displayField();
                    isAlright = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("You should enter numbers!");
            }
        } while (!isAlright);
        return displayStatus();
    }

    void displayField() { // helper method to display board
        System.out.println("---------");
        for (int i = 0; i < size; ++i) {
            System.out.print("| ");
            for (int j = 0; j < size; ++j) {
                System.out.printf("%c ", field[i][j]);
            }
            System.out.println("|");
        }
        System.out.println("---------");
    }

    private static void fillTrackMap(Map<Integer, Track> trackMap, int lowLim, int highLim) {
        // method to initialise the maps for tracking status of board
        for (int i = lowLim; i <= highLim; ++i) {
            trackMap.put(i, new Track('_', 0));
        }
    }

    private boolean updateTrackMap(Map<Integer, Track> trackMap, char symbol, int key) {
        // method to update the maps based on the latest symbol sent by method 'nextTurn()'
        boolean isWin = false;
        Track track = trackMap.get(key);
        if (symbol == track.lastSymbol) {
            ++track.count; // update the count if a symbol is appearing consecutively
            if (track.count == numToWin) {
                isWin = true;
            }
        } else {
            track.lastSymbol = symbol;
            track.count = 1;
        }
        trackMap.put(key, track); // update the map
        return isWin;
    }

    private Status displayStatus() { // method to track the status of the board after every turn

        boolean isFinished = true;
        boolean isWon = false;
        char winningPlayer = '_';

        /*
           method loops through every position on board after
           every turn so we reinitialise the maps and clear
           changes made to them after the last call to displayStatus()
         */
        fillTrackMap(rowTrackMap, 0, size - 1);
        fillTrackMap(colTrackMap, 0, size - 1);
        fillTrackMap(leftRightDiagTrackMap, -(size - 1), size - 1);
        fillTrackMap(rightLeftDiagTrackMap, 0, 2 * (size - 1));

        for (int i = 0; i < size && !isWon; ++i) {
            for (int j = 0; j < size && !isWon; ++j) {
                char symbol = field[i][j];
                if (symbol == '_') {
                    // the game doesn't finish as long as there is an unoccupied position and no one has won
                    isFinished = false;
                } else {
                    boolean rowWin = updateTrackMap(rowTrackMap, symbol, i);
                    boolean colWin = updateTrackMap(colTrackMap, symbol, j);
                    boolean lrdWin = updateTrackMap(leftRightDiagTrackMap, symbol, j - i);
                    boolean rldWin = updateTrackMap(rightLeftDiagTrackMap, symbol, j + i);
                    isWon = rowWin || colWin || lrdWin || rldWin;
                    if (isWon) {
                        winningPlayer = symbol;
                        isFinished = true;
                    }
                }
            }
        }

        // call the game a draw if the game has finished and no one has won
        boolean isDraw = isFinished && !isWon;

        if (isWon) {
            status.state = State.WIN;
            status.player = winningPlayer;
        } else if (isDraw) {
            status.state = State.DRAW;
        } else {
            status.state = State.UNFINISHED;
        }

        return status;
    }
}
