package Bank;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.UnaryOperator;

import Messaging.MessageIn;
import javafx.application.Application;
import javafx.collections.ListChangeListener.Change;
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

public class BankGUI extends Application {
    
    // Background colors used for various GUI elements.
    String BACKGROUNDWHITE     = "-fx-background-color: #FFFFFF";
    String BACKGROUNDUNMCHERRY = "-fx-background-color: #BA0C2F";
    String BACKGROUNDUNMSILVER = "-fx-background-color: #A7A8AA";
    String BACKGROUNDUNMTURQUOISE = "-fx-background-color: #007a86";    

    // Launch the program.
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane bankPane = createBorderPane();

        Scene scene = new Scene(bankPane, 1600, 900);
        primaryStage.setTitle("Bank GUI! - [CS-351-004] [Jacob Varela]");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();        

    }

    private BorderPane createBorderPane() {

        // Create new BorderPane.
        BorderPane border = new BorderPane();

        // Creates formatting to only allow integers for the text input field.
        UnaryOperator<TextFormatter.Change> filterPortNum = change -> {
            String input = change.getText();

            if( input.matches("[0-9]")) {
                return change;
            }

            return null;
        };
        TextFormatter<String> portFormatting = new TextFormatter<>(filterPortNum);

        // Create Label to Identify the Bank's IP address.
        Label infoLabel1 = new Label("Bank's IP Address:");
        infoLabel1.setTextFill(Color.web("#FFFFFF"));
        infoLabel1.setFont(Font.font("Arial", 20));

        // Create Label to Identify the Bank's Port Number.
        Label infoLabel2 = new Label("Bank's Port Number:");
        infoLabel2.setTextFill(Color.web("#FFFFFF"));
        infoLabel2.setFont(Font.font("Arial", 20));
        
        // Create Label to display the Bank's IP address.
        Label addressLabel = new Label("N/A");
        addressLabel.setTextFill(Color.web("#FFFFFF"));
        addressLabel.setFont(Font.font("Arial", 20));

        // Create Label to display the Bank's Port Number.
        Label portLabel = new Label("N/A");
        portLabel.setTextFill(Color.web("#FFFFFF"));
        portLabel.setFont(Font.font("Arial", 20));

        // Create a variable to use the UNM used on the BorderPane.
        ImageView unmLogo = new ImageView( new Image("file:resources/unmLogo.png"));
        unmLogo.setFitHeight(133);
        unmLogo.setFitWidth(171);
        
        // Create VBox to store info labels.
        VBox vboxAddress = new VBox();
        vboxAddress.getChildren().addAll(infoLabel1, addressLabel);
        vboxAddress.setStyle(BACKGROUNDUNMCHERRY);
        vboxAddress.setAlignment(Pos.CENTER);
        vboxAddress.setMinWidth(300);
        vboxAddress.setSpacing(25);

        // Create VBox to store data labels.
        VBox vboxPort = new VBox();
        vboxPort.getChildren().addAll(infoLabel2, portLabel);
        vboxPort.setStyle(BACKGROUNDUNMCHERRY);
        vboxPort.setAlignment(Pos.CENTER);
        vboxPort.setMinWidth(300);
        vboxPort.setSpacing(25);

        // Create HBox to store the Bank info and UNM Logo.
        HBox hboxStatic = new HBox();
        hboxStatic.setStyle(BACKGROUNDWHITE);
        hboxStatic.setAlignment(Pos.CENTER);
        hboxStatic.setMaxHeight(150);
        hboxStatic.setSpacing(250);
        hboxStatic.getChildren().addAll(vboxAddress, unmLogo, vboxPort);
       
        // Create Label to identify what to input.
        Label questionLabel2 = new Label("Enter the desired Port Number:");
        questionLabel2.setTextFill(Color.web("#FFFFFF"));
        questionLabel2.setFont(Font.font("Arial", 20));

        // Create Text Field to get the Bank's Port Number
        TextField portBox = new TextField();
        portBox.setTextFormatter(portFormatting);

        // Create Button to submit information.
        Button submitBtn = new Button("Create Bank!");
        submitBtn.setFont(Font.font("Arial",20));
        submitBtn.setOnMouseClicked(ev -> {
            serverThread.start();
        });

        // Create VBox to get information needed to initialize the bank.
        VBox vboxInitialize = new VBox();
        vboxInitialize.setStyle(BACKGROUNDUNMTURQUOISE);
        vboxInitialize.setAlignment(Pos.CENTER);
        vboxInitialize.setSpacing(25);
        vboxInitialize.getChildren().addAll( questionLabel2, portBox, submitBtn);

        // Add the final elements to the BorderPane.
        border.setTop(hboxStatic);
        border.setCenter(vboxInitialize);
        
        return border;
    }
}
