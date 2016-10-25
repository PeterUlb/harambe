package com.harambe.algorithm;

import com.harambe.game.Board;
import com.harambe.tools.Logger;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Implementation for the MiniMax algorithm. Requires use of the Board class
 */
public class MiniMax {
    private int tempSavedMove = -1;
    private int globalMaxDepth = -1; // points at the current max depth for the algorithm
    private char optimizingPlayer = Board.UNMARKED;
    private char opponentPlayer = Board.UNMARKED;

    private long maxNano; // max nanoseconds until function returns
    private long start; // marks the start

    /**
     * @param optimizingPlayer the symbol of the player (usually the AI), defined in Board class
     * @param maxMilis maximum miliseconds until function returns, 0 for infinity. <strong>Keep in mind that this doesn't represent the whole runtime of the best move function.
     *                 The real runtime is maxMilis + the timeOutDepth run</strong>
     */
    public MiniMax(char optimizingPlayer, long maxMilis) {
        this.optimizingPlayer = optimizingPlayer;
        this.maxNano = TimeUnit.MILLISECONDS.toNanos(maxMilis);

        if(optimizingPlayer == Board.PLAYER1) {
            opponentPlayer = Board.PLAYER2;
        } else if (optimizingPlayer == Board.PLAYER2) {
            opponentPlayer = Board.PLAYER1;
        } else {
            throw new IllegalArgumentException("Invalid Player");
        }
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
     * @param board Board object with 2d array grid char[Board.ROWS][Board.COLUMNS]
     * @return best column to put the next play in (starting at 0 being leftmost column)
     * -1 indicates a finished game state
     */
    public int getBestMove(Board board) {
        this.start = System.nanoTime();
        tempSavedMove = -1;
        int j = 1; // debug variable
        int trueSavedMove = -1; // temp saved move is changed in the function and can be interrupted on runtime,
        // those we need a variable containing "completed" states
        for (int i = 1; timeLeft() ; i++) {
            try {
                this.globalMaxDepth = i; // set the depth we are currently using
                alphabeta(board.getDeepCopy(), i, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
            } catch (OutOfTimeException e) {
                break;
            }
            // only called after a complete run through
            trueSavedMove = tempSavedMove;
            j = i;
        }
        Logger.debug("Managed to get depth " + j);
        return trueSavedMove;
    }

    private boolean timeLeft() {
        return System.nanoTime() < start + maxNano;
    }


    /**
     * Do NOT call directly, use getBestMove
     *
     * In MiniMax the minplayer (our enemy) tries to minimize our "advantage" / win chance
     * the maxplayer (our AI) tries to maximize our win chances
     *
     * alpha beta cuts of the trees that aren't being chosen from the enemy / us anyways, explained here:
     * http://www.emunix.emich.edu/~evett/AI/AlphaBeta_movie/sld009.htm
     *
     * @param board              2d char array with char[Board.ROWS][Board.COLUMNS]
     * @param depth              how many node-levels will be search (decreases performance)
     * @param alpha              initial call: -infinity; value that max-player (we) will receive as a minimum
     * @param beta               initial call: +infinity; value that min-player (enemy) will receive at max
     * @param isMaximizingPlayer MiniMax player type (initial call = true)
     * @return
     */
    private int alphabeta(Board board, int depth, int alpha, int beta, boolean isMaximizingPlayer) throws OutOfTimeException {
        if (!timeLeft()) {
            throw new OutOfTimeException();
        }
        // TODO: evt. die ersten male immer in die Mitte
        if (depth == 0 || board.isTerminalState()) {
            return evalValue(board, depth);
        }

        if (isMaximizingPlayer) {
            int bestValue = Integer.MIN_VALUE;
            ArrayList<Integer> moves = board.getPossibleMoves();
            while (moves.size() != 0) {
                int move = 0;
                // here we do move ordering (good moves first for early alphabeta cutoff)
                // we start with old "good" moves
                if (moves.contains(tempSavedMove)) {
                    move = tempSavedMove;
                } else if (moves.contains(3)) {
                    move = 3;
                } else {
                    move = moves.get(0);
                }
                board.put(move, optimizingPlayer);
                int val = alphabeta(board, depth - 1, alpha, beta, false);
//                if(depth == globalMaxDepth) {
//                    System.out.println("Max: " + val);
//                    System.out.println("-------------------------------");
//                }
                moves.remove(Integer.valueOf(move));
                board.remove(move);

                //after hours of searching.... only return the move on the TOP level
                if (val > bestValue && depth == globalMaxDepth) {
                    tempSavedMove = move;
                }
                bestValue = Math.max(bestValue, val);
                alpha = Math.max(alpha, bestValue);
                if (beta <= alpha) {
                    // no need to search that node, since min wouldn't choose that one
                    break;
                }
            }

            return bestValue;

        } else {
            int bestValue = Integer.MAX_VALUE;
            ArrayList<Integer> moves = board.getPossibleMoves();
            while (moves.size() != 0) {
                int move = 0;
                // here we do move ordering (good moves first for early alphabeta cutoff)
                // we start with old "good" moves
                if (moves.contains(tempSavedMove)) {
                    move = tempSavedMove;
                } else if (moves.contains(3)) {
                    move = 3;
                } else {
                    move = moves.get(0);
                }
                board.put(move, opponentPlayer);
                int val = alphabeta(board, depth - 1, alpha, beta, true);
//                if(depth == (globalMaxDepth - 1)) {
//                    System.out.println("Min: " + val);
//                }
                moves.remove(Integer.valueOf(move));
                board.remove(move);
                bestValue = Math.min(bestValue, val);
                beta = Math.min(beta, bestValue);
                if (beta <= alpha) {
                    break;
                }
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
        if (board.checkWin(optimizingPlayer)) {
            // again hours of debugging... an early win is preferable over late wins
            return 10000 + (depth * 100);
        } else if (board.checkWin(opponentPlayer)) {
            return -10000 + (depth * -100);
        } else {
            char[][] grid = board.getGrid();

            int rating = 0;
            // evaluate the board row for row
            for (int row = 0; row < Board.ROWS; row++)
                for (int col = 0; col < Board.COLUMNS; col++)
                    if (grid[row][col] == optimizingPlayer) {
                        // win possibilities if field set for us
                        rating += winPossibilities[row][col];
                    } else if (grid[row][col] == opponentPlayer) {
                        // win possibilities if field set for enemy
                        rating -= winPossibilities[row][col];
                    }
            return rating;
        }
    }
}
