package com.harambe;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {

    Stage window;
    Button button;

    public static void main(String[] args) {
        char[][] board = new char[][]{{' ',' ',' ',' ',' ',' ',' '},{' ',' ',' ',' ',' ',' ',' '},{' ',' ',' ',' ',' ',' ',' '},{' ',' ',' ',' ',' ',' ',' '},{' ',' ',' ',' ',' ',' ',' '},{' ',' ',' ',' ',' ',' ',' '}};
        Agent agent = new Agent();
        agent.insertChip(3,'X',board);
        agent.printBoard(board);

        int[] thirdResults = new int[343];
        int[] secondResults = new int[49];
        int[] firstResults = new int[7];
        for (int first = 0; first < 7; first++) {
            agent.insertChip(first,'O',board);
            for (int second = 0; second < 7; second++) {
                agent.insertChip(second,'X',board);
                for (int third = 0; third < 7; third++) {
                    agent.insertChip(third,'O',board);
                    //agent.printBoard(board);
                    int lowest = 9999999;
                    int[] ratings = agent.rateMoveOptions('X',board);
                    System.out.println("\nRESULTS: "+first+second+third);

                    for (int i = 0; i < 7; i++) {
                        if(ratings[i] < lowest){
                            lowest = ratings[i];
                        }
                        thirdResults[third+second*7+first*49]=lowest;
                        System.out.print("\b\b"+ratings[i]);

                    }

                    agent.removeChip(third,board);
                }
                int highest =-1000000;
                for (int i = first*49+second*7; i < first*49+second*7+7; i++) {
                    if (thirdResults[i] > highest){
                        highest = thirdResults[i];
                    }
                }
                secondResults[second+first*7]= highest;
                agent.removeChip(second,board);
            }
            int lowest = 100000000;
            for (int i = first*7; i < first*7+7; i++) {
                if(secondResults[i] < lowest){
                    lowest = secondResults[i];
                }
            }
            firstResults[first] = lowest;
            agent.removeChip(first,board);
        }
        System.out.println("WOW\n\n");
        for (int i = 0; i < 343; i++) {
            System.out.println(thirdResults[i]);
        }

        System.out.println("\n\nwow\n\n");
        for (int i = 0; i < 49; i++) {
            System.out.println(secondResults[i]);
        }

        System.out.println("\n\nwowowowowowowowowowowowowowowo\n\n");
        for (int i = 0; i < 7; i++) {
            System.out.println(firstResults[i]);
        }
        //agent.insertChip(0,'O',board);
        //int[] ratings = agent.rateMoveOptions('X',board);
        //agent.printBoard(board);
        //System.out.println("RESULTS:\n");
        //for (int i = 0; i < 7; i++) {
         //   System.out.println("Column: "+ i+ "\bRating: "+ratings[i]);
        //}

        //System.out.println(agent.getBestMove(ratings));
        //launch(args);
    }


    public void start(Stage stage) throws Exception {
        Pane root = (Pane) FXMLLoader.load(getClass().getClassLoader().getResource("scenes/main.fxml"));
        Scene scene = new Scene(root, 1280, 800);
        stage.setTitle("test");
        stage.setScene(scene);
        stage.show();
    }

}