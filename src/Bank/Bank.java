package Bank;

import java.io.ObjectOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Messaging.MessageInfo;
import Messaging.MessageIn;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Bank {

    // To help with message formatting.
    private String messageDivider = "-----------------------------\n";

    // Variables for Actor communication
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectInputStream  out;
    private ObjectOutputStream in;

    // ipAddress and portNum are static and chosen when creating the bank.
    private String ipAddress;
    private int portNum;

    // Integers to assign a new Agent/Auction House a unique ID number.
    private int agentNum;
    private int auctionNum;

    /*
     * The HashMap key is the ID of the Agent or Auction House.
     * The HashMap value is a dollar amount of the Agent or Auction House. 
     */
    private HashMap<String,Integer> agentBal;
    private HashMap<String,Integer> auctionBal;

    /*
     * The HashMap key is the ID of the Agent.
     * The HashMap value is the dollar amount that will need to be blocked.
     */
    private HashMap<String,Integer> agentBlockedFunds;

    /*
     * The HashMap key is the address of the Auction House.
     * The HashMap value will store the Auction house ID and port number.
     */
    private HashMap<String,String[]> auctionInfo;

    /*
     * The HashMap key is a port Number.
     * The HashMap value is the ID of the Agent or Auction House.
     */
    private HashMap<Integer,String> agentPort;
    private HashMap<Integer,String> auctionPort;

    /*
     * The HashMap key is the ID of the Agent or Auction House.
     * The HashMap value is an instance of the connection handler. 
     */
    private HashMap<String,MessageIn> agentLink;
    private HashMap<String,MessageIn> auctionLink;

    /**
     * Bank object Constructor. 
     * This constructor initializes all the HashMaps needed for the bank to 
     * properly store information about any Agents or Auction Houses.
     * The agentNum and auctionNum integers are initialized to 1, this will
     * ensure that any new connections to the bank will provide them with a
     * positive and unique ID number.
     * 
     * @param ipAddress A static, known IP address.
     * @param portNum A static, known port number.
     */
    public Bank(String ipAddress, int portNum){
        this.ipAddress = ipAddress;
        this.portNum   = portNum;

        agentBal  = new HashMap<>();
        agentPort = new HashMap<>();
        agentLink = new HashMap<>();
        agentBlockedFunds = new HashMap<>();

        auctionBal  = new HashMap<>();
        auctionPort = new HashMap<>();
        auctionLink = new HashMap<>();
        auctionInfo = new HashMap<>();

        agentNum   = 1;
        auctionNum = 1;
    }

    /************ */
    public static void main(String[] args) throws IOException {
        System.out.println("\nPlease type the Port number you would like to use for the bank:");
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        String[] inputText = userInput.readLine().split(" ");

        int portNum = Integer.parseInt(inputText[0]);
        Socket socket;

        /** Server Socket for the Bank. */
        try (ServerSocket serverSocket = new ServerSocket(portNum)) {
            String ipAddress = String.valueOf( InetAddress.getLocalHost().getHostAddress() );
            Bank bank = new Bank(ipAddress, portNum);

            System.out.println("\nBank Successfully created.\nBank information:");
            System.out.println("ip address: " + ipAddress);
            System.out.println("Port number: " + portNum);

            while ((socket = serverSocket.accept()) != null) {
                new Thread( new MessageIn(socket, bank)).start();

                System.out.println("New Connection Created");
            }

        } catch (IOException e) {
            System.err.print("Unable to connect to port number " + portNum);
            System.exit(-1);
        }
    }

    /************ */
    public synchronized String handleMessage(
        MessageInfo newMessage, Socket socket, MessageIn connectionHandler)
        throws IOException {

        String messageAuthor = newMessage.source.toLowerCase();
        String returnMessage = "Requestion action was not completed successfully. Please try again.";

        switch(messageAuthor){
            case "agent":
                if( !agentPort.containsKey( socket.getPort() ) &&
                        newMessage.message.equals("create account")){
                            String newAgentID = "A-" + agentNum;
                            
                            agentBal.put (newAgentID, 250);
                            agentPort.put(socket.getPort(), newAgentID);
                            agentLink.put(newAgentID, connectionHandler);
                            agentBlockedFunds.put(newAgentID, 0);

                            agentLink.get(newAgentID).sendMessage(
                                "Your agent ID is: " + newAgentID
                            );
                            agentNum++;

                            returnMessage = messageDivider +
                                            "New account Created." + '\n' +
                                            "AgentID: " + newAgentID + '\n' +
                                            "Account Balance: " + agentBal.get(newAgentID) + '\n' +
                                            messageDivider;
                        }
                else if( !agentPort.containsKey(socket.getPort() )){
                    returnMessage = messageDivider +
                                    "No bank account found." +
                                    "Please create an account with the bank before attempting to interact with the bank" +
                                    messageDivider;
                }
                else if(newMessage.message.contains("current AH connections")){
                    if(auctionLink.isEmpty()){
                        returnMessage = messageDivider +
                                        "No Auction Houses are currently connected to the bank.";
                    }
                    else{
                        returnMessage = messageDivider +
                                        "The Bank is currently connected to the following Auction Houses:\n";

                                        for(Map.Entry<String, String[]> entry :
                                            auctionInfo.entrySet()){
                                            String address = entry.getKey();
                                            String[] info  = entry.getValue();
                                            returnMessage += "Auction House  Address: " +
                                                address + '\n' +
                                                "Name & Port Number: " +
                                                Arrays.toString(info) +
                                                '\n' + messageDivider;
                                        }
                    }
                }
                else if(newMessage.message.equals("account info")){
                    String myID = agentPort.get(socket.getPort());
                    String myPort = String.valueOf(socket.getPort());
                    String myBal = String.valueOf(agentBal.get(myID));
                    String myBlocked = String.valueOf(
                        agentBlockedFunds.get(myID));

                    returnMessage = messageDivider +
                                    "Agent Information." + '\n'+
                                    "ID: " + myID + '\n' +
                                    "Port: " + myPort + '\n' +
                                    "Account Balance: " + myBal +'\n' +
                                    "Funds Blocked: " + myBlocked +'\n'+
                                    messageDivider;
                }
                else if(newMessage.message.contains("transfer")){
                    List<String> messageValues = 
                        extractValues(newMessage.message);

                    int actionStatus;
                    int transferAmount;
                    String auctionID;

                    if(messageValues.size() != 2){
                        returnMessage = messageDivider +
                                        "Invalid number of identifiers. " +'\n'+
                                        "Message should include the Auction House ID and funds amount." +'\n'+
                                        messageDivider;
                    }
                    else{
                        transferAmount = 
                            Integer.parseInt(messageValues.get(1));
                        auctionID = messageValues.get(0);

                        if( blockFunds(
                            agentPort.get(socket.getPort()),
                            transferAmount)){
                            actionStatus = transferFunds(
                                agentPort.get(socket.getPort()),
                                auctionID,
                                transferAmount);
                            
                            returnMessage = transferStatus(actionStatus,
                                                transferAmount,
                                                agentPort.get(socket.getPort()),
                                                auctionID);

                            return returnMessage;
                        }
                    }
                    returnMessage += " Error Blocking Funds.\n";
                }
                else if(newMessage.message.contains("bank info")){
                    returnMessage = messageDivider +
                                    "Bank information:" +'\n'+
                                    "ip address: " + ipAddress +'\n'+
                                    "Port Number: " + portNum + '\n'+
                                    messageDivider;
                }

                return returnMessage;

            case "auction":
                String auctionID = auctionPort.get(socket.getPort());

                if(newMessage.message.equals("create account")){
                    if(auctionPort.containsKey(socket.getPort())){
                        returnMessage = messageDivider +
                                        "An account for this Auction House already exists." +'\n'+
                                        "Please try another command." +
                                        messageDivider;
                    }
                    else {
                        String auctionHouseID = "AH-" + auctionNum;
                        String newConnection;

                        auctionBal.put(auctionHouseID, 0);
                        auctionPort.put(socket.getPort(), auctionHouseID);
                        auctionLink.put(auctionHouseID, connectionHandler);
                        auctionInfo.put(auctionHouseID, new String[]{
                            newMessage.AHAddress,
                            Integer.toString(newMessage.AHPort)});
                        auctionNum++;
                        
                        newConnection = messageDivider + 
                                        "A new Auction House has connected to the Bank!" + '\n'+
                                        "Auction House ID: " + auctionHouseID +'\n'+
                                        "Auction House Port: " + newMessage.AHPort +'\n'+
                                        "Auction House Address: " +
                                        newMessage.AHAddress + '\n'+
                                        messageDivider;

                        notifyAgents(newConnection);

                        returnMessage = messageDivider +
                                        "A new Auction House has connected to the Bank!"+'\n'+
                                        "Auction House ID: " + auctionHouseID +'\n'+
                                        "Auction House Port: " + newMessage.AHPort +'\n'+
                                        "Auction House Address: " +
                                        newMessage.AHAddress +'\n'+
                                        "Auction House Balance: " +
                                        auctionBal.get(auctionHouseID) +'\n'+
                                        messageDivider;
                    }
                }
                else if( !auctionPort.containsKey(socket.getPort())){
                    returnMessage = messageDivider +
                                    "No Auction House account found." + '\n'+
                                    "Please create an account with the bank before attempting to interact with the bank."
                                    +'\n'+messageDivider;
                }
                else if(newMessage.message.contains("checkID")){
                    String [] messageWords = newMessage.message.split(" ");
                    String agendID = messageWords[1];

                    if(agentBal.containsKey(agendID)){
                        returnMessage = messageDivider +
                                        "The agend ID: " + agendID +
                                        "is a valid agent account." +'\n'+
                                        messageDivider;
                    }
                    else{
                        returnMessage = messageDivider +
                                        "The agend ID: " + agendID +
                                        "is not a valid agent account." +'\n'+
                                        messageDivider;
                    }
                }
                else if(newMessage.message.contains("block funds")){
                    List<String> messageValues = extractValues(newMessage.message);

                    if( messageValues.size() != 2){
                        returnMessage = "Invalid number of identifiers." + '\n' +
                                "Message should include Agent ID " +
                                "and funds amount to be blocked.";
                    }
                    else {
                        String agentID = messageValues.get(0);
                        int amountToBlock = (int)(Float.parseFloat(messageValues.get(1)));

                        if( agentBal.get(agentID) > amountToBlock){
                            blockFunds(agentID, amountToBlock);
                            notifyAuction( auctionID, "Valid Bid. Successfully blocked funds.");
                            returnMessage = "Funds were successfully blocked. Bid successful." + '\n' +
                                    "Blocked " + messageValues.get(1) + " from " +
                                    "agent: " + agentID;
                        }
                        else {
                            notifyAuction(auctionID, "Invalid Bid. Unsuccessfully blocked funds.");
                            returnMessage = "Error blocking funds. Invalid bid.";
                        }
                    }
                }
                else if(newMessage.message.contains("release funds")){
                    List<String> messageValues = extractValues(newMessage.message);

                    if( messageValues.size() != 2) {
                        returnMessage = "Invalid number of identifiers." + '\n' +
                                        "Message should include Agent ID and amount of funds to release.";
                    }
                    else {
                        if( agentBlockedFunds.get(messageValues.get(0)) >= (int)(Float.parseFloat(messageValues.get(1))) ){
                            unBlockFunds( messageValues.get(0), (int)(Float.parseFloat(messageValues.get(1)))) ;
                            notifyAuction(auctionID, "Successfully released funds.");
                            returnMessage = "Agent " + messageValues.get(0) + " has had the amount of " +
                                            messageValues.get(1) + " funds released.";
                        }
                        else {
                            notifyAuction(auctionID, "Unsuccessfully released funds.");
                            returnMessage = "Error releasing the funds from Agent " + messageValues.get(0);
                        }
                    }
                }
                else if(newMessage.message.contains("transfer funds")){
                    List<String> messageValues = extractValues(newMessage.message);

                    if(messageValues.size() != 2){
                        returnMessage = messageDivider+
                                        "Invalid number of identifiers.\n" +
                                        "Message should include Agent ID, and amount of funds."
                                        +'\n'+messageDivider;
                    }
                    else {
                        String agentID = messageValues.get(0);
                        int amountToTransfer = (int)(Float.parseFloat(messageValues.get(1)));

                        if( transferFunds(agentID, auctionID, amountToTransfer) == 1) {
                            transferFunds(agentID, auctionID, amountToTransfer);
                            notifyAuction(auctionID, "Successfully transfered funds.");
                            notifyAuction(auctionID, auctionID + " account balance = " + amountToTransfer);
                            returnMessage = "Funds successfully transferred." + '\n' +
                                            "Transferred " + amountToTransfer +
                                            "from " + agentID + "to " + auctionID;
                        }
                    }
                }
                else if(newMessage.message.contains("untransfer funds")){
                    List<String> messageValues = extractValues(newMessage.message);

                    if( messageValues.size() != 2){
                        returnMessage = messageDivider +
                                        "Invalid number of identifiers." + '\n' +
                                        "Message should include Agent ID, and amount of funds."
                                        +'\n'+messageDivider;
                    }
                    else {
                        String agentID = messageValues.get(0);
                        int amountToTransfer = (int)(Float.parseFloat(messageValues.get(1)));

                        if( unTransferFunds(agentID, auctionID, amountToTransfer) ){
                            unTransferFunds(agentID, auctionID, amountToTransfer);
                            notifyAuction(auctionID, "Successfully untransfered funds.");
                            returnMessage = messageDivider+
                                            "Funds successfully untransferred." + '\n' +
                                            "Transferred " + amountToTransfer +
                                            "from " + auctionID + "to " + agentID
                                            +'\n'+messageDivider;
                        }
                        else {
                            notifyAuction(auctionID, "Unsuccessfully untransfered funds.");
                            returnMessage = "Funds were NOT transferred.";
                        }
                    }
                }

                return returnMessage;

            case "bank":
            default:
                return returnMessage;
        }
        
    }
        
    /**
     * @throws IOException**********
     */
    private String transferStatus(int actionStatus, int transferAmount, String agent, String auction)
            throws IOException {
        String message = "";

        switch(actionStatus){
            case 1:
                message = messageDivider +
                          "Transfer successful." + '\n'+
                          "Transferred " + transferAmount +
                          " from Agent: " + agent + 
                          " to Auction House: " + auction + ".\n"+
                          messageDivider;
                
                notifyAuction(auction, message);
            
            case -2:
                message = messageDivider +
                          "Transfer Unsuccessful." +'\n'+
                          "Not enough funds in the Agent's account to transfer."+
                          '\n' + messageDivider;
            
            case -3:
                message = messageDivider +
                          "Transfer Unsuccessful." +'\n'+
                          "The Agent requested a transfer of $0 or less."+
                          '\n' + messageDivider;

            default:
                message = messageDivider +
                          "Transfer Unsuccessful.";
        }

        return message;
    }

    /************ */
    private void notifyAuction(String auctionID, String newMessage) throws IOException {
        auctionLink.get(auctionID).sendMessage(newMessage);
    }

    /************ */
    private void notifyAgents(String newMessage) {
        for( String agentID : agentLink.keySet()) {
            try{
                agentLink.get(agentID).sendMessage(newMessage);
            } catch (IOException e) {
                System.out.println("Unable to send message.");
            }
        }
    }

    /************ */
    private boolean blockFunds(String agentID, int blockAmount) {
        boolean actionStatus = false;

        if( (agentBal.get(agentID) < blockAmount) ||
            (blockAmount <= 0) ){
            return actionStatus;
        }

        agentBlockedFunds.replace(agentID, agentBlockedFunds.get(agentID) + blockAmount);
        agentBal.replace( agentID, agentBal.get(agentID) - blockAmount);

        actionStatus = true;

        return actionStatus;
    }

    /************ */
    private boolean unBlockFunds(String agentID, int unBlockAmount) {
        boolean actionStatus = false;

        if( (agentBlockedFunds.get(agentID) < unBlockAmount) || 
            (unBlockAmount <= 0) ){
            return actionStatus;
        }

        agentBlockedFunds.replace(agentID, agentBlockedFunds.get(agentID) - unBlockAmount);
        agentBal.replace(agentID, agentBal.get(agentID) + unBlockAmount);

        actionStatus = true;

        return actionStatus;
    }

    /************ */
    private int transferFunds(String agentID, String auctionID, int transferAmount){
        int actionStatus = 0;

        if(transferAmount <= 0){
            actionStatus = -3;
            return actionStatus;
        }

        if(transferAmount > agentBal.get(agentID)){
            actionStatus = -2;
            return actionStatus;
        }

        agentBlockedFunds.replace(agentID, agentBlockedFunds.get(agentID) - transferAmount);
        auctionBal.replace(auctionID, auctionBal.get(auctionID) + transferAmount);

        actionStatus = 1;

        return actionStatus;
    }

    /************ */
    private boolean unTransferFunds(String agentID, String auctionID, int transferAmount){
        boolean actionStatus = false;

        if( (transferAmount <= 0) ||
            (transferAmount > auctionBal.get(auctionID)) ){
            return actionStatus;
        }

        auctionBal.replace(auctionID, auctionBal.get(auctionID) - transferAmount);
        agentBal.replace(agentID, agentBal.get(agentID) + transferAmount);

        actionStatus = true;
        
        return actionStatus;
    }

    /************ */
    private List<String> extractValues(String inputText){

        String[] newInputText = inputText.split(" ");

        List<String> inputValues = new ArrayList<>();

        for (String s : newInputText) {
            for (int j = 0; j < s.length(); j++) {

                if (Character.isDigit(s.charAt(j))) {
                    inputValues.add(s);
                    break;
                }
            }
        }

        return inputValues;
    }

    /************ *//*
    public HBox bankInfo() {

    }*/

    /************ *//*
    public VBox bankConnections(){

    } */
}
