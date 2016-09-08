package com.harambe.game;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Peter on 08.09.2016.
 */
public class BoardTest extends TestCase {

    Board board;

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testReset() throws Exception {
        board = new Board();
        char[][] emptyGrid = board.getDeepCopyGrid();
        char[][] grid = board.getGrid();
        board.put(0, Board.PLAYER1);
        board.put(1, Board.PLAYER2);
        board.put(0, Board.PLAYER1);
        board.put(4, Board.PLAYER2);
        board.put(6, Board.PLAYER1);
        board.put(5, Board.PLAYER2);
        board.put(2, Board.PLAYER1);
        board.put(3, Board.PLAYER2);
        board.reset();
        assertEquals(true, Arrays.deepEquals(emptyGrid, grid));
    }

    public void testGetPossibleMoves() throws Exception {
        board = new Board();
        ArrayList<Integer> moves = board.getPossibleMoves();
        assertEquals("Found too many initial moves", Board.COLUMNS, moves.size());

        for (int i = 0; i < Board.COLUMNS; i++) {
            if (moves.get(i) != i) {
                Assert.fail("Getting wrong initial possible moves");
            }
        }

        //checking full detection
        board.put(0, Board.PLAYER1);
        board.put(0, Board.PLAYER1);
        board.put(0, Board.PLAYER1);
        board.put(0, Board.PLAYER1);
        board.put(0, Board.PLAYER1);
        board.put(0, Board.PLAYER1);

        moves = board.getPossibleMoves();

        assertEquals("Did not detect full column", Board.COLUMNS - 1, moves.size());
    }

    public void testGetGrid() throws Exception {
        board = new Board();
        char[][] grid = board.getGrid();
        assertNotNull("returned null", grid);
        board.put(0, Board.PLAYER1);
        assertEquals(Board.PLAYER1, grid[Board.ROWS - 1][0]);
    }

    public void testGetDeepCopyGrid() throws Exception {
        board = new Board();
        char[][] grid = board.getDeepCopyGrid();
        assertNotNull("returned null", grid);
        board.put(0, Board.PLAYER1);
        char[][] currentGrid = board.getGrid();

        assertFalse("not a deep copy", Arrays.deepEquals(grid, currentGrid));

    }

    public void testPut() throws Exception {
        board = new Board();

        //simulate puts
        board.put(0, Board.PLAYER1);
        char[][] grid = board.getGrid();
        assertEquals(Board.PLAYER1, grid[Board.ROWS - 1][0]);
        board.put(Board.COLUMNS - 1, Board.PLAYER2);
        assertEquals(Board.PLAYER2, grid[Board.ROWS - 1][Board.COLUMNS - 1]);

        board.reset();

        //simulate board full
        boolean occured = false;
        try {
            for (int i = 0; i < Board.ROWS + 1; i++) {
                board.put(0, Board.PLAYER1);
            }

        } catch (IllegalArgumentException ex) {
            occured = true;
        }
        assertTrue("Exception for full column not thrown", occured);
    }

    public void testRemove() throws Exception {
        board = new Board();
        //simulate nothing to remove
        boolean occured = false;
        try {
            board.remove(0);
        } catch (IllegalArgumentException ex) {
            occured = true;
        }
        assertTrue("Exception for removing from empty column not thrown", occured);

        //simulate remove
        board.put(0, Board.PLAYER1);
        board.remove(0);
        assertEquals("Removing from column 0 failed", Board.UNMARKED, board.getGrid()[0][Board.ROWS - 1]);


    }

    public void testIsTerminalState() throws Exception {
        board = new Board();
        // [-] win condition check
        board.put(0, Board.PLAYER1);
        board.put(1, Board.PLAYER1);
        board.put(2, Board.PLAYER1);
        assertFalse("False [-] detected", board.isTerminalState());
        board.put(3, Board.PLAYER1);
        assertTrue("Correct [-] not detected", board.isTerminalState());
        board.reset();

        // [|] win condition check
        board.put(3, Board.PLAYER1);
        board.put(3, Board.PLAYER1);
        board.put(3, Board.PLAYER1);
        assertFalse("False [|] detected", board.isTerminalState());
        board.put(3, Board.PLAYER1);
        assertTrue("Correct [|] not detected", board.isTerminalState());
        board.reset();

        // [/] win condition check
        board.put(0, Board.PLAYER1);
        board.put(1, Board.PLAYER1);
        board.put(1, Board.PLAYER1);
        board.put(2, Board.PLAYER1);
        board.put(2, Board.PLAYER1);
        board.put(2, Board.PLAYER1);
        board.put(3, Board.PLAYER2);
        board.put(3, Board.PLAYER1);
        board.put(3, Board.PLAYER2);
        assertFalse("False [|] detected", board.isTerminalState());
        board.put(3, Board.PLAYER1);
        assertTrue("Correct [|] not detected", board.isTerminalState());
        board.reset();

        // [\] win condition check
        board.put(6, Board.PLAYER2);
        board.put(5, Board.PLAYER2);
        board.put(5, Board.PLAYER2);
        board.put(4, Board.PLAYER2);
        board.put(4, Board.PLAYER2);
        board.put(4, Board.PLAYER2);
        board.put(3, Board.PLAYER1);
        board.put(3, Board.PLAYER2);
        board.put(3, Board.PLAYER1);
        assertFalse("False [|] detected", board.isTerminalState());
        board.put(3, Board.PLAYER2);
        assertTrue("Correct [|] not detected", board.isTerminalState());
        board.reset();

        // test full board final state check
        char[][] grid = board.getGrid();
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLUMNS; col++) {
                grid[row][col] = Board.PLAYER1;
            }
        }
        assertTrue("Full board not detected", board.isTerminalState());
        board.reset();

        // test empty board
        assertFalse("Empty board as terminal detected", board.isTerminalState());
    }

    public void testCheckWin() throws Exception {
        board = new Board();
        // [-] win condition check
        board.put(0, Board.PLAYER1);
        board.put(1, Board.PLAYER1);
        board.put(2, Board.PLAYER1);
        assertFalse("False [-] detected", board.checkWin(Board.PLAYER1));
        assertFalse("False [-] detected", board.checkWin(Board.PLAYER2));
        board.put(3, Board.PLAYER1);
        assertTrue("Correct [-] not detected", board.checkWin(Board.PLAYER1));
        assertFalse("False [-] detected", board.checkWin(Board.PLAYER2));
        board.reset();

        // [|] win condition check
        board.put(3, Board.PLAYER1);
        board.put(3, Board.PLAYER1);
        board.put(3, Board.PLAYER1);
        assertFalse("False [|] detected", board.checkWin(Board.PLAYER1));
        assertFalse("False [|] detected", board.checkWin(Board.PLAYER2));
        board.put(3, Board.PLAYER1);
        assertTrue("Correct [|] not detected", board.checkWin(Board.PLAYER1));
        assertFalse("False [|] detected", board.checkWin(Board.PLAYER2));
        board.reset();

        // [/] win condition check
        board.put(0, Board.PLAYER1);
        board.put(1, Board.PLAYER1);
        board.put(1, Board.PLAYER1);
        board.put(2, Board.PLAYER1);
        board.put(2, Board.PLAYER1);
        board.put(2, Board.PLAYER1);
        board.put(3, Board.PLAYER2);
        board.put(3, Board.PLAYER1);
        board.put(3, Board.PLAYER2);
        assertFalse("False [|] detected", board.checkWin(Board.PLAYER1));
        assertFalse("False [|] detected", board.checkWin(Board.PLAYER2));
        board.put(3, Board.PLAYER1);
        assertTrue("Correct [|] not detected", board.checkWin(Board.PLAYER1));
        assertFalse("False [|] detected", board.checkWin(Board.PLAYER2));
        board.reset();

        // [\] win condition check
        board.put(6, Board.PLAYER2);
        board.put(5, Board.PLAYER2);
        board.put(5, Board.PLAYER2);
        board.put(4, Board.PLAYER2);
        board.put(4, Board.PLAYER2);
        board.put(4, Board.PLAYER2);
        board.put(3, Board.PLAYER1);
        board.put(3, Board.PLAYER2);
        board.put(3, Board.PLAYER1);
        assertFalse("False [|] detected", board.checkWin(Board.PLAYER1));
        assertFalse("False [|] detected", board.checkWin(Board.PLAYER2));
        board.put(3, Board.PLAYER2);
        assertTrue("Correct [|] not detected", board.checkWin(Board.PLAYER2));
        assertFalse("False [|] detected", board.checkWin(Board.PLAYER1));
    }

}