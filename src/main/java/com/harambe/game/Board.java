package com.harambe.game;

import java.util.ArrayList;

/**
 * Created by Peter on 06.09.2016.
 * <p>
 * Internal representation of the connect 4 grid as a 2d char array & logic
 */
public class Board {
    public static final int ROWS = 6, COLUMNS = 7;
    public static final char UNMARKED = ' ', PLAYER1 = 'X', PLAYER2 = 'O';

    //char[ROWS][COLUMNS]
    private char[][] grid;
    //holds the first available row
    private final int[] firstAvailableRow;


    public Board() {
        // setup
        grid = new char[ROWS][COLUMNS];
        firstAvailableRow = new int[COLUMNS];
        reset();
    }

    /**
     * Resets the grid with everything marked as UNMARKED
     */
    public void reset() {
        // reset the char array row for row to unmarked
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                grid[row][col] = UNMARKED;
            }
        }

        // set the first available row to array size - 1
        for (int col = 0; col < COLUMNS; col++) {
            firstAvailableRow[col] = ROWS - 1;
        }
    }

    /**
     * @return every possible column to put a coin into (starting at 0)
     */
    public ArrayList<Integer> getPossibleMoves() {
        ArrayList<Integer> moves = new ArrayList<>();

        for (int i = 0; i < firstAvailableRow.length; i++) {
            // value greater or equal 0 means there is still space
            if (firstAvailableRow[i] >= 0) {
                moves.add(i);
            }
        }
        return moves;
    }

    /**
     * Get the 2d array representing the grid
     *
     * @return 2d char array with char[Board.ROWS][Board.COLUMNS]
     */
    public char[][] getGrid() {
        return this.grid;
    }

    public char[][] getDeepCopyGrid() {
        char[][] grid = new char[ROWS][COLUMNS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                grid[row][col] = this.grid[row][col];
            }
        }
        return grid;
    }

    public void display() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                System.out.print("|" + grid[row][col]);
            }
            System.out.println("|");
        }
        System.out.println("---------------");
    }

    /**
     * Puts a coin on the board and in the internal grid representation
     *
     * @param col    Column to put into (leftmost being 0)
     * @param symbol Symbol of the Player
     * @throws IllegalArgumentException putting in not possible
     */
    public void put(int col, char symbol) throws IllegalArgumentException {
        int row = firstAvailableRow[col];
        if (row < 0) {
            throw new IllegalArgumentException("Column " + col + " is already full");
        }
        grid[row][col] = symbol;
        firstAvailableRow[col]--;
    }

    /**
     * Removes a coin from the column on the internal grid representation
     *
     * @param col Column to remove from (leftmost being 0)
     * @throws IllegalArgumentException already empty
     */
    public void remove(int col) throws IllegalArgumentException {
        int row = firstAvailableRow[col];
        if (row >= ROWS - 1) {
            throw new IllegalArgumentException("Column " + col + " is already empty");
        }
        firstAvailableRow[col]++;
        row = firstAvailableRow[col];
        grid[row][col] = UNMARKED;
    }

    /**
     * Checks whether the current board is in a final state (meaning a leaf node)
     * This is true if board if full (draw) or someone wins
     *
     * @return boolean value
     */
    public boolean isTerminalState() {
        // aka, leaf node
        short y = 0;
        for (int i : firstAvailableRow) {
            if (i < 0) {
                y++;
            }
        }

        if (checkWin(PLAYER1) || checkWin(PLAYER2)) {
            return true;
        } else if (y == COLUMNS) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * checks for a win horizontally, vertically and diagonally
     * @param playerSymbol Symbol to check for wins
     * @return win or no win for player
     */
    public boolean checkWin(char playerSymbol) {
        // -
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS - 3; col++) {
                if (grid[row][col] == playerSymbol) {
                    if (grid[row][col + 1] == playerSymbol && grid[row][col + 2] == playerSymbol && grid[row][col + 3] == playerSymbol) {
                        return true;
                    }
                }
            }
        }

        // |
        for (int col = 0; col < COLUMNS; col++) {
            for (int row = 0; row < ROWS - 3; row++) {
                if (grid[row][col] == playerSymbol) {
                    if (grid[row + 1][col] == playerSymbol && grid[row + 2][col] == playerSymbol && grid[row + 3][col] == playerSymbol) {
                        return true;
                    }
                }
            }
        }

        // /
        for (int row = ROWS - 4; row >= 0; row--) {
            for (int col = COLUMNS - 4; col >= 0; col--) {
                int hitCount = 0;
                for (int val = 3; val >= 0; val--) {
                    char symbol = grid[row + val][col - val + 3];
                    if (symbol == playerSymbol) {
                        hitCount++;
                    }
                }
                if (hitCount == 4) {
                    return true;
                }
            }
        }

        // \
        for (int row = ROWS - 4; row >= 0; row--) {
            for (int col = COLUMNS - 4; col >= 0; col--) {
                int hitCount = 0;
                for (int val = 3; val >= 0; val--) {
                    char symbol = grid[row + val][col + val];
                    if (symbol == playerSymbol) {
                        hitCount++;
                    }
                }
                if (hitCount == 4) {
                    return true;
                }
            }
        }

        return false;
    }

    //responds with free columnSpace
    public int[] getFirstAvailableRow() {
        return firstAvailableRow;
    }

    public Boolean isFull(int column) {
        if (getFirstAvailableRow()[column]<0) {
            return true;
        }
        else {
            return false;
        }
    }


}

