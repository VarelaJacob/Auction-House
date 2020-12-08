package AuctionHouse;

import Agent.Agent;
import Bank.Bank;

import Messaging.MessageInfo;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import Messaging.MessageIn;

public class AuctionHouseGUI {

    // Background colors used for various GUI elements.
    String BACKGROUNDWHITE     = "-fx-background-color: #FFFFFF";
    String BACKGROUNDUNMCHERRY = "-fx-background-color: #BA0C2F";
    String BACKGROUNDUNMSILVER = "-fx-background-color: #A7A8AA";
    String BACKGROUNDUNMTURQUOISE = "-fx-background-color: #007a86";  

    //
    public static Text log = new Text("Waiting for user interaction");
    
    // Launch the program.
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane bankPane = createBorderPane();

        Scene scene = new Scene(bankPane, 1600, 900);
        primaryStage.setTitle("Auction House GUI!"+
                " - [CS-351-004] [Jacob Varela]");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();        

    }
}
