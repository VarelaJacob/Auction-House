package AuctionHouse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.function.UnaryOperator;

import Agent.Agent;
import Bank.Bank;

import Messaging.MessageInfo;
import javafx.application.Application;
import javafx.application.Platform;
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

    // Boolean value that represents if a bank connection is present.
      private boolean isConnected = false;

    // Create item list to sell at auction.
    public Items items;

    // Maximum possible number of items to sell at auction.
    private int MAXITEMS = 10;

    // Create instances of Bank and Auction House.
    AuctionHouse auctionHouse;
    Bank bank;

    // String of this objects ID.
    String auctionID;

    /*
     * The HashMap key is a port Number.
     * The HashMap value is the ID of the Agent or Auction House.
     */
    private HashMap<Integer, String> agentPort;

    // Create Catalogue of items.
    public ArrayList<Item> currItems = new ArrayList<>();

    // Label to update when status updates are received.
    static Label log = new Label("Currently waiting for user input.");

    // Background colors used for various GUI elements.
    String BACKGROUNDWHITE     = "-fx-background-color: #FFFFFF";
    String BACKGROUNDUNMGRAY = "-fx-background-color: #63666A";
    String BACKGROUNDUNMCHERRY = "-fx-background-color: #BA0C2F";
    String BACKGROUNDUNMSILVER = "-fx-background-color: #A7A8AA";
    String BACKGROUNDUNMTURQUOISE = "-fx-background-color: #007a86";  

    // Create VBox to store the items for sale.
    private VBox vboxItems = new VBox();

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

        Scene scene = new Scene(bankPane, 985, 650);
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

        // Format the items VBox.
        vboxItems.setStyle(BACKGROUNDUNMTURQUOISE);
        vboxItems.setAlignment(Pos.CENTER);

        border.setTop(hboxStatic);
        border.setLeft(vboxButtons);
        border.setCenter(hboxInit);

        connectBtn.setOnMouseClicked( e -> {

            isConnected = false; 
            String bankAddress = "N/A";
            String ip = "N/A";
            String bankP = "N/A";
            String auctionP = "N/A";

            if(bankAddressBox.getText().trim().isEmpty() |
               bankPortBox.getText().trim().isEmpty() |
               auctionPortBox.getText().trim().isEmpty()
            ){

            }  
            else{
                ip = "";
                
                try{
                    ip = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                }

                bankAddress = bankAddressBox.getText();
                int bankPort = Integer.parseInt(bankPortBox.getText());
                int auctionPort = Integer.parseInt(auctionPortBox.getText());

                auctionHouse = 
                    new AuctionHouse(bankAddress, bankPort, auctionPort);
                
                auctionHouse.bankLink.setHostAndPort(bankAddress, bankPort);

                try {
                    auctionHouse.bankLink.outQueue.put(new
                        MessageInfo("create account", "auction_house", ip, auctionPort, 0));
                        isConnected = true;
                        bankP = Integer.toString(bankPort);
                        auctionP = Integer.toString(auctionPort);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            if(isConnected){
            agentPort = new HashMap<>();
            auctionHouse.setIsConnected(true);
            connectBtn.setDisable(true);
            disconnectBtn.setDisable(false);

            bankAddressLabel2.setText(bankAddress);
            bankPortLabel2.setText(bankP);
            auctionLabel2.setText(ip);
            auctionLabel4.setText(auctionP);

            Scanner s = null;

            try {
                s = new Scanner(new File("resources/items.txt"));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }

            items = new Items(s, auctionID);
            populateList();
            updateList();
            
            Thread auctionThread = new Thread(
                new AuctionProxy()
            );
            auctionThread.start();

            Thread time = new Thread(
                new Timer()
            );
            time.start();

            //vboxItems 
            Thread linkThread = new Thread(
                auctionHouse.bankLink
            );
            linkThread.start();}
        });

        return border;
    }

    public void updateList() {
        
        Platform.runLater(() ->{
            vboxItems.getChildren().clear();

            for(Item i : currItems){
                Label temp = new Label(i.toString());
                vboxItems.getChildren().add(temp);
            }
        });
    }
    
    public void populateList(int n) {
        Random rand = new Random();
        if(currItems.size() < n) {
            int limit = MAXITEMS - currItems.size();
            int randInt = ((rand.nextInt(limit)) + 1);

            for(int i = 0; i < randInt; i++) {
                int chose = rand.nextInt(items.items.size());
                Item item = new Item(items.items.get(chose).getName(),
                        (int) items.items.get(chose).getMinBid(), auctionID);
                currItems.add(item);
            }
        }
        auctionHouse.setCurrList(currItems);
    }

    public void populateList() {
        Random rand = new Random();
        if(currItems.size() < 4) {

            for(int i = 0; i < 4; i++) {
                int chose = rand.nextInt(items.items.size());
                currItems.add(items.items.get(chose));
            }
        }
        auctionHouse.setCurrList(currItems);
    }


    private class AuctionProxy implements Runnable {

        @Override
        public void run() {
            try(ServerSocket server = auctionHouse.getServer()){
                Socket s;
                
                while( (s = server.accept()) != null){
                    new Thread( new MessageIn(s, auctionHouse)).start();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private class Timer implements Runnable {

        @Override
        public void run(){
            ArrayList<String> IDs = new ArrayList<>();
            
            while(isConnected) {

                for(Item i : currItems){
                    Instant curr = Instant.now();

                    if(i.getTimeLeft() == 0){
                        IDs.add(i.getItem());
/*                        try {
                            getResult(i);
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }*/
                    }

                    i.updateTime(curr);
                }

                auctionHouse.setCurrList(currItems);

                if(IDs.size() > 0){
                    int n = IDs.size();
                    
                    isConnected = false;
//                    deleteUnsold(IDs);
                    updateList();
                    
                    IDs = new ArrayList<>();

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    populateList(n);
                    updateList();
                    auctionHouse.setCurrList(currItems);
                    isConnected = true;
                }
                
                auctionHouse.setCurrList(currItems);
                
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                updateList();
            }
        }
    }
}
