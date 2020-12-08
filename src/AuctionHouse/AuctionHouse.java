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

/**
 * This AuctionHouse class contains methods that will 
 * allow for communication between this object and the bank or
 * an agent. Once it is properly connected to the bank and has an
 * account with the bank it will begin to display a random number
 * of items less than the specified maximum number of items, counting
 * down at the specified maximum timer until the auctions are complete.
 * 
 * @author Jacob Varela
 */
public class AuctionHouse {
    
    // Socket connections to the bank
    public ServerSocket server;
    public Socket auctionToBank;

    // Used to communicate to the bank
    public MessageOut bankLink;

    // Used for communication Stream.
    private ObjectOutputStream out;

    // ID given by the bank.
    private String auctionID;

    // Identifies if there is a connection to the Bank.
    private boolean isConnected;

    /*
     * The HashMap key is the ID of the Agent.
     * The HashMap value is an input communication. 
     */
    private HashMap<String, MessageIn> agentLink;

    // List of current items for sale.
    private ArrayList<Item> currList = new ArrayList<>();

    // The Auction House's ip address.
    private String myIpAddress;
    
    /**
     * AuctionHouse Constructor.
     * Initializes the Auction House and makes a connection with the bank
     * at the specified parameters.  
     *
     * @param ipAddress Bank's ip address.
     * @param clientPort Auction House's port.
     * @param serverPort Bank's Port.
     */
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

    /**
     * This method will parse the newMessage to identify who the message
     * is coming from, what they want the message to execute, and it will
     * execute it if it is a valid command but not otherwise. May return
     * and/or send reply messages or messages to other Actors. 
     * 
     * @param newMessage Message containing instructions for the method.
     * @param socket The socket the communication is on.
     * @param connectionHandler Message containing information for an Actor.
     * @return String with information about the transaction.
     * @throws IOException
     */
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

    /**
     * extractValues method.
     * This method will take an input String and take every
     * word separated by a space with a number in it and 
     * store them in a String Array.
     * 
     * @param inputText String input to parse.
     * @return List of words with digits in them.
     */
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

    /**
     * This method will return a message in the form of a String.
     * @param newMessage The Message received.
     * @return Message in String form.
     */
    public synchronized String processBankInfo
            (MessageInfo newMessage){
        String returnMessage = newMessage.toString();
        return returnMessage;
    }
    
    /**
     * Disconnect method.
     * This method will forcively close the server connection.
     */
    public void disconnect() throws IOException {
        if(server != null) {
            server.close();
        }
        if(server != null) {
            auctionToBank.close();
        }
    }

    /**
     * Sets if this Auction House is connected to a Bank or not.
     * @param status Boolean indicating if there is a connection to a bank.
     */
    public void setIsConnected(boolean status) {
        this.isConnected = status;
    }

    /**
     * Setter that sets the current List of items to a specified list.
     * @param L List of items currently for sale.
     */
    public void setCurrList(ArrayList<Item> l) {
        this.currList = l;
    }

    /**
     * Getter that return's the serverSocker.
     * @return Server
     */
    public ServerSocket getServer() {
        return server;
    }

    /**
     * bid method.
     * Thid method places a specified bid onto an item.
     * 
     * @param ItemId The item being bid on.
     * @param bidder The Agent bidding on the item.
     * @param bid The amount to bid.
     * @return Boolean indicating if the bid was successful or not.
     */
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

    /**
     * Getter that returns a duplicate of the current items for sale.
     * @param a Item a
     * @return A String line of one item for sale.
     */
    public String getCatalogue(ArrayList<Item> a) {
        String temp = "";
        for(Item i : a) {
            temp += (i.toString() + " \n");
        }
        return temp;
    }
}
