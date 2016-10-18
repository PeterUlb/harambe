package com.harambe.algorithm;

import com.harambe.game.Board;
import com.harambe.game.SessionVars;
import junit.framework.TestCase;

public class MiniMaxTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();
        SessionVars.timeoutThresholdInMillis = 2000;
    }


    public void testSimpleLossPrevention() throws Exception {
        Board board = new Board();
        board.put(Board.COLUMNS - 1, Board.PLAYER2);
        board.put(Board.COLUMNS - 2, Board.PLAYER2);
        board.put(Board.COLUMNS - 3, Board.PLAYER2);

        assertEquals(Board.COLUMNS - 4, new MiniMax(Board.PLAYER1, SessionVars.timeoutThresholdInMillis).getBestMove(board));
    }


    public void testSimpleWin() throws Exception {
        Board board = new Board();
        board.put(Board.COLUMNS - 1, Board.PLAYER1);
        board.put(Board.COLUMNS - 1, Board.PLAYER1);
        board.put(Board.COLUMNS - 1, Board.PLAYER1);

        assertEquals(Board.COLUMNS - 1, new MiniMax(Board.PLAYER1, SessionVars.timeoutThresholdInMillis).getBestMove(board));
    }

    public void testWinDiagonally() throws Exception {
        Board board = new Board();
        board.put(0, Board.PLAYER1);
        board.put(1, Board.PLAYER2);
        board.put(1, Board.PLAYER1);
        board.put(2, Board.PLAYER2);
        board.put(2, Board.PLAYER1);
        board.put(1, Board.PLAYER2);
        board.put(3, Board.PLAYER1);
        board.put(3, Board.PLAYER2);
        board.put(2, Board.PLAYER1);
        board.put(3, Board.PLAYER2);



        assertEquals(3, new MiniMax(Board.PLAYER1, SessionVars.timeoutThresholdInMillis).getBestMove(board));
    }

    public void testMiddleFirst() throws Exception {
        // first turn middle is the best turn to make in 6x7
        Board board = new Board();

        assertEquals(3, new MiniMax(Board.PLAYER1, SessionVars.timeoutThresholdInMillis).getBestMove(board));
    }


}