package com.harambe.game;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Internal representation of the connect 4 grid as a 2d char array and logic
 */
public class Board {
    public static final int ROWS = 6, COLUMNS = 7;
    public static final char UNMARKED = '-', PLAYER1 = 'O', PLAYER2 = 'X';


    //char[ROWS][COLUMNS]
    private char[][] grid;
    //holds the first available row
    private int[] firstAvailableRow;


    public Board() {
        // setup
        grid = new char[ROWS][COLUMNS];
        firstAvailableRow = new int[COLUMNS];
        reset();
    }

    public Board getDeepCopy() {
        Board returnBoard = new Board();
        returnBoard.grid = this.getDeepCopyGrid();
        returnBoard.firstAvailableRow = Arrays.copyOf(firstAvailableRow, firstAvailableRow.length);

        return returnBoard;
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

        return checkWin(PLAYER1) || checkWin(PLAYER2) || y == COLUMNS;
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


    /**
     * Win location for the UI
     * @param playerSymbol the playerSymbol (Board.PlayerX)
     * @return 2d array of 4-fields (4x 2 integers in form row, column)
     */
    public int[][] getWinForUI(char playerSymbol) {
        // -
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS - 3; col++) {
                if (grid[row][col] == playerSymbol) {
                    if (grid[row][col + 1] == playerSymbol && grid[row][col + 2] == playerSymbol && grid[row][col + 3] == playerSymbol) {
                        return new int[][] { {row, col}, {row,col + 1}, {row, col + 2}, {row, col + 3}};
                    }
                }
            }
        }

        // |
        for (int col = 0; col < COLUMNS; col++) {
            for (int row = 0; row < ROWS - 3; row++) {
                if (grid[row][col] == playerSymbol) {
                    if (grid[row + 1][col] == playerSymbol && grid[row + 2][col] == playerSymbol && grid[row + 3][col] == playerSymbol) {
                        return new int[][] { {row, col}, {row + 1, col}, {row + 2, col}, {row + 3, col}};
                    }
                }
            }
        }

        // /
        for (int row = ROWS - 4; row >= 0; row--) {
            for (int col = COLUMNS - 4; col >= 0; col--) {
                int hitCount = 0;
                int[][] returnValue = new int[4][2];
                for (int val = 3; val >= 0; val--) {
                    char symbol = grid[row + val][col - val + 3];
                    if (symbol == playerSymbol) {
                        returnValue[val][0] = row + val;
                        returnValue[val][1] = col - val + 3;
                        hitCount++;
                    }
                }
                if (hitCount == 4) {
                    return returnValue;
                }
            }
        }

        // \
        for (int row = ROWS - 4; row >= 0; row--) {
            for (int col = COLUMNS - 4; col >= 0; col--) {
                int hitCount = 0;
                int[][] returnValue = new int[4][2];
                for (int val = 3; val >= 0; val--) {
                    char symbol = grid[row + val][col + val];
                    if (symbol == playerSymbol) {
                        returnValue[val][0] = row + val;
                        returnValue[val][1] = col + val;
                        hitCount++;
                    }
                }
                if (hitCount == 4) {
                    return returnValue;
                }
            }
        }

        return null;
    }

    //responds with free columnSpace
    public int[] getFirstAvailableRow() {
        return firstAvailableRow;
    }

    public boolean isFull(int column) {
        return getFirstAvailableRow()[column] < 0;
    }


}

