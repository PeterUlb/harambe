package com.harambe.algorithm;

import com.harambe.game.Board;

import java.util.ArrayList;

/**
 * Created by Peter on 06.09.2016.
 *
 * Implementation for the MiniMax algorithm. Requires use of the Board class
 */
public class MiniMax {
    private int savedMove = -1;
    private int globalDepth = 7;

    MiniMax(int depth) {
        this.setGlobalDepth(depth);
    }

    public int getGlobalDepth() {
        return globalDepth;
    }

    public void setGlobalDepth(int globalDepth) {
        this.globalDepth = globalDepth;
    }

    // how many win possibilities each field offers (see picture)
    private static final short[][] winPossibilities =
            {{3, 4, 5, 7, 5, 4, 3},
                    {4, 6, 8, 10, 8, 6, 4},
                    {5, 8, 11, 13, 11, 8, 5},
                    {5, 8, 11, 13, 11, 8, 5},
                    {4, 6, 8, 10, 8, 6, 4},
                    {3, 4, 5, 7, 5, 4, 3}};

    /**
     * @param board 2d char array with char[Board.ROWS][Board.COLUMNS]
     * @return best column to put the next play in (starting at 0 being leftmost column)
     */
    public int getBestMove(Board board) {
        savedMove = -1;
        minimax(board, globalDepth, true);
        if (savedMove == -1) {
            throw new RuntimeException("Woops. Couldn't find a single row to put a coin into :/");
        }
        return savedMove;
    }


    /**
     * Do NOT call directly, use getBestMove
     *
     * @param board              2d char array with char[Board.ROWS][Board.COLUMNS]
     * @param depth              how many node-levels will be search (decreases performance), initial call = globalDepth
     * @param isMaximizingPlayer MiniMax player type (initial call = true)
     * @return
     */
    private int minimax(Board board, int depth, boolean isMaximizingPlayer) {
        // TODO: evt. die ersten male immer in die Mitte
        if (depth == 0 || board.isTerminalState()) {
            return evalValue(board, depth);
        }

        if (isMaximizingPlayer) {
            int bestValue = Integer.MIN_VALUE;
            ArrayList<Integer> moves = board.getPossibleMoves();
            for (int move : moves) {
                board.put(move, Board.PLAYER1);
                int val = minimax(board, depth - 1, false);
//                if(depth == globalDepth) {
//                    System.out.println("Max: " + val);
//                    System.out.println("-------------------------------");
//                }
                board.remove(move);

                //after hours of searching.... only return the move on the TOP level
                if (val > bestValue && depth == globalDepth) {
                    savedMove = move;
                }
                bestValue = Math.max(bestValue, val);
            }

            return bestValue;

        } else {
            int bestValue = Integer.MAX_VALUE;
            ArrayList<Integer> moves = board.getPossibleMoves();
            for (int move : moves) {
                board.put(move, Board.PLAYER2);
                int val = minimax(board, depth - 1, true);
//                if(depth == (globalDepth - 1)) {
//                    System.out.println("Min: " + val);
//                }
                board.remove(move);
                bestValue = Math.min(bestValue, val);
            }

            return bestValue;
        }

    }

    /**
     * Evaluation function for the MiniMax algorithm
     *
     * @param board 2d char array with char[Board.ROWS][Board.COLUMNS]
     * @return value of the checked board for max player (higher = better)
     */
    private int evalValue(Board board, int depth) {
        if (board.checkWin(Board.PLAYER1)) {
            // again hours of debugging... an early win is preferable over late wins
            return 10000 * depth;
        } else if (board.checkWin(Board.PLAYER2)) {
            return -10000 * depth;
        } else {
            char[][] grid = board.getGrid();

            int rating = 0;
            // evaluate the board row for row
            for (int row = 0; row < Board.ROWS; row++)
                for (int col = 0; col < Board.COLUMNS; col++)
                    if (grid[row][col] == Board.PLAYER1) {
                        // win possibilities if field set for us
                        rating += winPossibilities[row][col];
                    } else if (grid[row][col] == Board.PLAYER2) {
                        // win possibilities if field set for enemy
                        rating -= winPossibilities[row][col];
                    }
            return rating;
        }
    }
}
