package com.harambe;

import com.sun.org.apache.xpath.internal.SourceTree;

/**
 * Created by Robert on 06.09.2016.
 */
public class Agent {

    /* agent turn: steps
    * ------------------
    * receive_event/ receive_file
    * enter_opponent_move
    * make_decision
    * send_event / write_file
    * write_moves_to_database
    * wait for next turn
    */


    /* create char array representing a game board. [game rows][game columns]

    *    columns->    0        1        2       3        4        5        6
    *      5     | [0][0] | [0][1] | [0][2] | [0][3] | [0][4] | [0][5] | [0][6] |
    *      4     | [1][0] | [1][1] | [1][2] | [1][3] | [1][4] | [1][5] | [1][6] |
    *      3     | [2][0] | [2][1] | [2][2] | [2][3] | [2][4] | [2][5] | [2][6] |
    *      2     | [3][0] | [3][1] | [3][2] | [3][3] | [3][4] | [3][5] | [3][6] |
    *      1     | [4][0] | [4][1] | [4][2] | [4][3] | [4][4] | [4][5] | [4][6] |
    *      0     | [5][0] | [5][1] | [5][2] | [5][3] | [5][4] | [5][5] | [5][6] |
    *      ^     ----------------------------------------------------------------
    *     rows
    *
    * the char values represent fields of the game and can have the following values/states:
    *
    *                   'X' => opponent chip in field
    *                   ' '  => field is empty
    *                   'O'  => agent chip in field
    *
    * the winning blocks array represents all possible combinations of fields that can contain 4 matching chips, so if one of these is filled by exclusively one party, this party has won.
    * it contains the values of its fields for each block.
    */



    public Agent() {
        int DEPTH = 1;
    }


    public int[] makeDecision(int depth, char[][]board,char player){

        long possibleOutcomes = 7^depth;
        int resultColumn;
        for (int outcome = 0; outcome < possibleOutcomes; outcome++) {

        }
        return resultColumn;
    }
    public int getBestMove(int[]ratingList){
        int max = ratingList[0];
        int col = 0;

        for (int i = 1; i < ratingList.length; i++) {
            if (ratingList[i] > max) {
                col = i;
                max = ratingList[i];
            }
        }
        return col;
    }
    public void printBoard(char[][]board){
        System.out.println("Game Board\n\n");

        System.out.println("| "+board[0][0]+" | "+board[0][1]+" | "+board[0][2]+" | "+board[0][3]+" | "+board[0][4]+" | "+board[0][5]+" | "+board[0][6]+" |");
        System.out.println("| "+board[1][0]+" | "+board[1][1]+" | "+board[1][2]+" | "+board[1][3]+" | "+board[1][4]+" | "+board[1][5]+" | "+board[1][6]+" |");
        System.out.println("| "+board[2][0]+" | "+board[2][1]+" | "+board[2][2]+" | "+board[2][3]+" | "+board[2][4]+" | "+board[2][5]+" | "+board[2][6]+" |");
        System.out.println("| "+board[3][0]+" | "+board[3][1]+" | "+board[3][2]+" | "+board[3][3]+" | "+board[3][4]+" | "+board[3][5]+" | "+board[3][6]+" |");
        System.out.println("| "+board[4][0]+" | "+board[4][1]+" | "+board[4][2]+" | "+board[4][3]+" | "+board[4][4]+" | "+board[4][5]+" | "+board[4][6]+" |");
        System.out.println("| "+board[5][0]+" | "+board[5][1]+" | "+board[5][2]+" | "+board[5][3]+" | "+board[5][4]+" | "+board[5][5]+" | "+board[5][6]+" |");
        System.out.println("------------------------------");
    }
    public int[] rateMoveOptions(char player, char[][]board){
        //rate possible outcome if you put a chip into a specific column

        //rating list to store column ratings
        int[] rating_list= new int[7];
        for (int i = 0; i < 7; i++) {
            rating_list[i] = simulateMove(i,player,board);
        }
        return rating_list;
    }
    public int simulateMove(int column, char player, char[][]board){
        //returns the score of beneficiousness of making this move
        int move_benefit = rateWinningBlocks(loadWinningBlocks(insertChip(column,player,board)));
        removeChip(column,board);
        return move_benefit;
    }
    public int rateWinningBlocks(char[][]winning_blocks){
        //rate the benefit of each winning block
        
        int total_benefit = 0;
        for (int i = 0; i < 69; i++) {
            int benefit_block = 0;
            char[] block = winning_blocks[i];
            int agentChipCount = countChar('O',block);
            int opponentChipCount = countChar('X',block);
            if(!(agentChipCount == 0)){
                if(opponentChipCount == 0){
                    switch (agentChipCount) {
                        case 1: benefit_block = 10;
                            break;
                        case 2: benefit_block = 100;
                            break;
                        case 3: benefit_block = 10000;
                            break;
                        case 4: benefit_block = 100000000;
                            break;
                    }
                }
            }else{
                switch(opponentChipCount){
                    case 0: break;
                    case 1: benefit_block = -10;
                        break;
                    case 2: benefit_block = -100;
                        break;
                    case 3: benefit_block = -10000;
                        break;
                    case 4: benefit_block = -100000000;
                        break;

                }
            }
        total_benefit += benefit_block;
        }
        return total_benefit;
    }
    public char[][] loadWinningBlocks(char[][]board){
        int index = 0;
        char[][] winning_blocks = new char[69][4];
        //vertical blocks

        //loop through rows
        for (int i = 0; i < 6; i++) {

            winning_blocks[index] = new char[]{board[i][0],board[i][1],board[i][2],board[i][3]};
            index++;
            winning_blocks[index] = new char[]{board[i][1],board[i][2],board[i][3],board[i][4]};
            index++;
            winning_blocks[index] = new char[]{board[i][2],board[i][3],board[i][4],board[i][5]};
            index++;
            winning_blocks[index] = new char[]{board[i][3],board[i][4],board[i][5],board[i][6]};
            index++;
        }
        //horizontal blocks
        //loop through columns
        for (int i = 0; i < 7; i++) {
            winning_blocks[index] = new char[]{board[0][i],board[1][i],board[2][i],board[3][i]};
            index++;
            winning_blocks[index] = new char[]{board[1][i],board[2][i],board[3][i],board[4][i]};
            index++;
            winning_blocks[index] = new char[]{board[2][i],board[3][i],board[4][i],board[5][i]};
            index++;
        }
        //falling diagonal blocks
        //loop through rows
        for (int i = 3; i < 6; i++) {
            winning_blocks[index] = new char[]{board[i][3],board[i-1][2],board[i-2][1],board[i-3][0]};
            index++;
            winning_blocks[index] = new char[]{board[i][4],board[i-1][3],board[i-2][2],board[i-3][1]};
            index++;
            winning_blocks[index] = new char[]{board[i][5],board[i-1][4],board[i-2][3],board[i-3][2]};
            index++;
            winning_blocks[index] = new char[]{board[i][6],board[i-1][5],board[i-2][4],board[i-3][3]};
            index++;
        }
        //ascending diagonal blocks
        for (int i = 3; i < 6; i++) {
            winning_blocks[index] = new char[]{board[i][0],board[i-1][1],board[i-2][2],board[i-3][3]};
            index++;
            winning_blocks[index] = new char[]{board[i][1],board[i-1][2],board[i-2][3],board[i-3][4]};
            index++;
            winning_blocks[index] = new char[]{board[i][2],board[i-1][3],board[i-2][4],board[i-3][5]};
            index++;
            winning_blocks[index] = new char[]{board[i][3],board[i-1][4],board[i-2][5],board[i-3][6]};
            index++;
        }
        return winning_blocks;
    }

    public char[][] removeChip(int column, char[][]board){
        
        int x = 0;
        while(board[x][column] == ' '){
            if(x==5){
                //handle
                System.out.println("ERROR: column was empty.");
                        return board;
            }
            x++;
        }
        board[x][column] = ' ';
        return board;
    }
    public char[][] insertChip(int column, char player, char[][]board){

        if (board[5][column] == ' '){
            board[5][column] = player;
            return board;
        }
        if (board[4][column] == ' '){
            board[4][column] = player;
            return board;
        }
        if (board[3][column] == ' '){
            board[3][column] = player;
            return board;
        }
        if (board[2][column] == ' '){
            board[2][column] = player;
            return board;
        }
        if (board[1][column] == ' '){
            board[1][column] = player;
            return board;
        }
        if (board[0][column] == ' '){
            board[0][column] = player;
            return board;
        }else{
            System.out.println("Column full");
            //handle
            return board;
        }
    }
    static int countChar(char c, char[] array) {
        int count = 0;
        for (char x : array) {
            if (x == c) {
                count++;
            }
        }
        return count;
    }
    static boolean contains(char c, char[] array) {
        for (char x : array) {
            if (x == c) {
                return true;
            }
        }
        return false;
    }

}
