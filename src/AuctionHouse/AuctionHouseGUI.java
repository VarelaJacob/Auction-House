package AuctionHouse;

import java.util.function.UnaryOperator;

import Agent.Agent;
import Bank.Bank;

import Messaging.MessageInfo;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import Messaging.MessageIn;

public class AuctionHouseGUI extends Application {

    // Background colors used for various GUI elements.
    String BACKGROUNDWHITE     = "-fx-background-color: #FFFFFF";
    String BACKGROUNDUNMGRAY = "-fx-background-color: #63666A";
    String BACKGROUNDUNMCHERRY = "-fx-background-color: #BA0C2F";
    String BACKGROUNDUNMSILVER = "-fx-background-color: #A7A8AA";
    String BACKGROUNDUNMTURQUOISE = "-fx-background-color: #007a86";  

    // Counter to limit the port number to 5 digits.
    public int counter1 = 0;
    public int counter2 = 0;
    
    // Launch the program.
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane bankPane = createBorderPane();

        Scene scene = new Scene(bankPane, 985, 400);
        primaryStage.setTitle("Auction House GUI!"+
                " - [CS-351-004] [Jacob Varela]");
        primaryStage.setScene(scene);
        primaryStage.show();        

    }

    private BorderPane createBorderPane() {

        // Create a new BorderPane.
        BorderPane border = new BorderPane();

        // Creates formatting to only allow integers for the text input field.
        UnaryOperator<TextFormatter.Change> filterPortNum1 = change -> {
            String input = change.getText();

            if( input.matches("[0-9]") && counter1 < 5) {
                counter1++;
                return change;
            }

            return null;
        };

        // Creates formatting to only allow integers for the text input field.
        UnaryOperator<TextFormatter.Change> filterPortNum2 = change -> {
            String input = change.getText();

            if( input.matches("[0-9]") && counter2 < 5) {
                counter2++;
                return change;
            }

            return null;
        };
        TextFormatter<String> portFormatting1 = new TextFormatter<>(filterPortNum1);
        TextFormatter<String> portFormatting2 = new TextFormatter<>(filterPortNum2);

        /* Creates formatting to only allow integers and the period symbol
         * for the text input field to enter an ip address.
         */
        UnaryOperator<TextFormatter.Change> filterAddress = change -> {
            String input = change.getText();

            if( input.matches("[0-9]") ||
                input.matches("[.]"  ) ){
                return change;
            }

            return null;
        };
        TextFormatter<String> addresssFormatting = new TextFormatter<>(filterAddress);
        
        // Create Label to Identify the Bank's IP address.
        Label bankAddressLabel1 = new Label("Bank's IP Address:");
        bankAddressLabel1.setTextFill(Color.web("#FFFFFF"));
        bankAddressLabel1.setFont(Font.font("Arial", 20));

        // Create Label to display the Bank's IP address.
        Label bankAddressLabel2 = new Label("N/A");
        bankAddressLabel2.setTextFill(Color.web("#FFFFFF"));
        bankAddressLabel2.setFont(Font.font("Arial", 20));

        // Create Label to Identify the Bank's port Number.
        Label bankPortLabel1 = new Label("Bank's Port Number:");
        bankPortLabel1.setTextFill(Color.web("#FFFFFF"));
        bankPortLabel1.setFont(Font.font("Arial", 20));

        // Create Label to display the Bank's port Number.
        Label bankPortLabel2 = new Label("N/A");
        bankPortLabel2.setTextFill(Color.web("#FFFFFF"));
        bankPortLabel2.setFont(Font.font("Arial", 20));

        // Create Label to Identify the Auction House's port Number.
        Label auctionLabel1 = new Label("Auction House's\n Port Number:");
        auctionLabel1.setTextFill(Color.web("#FFFFFF"));
        auctionLabel1.setFont(Font.font("Arial", 18));

        // Create Label to display the Auction House's port Number.
        Label auctionLabel2 = new Label("N/A");
        auctionLabel2.setTextFill(Color.web("#FFFFFF"));
        auctionLabel2.setFont(Font.font("Arial", 20));

        // Create Label to Identify the Auction House's current balance.
        Label auctionLabel3 = new Label("Auction House's\n Balance:");
        auctionLabel3.setTextFill(Color.web("#FFFFFF"));
        auctionLabel3.setFont(Font.font("Arial", 18));

        // Create Label to display the Auction House's current balance.
        Label auctionLabel4 = new Label("N/A");
        auctionLabel4.setTextFill(Color.web("#FFFFFF"));
        auctionLabel4.setFont(Font.font("Arial", 20));

        // Create a variable to use the UNM used on the BorderPane.
        ImageView unmLogo = new ImageView(new Image("file:resources/unmLogo.png"));
        unmLogo.setFitHeight(133);
        unmLogo.setFitWidth(171);

        // Create VBox to store the Bank's ip address labels.
        VBox vboxBank1 = new VBox();
        vboxBank1.getChildren().addAll(bankAddressLabel1, bankAddressLabel2);
        vboxBank1.setStyle(BACKGROUNDUNMCHERRY);
        vboxBank1.setAlignment(Pos.CENTER);
        vboxBank1.setMinWidth(200);
        vboxBank1.setSpacing(25);

        // Create VBox to store the Bank's port number labels.
        VBox vboxBank2 = new VBox();
        vboxBank2.getChildren().addAll(bankPortLabel1, bankPortLabel2);
        vboxBank2.setStyle(BACKGROUNDUNMCHERRY);
        vboxBank2.setAlignment(Pos.CENTER);
        vboxBank2.setMinWidth(200);
        vboxBank2.setSpacing(25);

        // Create VBox to store the Auction House's port number labels.
        VBox vboxAuction1 = new VBox();
        vboxAuction1.getChildren().addAll(auctionLabel1, auctionLabel2);
        vboxAuction1.setStyle(BACKGROUNDUNMCHERRY);
        vboxAuction1.setAlignment(Pos.CENTER);
        vboxAuction1.setMinWidth(200);
        vboxAuction1.setSpacing(25);

        // Create VBox to store the Auction House's port number labels.
        VBox vboxAuction2 = new VBox();
        vboxAuction2.getChildren().addAll(auctionLabel3, auctionLabel4);
        vboxAuction2.setStyle(BACKGROUNDUNMCHERRY);
        vboxAuction2.setAlignment(Pos.CENTER);
        vboxAuction2.setMinWidth(200);
        vboxAuction2.setSpacing(25);

        // Create HBox to store the connectivity info and UNM Logo.
        HBox hboxStatic = new HBox();
        hboxStatic.setStyle(BACKGROUNDWHITE);
        hboxStatic.setAlignment(Pos.TOP_LEFT);
        hboxStatic.setMaxHeight(150);
        hboxStatic.setSpacing(5);
        hboxStatic.getChildren().addAll(unmLogo,vboxBank1, vboxBank2,
                                         vboxAuction1, vboxAuction2);

       // Create button that starts a server connection. 
        Button connectBtn = new Button("Start Connection");
        connectBtn.setMaxWidth(125);
        connectBtn.setMinHeight(40);

        // Create button that disconnects from the current server connection. 
        Button disconnectBtn = new Button("End Connection");
        disconnectBtn.setMaxWidth(125);
        disconnectBtn.setMinHeight(40);
        disconnectBtn.setDisable(true);

        // Create VBox to hold start and end server connection buttons.
        VBox vboxButtons = new VBox();
        vboxButtons.getChildren().addAll(connectBtn, disconnectBtn);
        vboxButtons.setStyle(BACKGROUNDUNMGRAY);
        vboxButtons.setAlignment(Pos.CENTER);
        vboxButtons.setMinWidth(176);
        vboxButtons.setSpacing(25);

        // Create Text Field to get the Bank's Port Number
        TextField bankAddressBox = new TextField();
        bankAddressBox.setPrefHeight(50);
        bankAddressBox.setMaxWidth(150);
        bankAddressBox.setFont(Font.font("Arial", 18));
        bankAddressBox.setTextFormatter(addresssFormatting);

        // Create Text Field to get the Bank's Port Number
        TextField bankPortBox = new TextField();
        bankPortBox.setPrefHeight(50);
        bankPortBox.setMaxWidth(150);
        bankPortBox.setFont(Font.font("Arial", 25));
        bankPortBox.setTextFormatter(portFormatting1);

        // Create Text Field to get the Auction House's Port Number
        TextField auctionPortBox = new TextField();
        auctionPortBox.setPrefHeight(50);
        auctionPortBox.setMaxWidth(150);
        auctionPortBox.setFont(Font.font("Arial", 25));
        auctionPortBox.setTextFormatter(portFormatting2);

        // Create Label to identify what to input.
        Label inputLabel1 = new Label("Bank's ip address:");
        inputLabel1.setTextFill(Color.web("#FFFFFF"));
        inputLabel1.setFont(Font.font("Arial", 20));
        inputLabel1.setWrapText(true);

        // Create Label to identify what to input.
        Label inputLabel2 = new Label("Bank's port number:");
        inputLabel2.setTextFill(Color.web("#FFFFFF"));
        inputLabel2.setFont(Font.font("Arial", 20));
        inputLabel2.setWrapText(true);

        // Create Label to identify what to input.
        Label inputLabel3 = new Label("Desired Auction House port number:");
        inputLabel3.setTextFill(Color.web("#FFFFFF"));
        inputLabel3.setFont(Font.font("Arial", 20));
        inputLabel3.setWrapText(true);

        // Create VBox to get information needed to initialize the bank.
        VBox vboxInit1 = new VBox();
        vboxInit1.getChildren().addAll(inputLabel1, bankAddressBox);
        vboxInit1.setStyle(BACKGROUNDUNMTURQUOISE);
        vboxInit1.setAlignment(Pos.CENTER);
        vboxInit1.setMinWidth(250);
        vboxInit1.setSpacing(25);

        // Create VBox to get information needed to initialize the bank.
        VBox vboxInit2 = new VBox();
        vboxInit2.getChildren().addAll(inputLabel2, bankPortBox);
        vboxInit2.setStyle(BACKGROUNDUNMTURQUOISE);
        vboxInit2.setAlignment(Pos.CENTER);
        vboxInit2.setMinWidth(250);
        vboxInit2.setSpacing(25);

        // Create VBox to get information needed to initialize the Auction House.
        VBox vboxInit3 = new VBox();
        vboxInit3.getChildren().addAll(inputLabel3, auctionPortBox);
        vboxInit3.setStyle(BACKGROUNDUNMTURQUOISE);
        vboxInit3.setAlignment(Pos.CENTER);
        vboxInit3.setMinWidth(250);
        vboxInit3.setSpacing(25);

        // Create HBox to hold initialization text labels/fields.
        HBox hboxInit = new HBox();
        hboxInit.setStyle(BACKGROUNDUNMTURQUOISE);
        hboxInit.setAlignment(Pos.CENTER);
        hboxInit.setSpacing(25);
        hboxInit.getChildren().addAll(vboxInit1, vboxInit2, vboxInit3);

        border.setTop(hboxStatic);
        border.setLeft(vboxButtons);
        border.setCenter(hboxInit);

        return border;
    }
}
