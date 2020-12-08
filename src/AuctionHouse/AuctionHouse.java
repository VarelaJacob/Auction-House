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
        } catch (IOException e){
            AuctionHouseGUI.log.setText("Connection attempt unsuccessful.");
        }
        this.isConnected = true;
    }

    public synchronized String handleMessage(
        MessageInfo newMessage, Socket socket,
        MessageIn connectionHandler)
        throws IOException, InterruptedException{
        
        String source = newMessage.source.toLowerCase();

        String returnMessage = "Invalid keyword, try again.";

        switch (source) {
            case "agent":

                if(newMessage.message.equals("catalogue")){
                    returnMessage = getCatalogue(currList);
                    return "\n" + returnMessage;
                }
                else if(newMessage.message.contains("bid")) {
                    List<String> messageValues = extractValues(
                        newMessage.message);

                        if(messageValues.size() == 3) {
                        String itemID = messageValues.get(0);
                        String userID = messageValues.get(1);
                        int amount = Integer.parseInt(messageValues.get(2));
                        getCatalogue(currList);
                        
                        if(bid(itemID, userID, amount)){
                            String transferMsg = "block funds " + userID + " "
                                        + amount;
                                bankLink.outQueue.put
                                        (new MessageInfo(transferMsg,"auction", 
                                                null, 0, 0));
                                returnMessage = "Bid $" + amount + " on item " +
                                        itemID + " by " + userID;
                        }
                        else {
                            returnMessage = "Bid of $" + amount + " on item "
                            + itemID + " by " + userID + " is too little";
                        }
                    }
                }
        }

        return returnMessage;
    }

    private List<String> extractValues(String in) {
        String[] newIn = in.split(" ");

        List<String> inputValues = new ArrayList<>();

        for(String s : newIn) {
            for(int i = 0; i < s.length(); i++) {
                if(Character.isDigit(s.charAt(i))) {
                    inputValues.add(s);
                    break;
                }
            }
        }
        return inputValues;
    }

    public synchronized String processBankInfo
            (MessageInfo newMessage){
        String returnMessage = newMessage.toString();
        return returnMessage;
    }
    
    public void disconnect() throws IOException {
        if(server != null) {
            server.close();
        }
        if(server != null) {
            auctionToBank.close();
        }
    }

    public void setIsConnected(boolean status) {
        this.isConnected = status;
    }

    public void setCurrList(ArrayList<Item> l) {
        this.currList = l;
    }

    public ServerSocket getServer() {
        return server;
    }

    public boolean bid(String ItemId, String bidder, int bid) {
        for (Item i : currList) {

            if(i.getItem().equals(ItemId)) {
                if(bid > i.getCurrBid()) {
                    i.placeBid(bidder, bid);
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public String getCatalogue(ArrayList<Item> a) {
        String temp = "";
        for(Item i : a) {
            temp += (i.toString() + " \n");
        }
        return temp;
    }
}
