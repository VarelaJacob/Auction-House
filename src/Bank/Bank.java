package Bank;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import Message.MessageInfo;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import Message.MessageIn;


public class Bank {

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
    public synchronized String handleMessage(){

    }
        
    /************ */
    private void notifyAuction() {

    }

    /************ */
    private void notifyAgents() {

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
    private boolean transferFunds(String agentID, String auctionID, int transferAmount){
        boolean actionStatus = false;

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
    private boolean UntransferFunds(String agentID, string auctionID, int transferAmount){
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
    public HBox bankInfo() {

    }

    /************ */
    public VBox bankConnections(){

    } 
}
