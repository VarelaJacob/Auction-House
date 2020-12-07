package Bank;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
    private static void main(String[] args){

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

                return returnMessage;
            case "auction":
            case "bank":
            default:
                return returnMessage;
        }
        
    }
        
    /************ */    
    private String transferStatus(int actionStatus, int transferAmount, String agent, String auction) {
        String message;

        switch(actionStatus){
            case 1:
                message = messageDivider + '\n'+
                          "Transfer successful." + '\n'+
                          "Transferred " + transferAmount +
                          " from Agent: " + agent + 
                          " to Auction House: " + auction + ".\n"+
                          messageDivider;
                

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
        int actionStatus;

        if( (transferAmount <= 0) ||
            (transferAmount > agentBal.get(agentID)) ){
            return actionStatus;
        }

        agentBlockedFunds.replace(agentID, agentBlockedFunds.get(agentID) - transferAmount);
        auctionBal.replace(auctionID, auctionBal.get(auctionID) + transferAmount);

        actionStatus = true;

        return actionStatus;
    }

    /************ */
    private boolean UntransferFunds(String agentID, String auctionID, int transferAmount){
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
