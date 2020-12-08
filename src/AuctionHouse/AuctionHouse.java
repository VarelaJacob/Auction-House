package AuctionHouse;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Bank.Bank;
import Messaging.MessageIn;
import Messaging.MessageOut;
import Messaging.MessageInfo;

public class AuctionHouse {
    
    //
    public ServerSocket server;
    public Socket auctionToBank;

    //
    public MessageOut bankLink;

    //
    private ObjectOutputStream out;

    //
    private String auctionID;

    //
    private boolean isConnected;

    //
    private HashMap<String, MessageIn> agentLink;

    //
    private ArrayList<Item> currList = new ArrayList<>();

    //
    private String myIpAddress;
    
    public AuctionHouse(String ipAddress, int clientPort, int serverPort){

        bankLink = new MessageOut("Bank");
        bankLink.setAuction(this);

        try{
            agentLink = new HashMap<>();
            
            System.out.println("Connecting to the bank.");

            auctionToBank = new Socket(ipAddress, clientPort);
            server = new ServerSocket(serverPort);

            out = new ObjectOutputStream(auctionToBank.getOutputStream());
            System.out.println("Server is running: "+server.toString());

            myIpAddress = auctionToBank.getInetAddress().toString();
        } catch (UnknownHostException |
                 ConnectException |
                 IOException e){
            AuctionHouseGUI.log.setText("Connection attempt unsuccessful.");
        }
    }
}
