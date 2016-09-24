package com.harambe.algorithm;

import com.harambe.game.Board;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by Peter on 06.09.2016.
 *
 * Implementation for the MiniMax algorithm. Requires use of the Board class
 */
public class MiniMax {
    private int savedMove = -1;
    private int globalDepth = 7;
    private char optimizingPlayer = Board.UNMARKED;
    private char opponentPlayer = Board.UNMARKED;

    private long maxNano; // max nanoseconds until function returns
    private long start; // marks the start
    private int outOfTimeDepth = 4;

    /**
     * @param depth how many iterations of the minimax algorithm will be performed
     * @param optimizingPlayer the symbol of the player (usually the AI), defined in Board class
     * @param maxMilis maximum miliseconds until function returns, 0 for infinity. <strong>Keep in mind that this doesn't represent the whole runtime of the best move function.
     *                 The real runtime is maxMilis + the timeOutDepth run</strong>
     * @param outOfTimeDepth the depth that will be performed when time > maxMilis, choose something fast!
     */
    public MiniMax(int depth, char optimizingPlayer, long maxMilis, int outOfTimeDepth) {
        this.setGlobalDepth(depth);
        this.optimizingPlayer = optimizingPlayer;
        this.maxNano = TimeUnit.MILLISECONDS.toNanos(maxMilis);
        this.outOfTimeDepth = outOfTimeDepth;

        if(optimizingPlayer == Board.PLAYER1) {
            opponentPlayer = Board.PLAYER2;
        } else if (optimizingPlayer == Board.PLAYER2) {
            opponentPlayer = Board.PLAYER1;
        } else {
            throw new IllegalArgumentException("Invalid Player");
        }
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
     * @param board Board object with 2d array grid char[Board.ROWS][Board.COLUMNS]
     * @return best column to put the next play in (starting at 0 being leftmost column)
     * -1 indicates a finished game state
     */
    public int getBestMove(Board board) {
        this.start = System.nanoTime();
        savedMove = -1;
        try {
            alphabeta(board.getDeepCopy(), globalDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        } catch (OutOfTimeException e) {
            System.out.println("Out of time");
            savedMove = getOutOfTimeMove(board);
        }
        return savedMove;
    }

    /**
     * Called when the main minimax runs out of time. The depth is defined in the MiniMax Constructor
     * @param board the current board
     * @return the best move or random
     */
    private int getOutOfTimeMove(Board board) {
        savedMove = -1;
        savedMove = new MiniMax(outOfTimeDepth, optimizingPlayer, 0, 0).getBestMove(board.getDeepCopy());
        if (savedMove == -1) {
            savedMove = ThreadLocalRandom.current().nextInt(0, Board.COLUMNS);
        }
        return savedMove;
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
     * @param depth              how many node-levels will be search (decreases performance), initial call = globalDepth
     * @param alpha              initial call: -infinity; value that max-player (we) will receive as a minimum
     * @param beta               initial call: +infinity; value that min-player (enemy) will receive at max
     * @param isMaximizingPlayer MiniMax player type (initial call = true)
     * @return
     */
    private int alphabeta(Board board, int depth, int alpha, int beta, boolean isMaximizingPlayer) throws OutOfTimeException {
        if(maxNano != 0 && System.nanoTime() > start + maxNano) {
            // we do care for the time (!= 0) and the time ran out
            throw new OutOfTimeException();
        }

        // TODO: evt. die ersten male immer in die Mitte
        if (depth == 0 || board.isTerminalState()) {
            return evalValue(board, depth);
        }

        if (isMaximizingPlayer) {
            int bestValue = Integer.MIN_VALUE;
            ArrayList<Integer> moves = board.getPossibleMoves();
            for (int move : moves) {
                board.put(move, optimizingPlayer);
                int val = alphabeta(board, depth - 1, alpha, beta, false);
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
            for (int move : moves) {
                board.put(move, opponentPlayer);
                int val = alphabeta(board, depth - 1, alpha, beta, true);
//                if(depth == (globalDepth - 1)) {
//                    System.out.println("Min: " + val);
//                }
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
