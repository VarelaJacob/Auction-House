/**
 * Jacob Varela
 * CS-351-004
 * 10-09-2020
 */

package Messaging;

import Agent.Agent;
import AuctionHouse.AuctionHouse;
import Messaging.MessageInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class will implement ObjectOutPutStream and utilize a 
 * BlockingQueue to help transmit messages between different 
 * clients. This class will also update the sender of the
 * message as to any status/replies from the destiniation, if
 * applicable. It will also identify if the connection between
 * two clients has been lost.
 * 
 * @author Jacob Varela
 */
public class MessageOut implements Runnable{
    
    // Create a blocking queue for the outgoing messages.
    public BlockingQueue<MessageInfo> outQueue;

    // Integer contains the socket port between an agent and a bank.
    public int bankPort;

    //  This is the port number on which the connection is made.
    private int portNum;

    //  ipAddress of the bank.
    private String ipAddress;

    // String representation of the object that will be receiving the message.
    private String destination;
    
    // Initialize Auction House and Agent.
    private AuctionHouse auction = null;
    private Agent agent = null;

    /**
     * MessageOut constructor.
     * Initializes a new Linked Blocking Queue for outgoing messages.
     * 
     * @param destination String representing the name of the object that will
     * be receiving the message.
     */
    public MessageOut(String destination){
        outQueue = new LinkedBlockingQueue<MessageInfo>();
        this.destination = destination;
    }

    /**
     * Setter method.
     * Used to store the bank's connection information.
     * 
     * @param ipAddress String representing the ipAddress of the 
     *                  current connection.
     * @param portNum   Integer representing the socket port number 
     *                  of the current connection.
     */
    public void setHostAndPort(String ipAddress, int portNum) {
        this.ipAddress = ipAddress;
        this.portNum   = portNum;
    }

    /**
     * Setter method. 
     * Create a reference of the Auction House Object
     * to make interacting easier. Only applicable if MessageOut is called
     * by an Auction House.
     * 
     * @param auction AuctionHouse A reference to the Auction house object that
     *                             created this instance of MessageOut.
     */
    public void setAuction(AuctionHouse auction){
        this.auction = auction;
    }

    /**
     * Setter method. 
     * Create a reference of the Agent Object
     * to make interacting easier. Only applicable if MessageOut is called
     * by an Agent.
     * 
     * @param agent Agent A reference to the Agent object that
     *                    created this instance of MessageOut.
     */
    public void setAgent(Agent agent){
        this.agent = agent;
    }

    /**
     * This class will handle any incomming messages. It will
     * identify the objective of the message received and
     * execute the commands received, if applicable and if able.
     */
    public class receiveMessages implements Runnable{
        
        // Use ObjectInputStream to receive messages from a server.
        ObjectInputStream serverInput;
        
        /**
         * receiveMessages constructor.
         * Initializes the serverInput variable.
         * 
         * @param serverInput ObjectInputStream Input received from the server.
         */
        public receiveMessages(ObjectInputStream serverInput){
            this.serverInput = serverInput;
        }

        /**
         * connectNewAuction method.
         * This method receives the proper Auction House identifiers from a 
         * Message and uses that information to connect an agent to a
         * new Auction House.
         * 
         * @param message Message containing information about the Auction
         *                House that we are trying to connect to.
         * @throws IOException
         * @throws InterruptedException
         */
        public void connectNewAuction(String message)
            throws IOException, InterruptedException{
                if(agent != null){
                    String auctionID = 
                        message.substring(message.indexOf("Name:")+6,
                            message.indexOf(","));
                    String address = 
                        message.substring(message.indexOf("Address:")+9,
                            message.indexOf("Port:")-2);
                    int portNumber =
                        Integer.parseInt(
                            message.substring(message.indexOf("Port:")+6
                        ));

                    agent.connectToAuctionHouse(auctionID, address, portNumber);
                }
        }

        /**
         * parseReceivedMessage method.
         * This method receives a message from the server and identifies
         * what Object is the intended destination. Once that is done it will 
         * either print some update of what was done or execute a task 
         * specified in the received message. 
         * 
         * @param currMessage Message The current message that we are parsing.
         * @throws IOException
         * @throws InterruptedException
         */
        private void parseReceivedMessage(MessageInfo currMessage)
            throws IOException, InterruptedException {
                
                if(auction != null){
                    auction.processBankInfo(currMessage);
                }
                else if(currMessage.message.contains("YourIDis ") &&
                        agent != null){
                    agent.setAgentID(currMessage.message.split(" ")[1]);
                    return;
                }
                else if(agent != null &&
                        currMessage.message.contains("delete")) {
                    agent.removeAuctionHouse(currMessage.message.split(" ")[1]);
                    return;
                }
                else if(agent!=null && 
                        currMessage.message.contains("successful bid on")){
                    agent.setIsBidding(true);
                }
                else if(agent!=null &&(
                        currMessage.message.contains("You did not transfer the money quickly enough") ||
                        currMessage.message.contains("You have been outbid on the Item:") ||
                        currMessage.message.contains("Sold item")
                )){
                    agent.setIsBidding(false);
                }                             
                else if(currMessage.message.contains(
                        "A new auction house has been created")){
                    connectNewAuction(currMessage.message);
                }
                else if(currMessage.message.contains(
                        "list of the auction houses that are currently up"
                        )){

                    String[] temp = currMessage.message.split(
                        "\\r?\\n");

                    for(int i = 3; i < temp.length; i++){
                        connectNewAuction(temp[i]);
                    }
                }    
                System.out.println(destination + ": " + currMessage.message);
                         
        }

        /**
         * run Method.
         * Utilize a while loop to indefinitely wait for incoming messages
         * from the server. Will print out to the command line if the connection
         * is lost. 
         */
        @Override
        public void run(){
            MessageInfo currMessage;
            
            try{
                while( (currMessage = (MessageInfo) 
                    serverInput.readObject()) != null){
                    parseReceivedMessage(currMessage);
                }
            } catch (IOException | 
                     ClassNotFoundException | 
                     InterruptedException e){
                if(destination.equals("Bank")){
                    System.out.println("Connection lost with the Bank, exiting " +
                    "program");
                System.exit(1);
                } else {
                    System.out.println("Connection lost with an Auction " +
                        "House, please ask the bank for existing Auction " +
                        "Houses for " +
                        "more information");
                }                    
            }
        }
    }

    /**
     * run Method.
     * This method starts when a new MessageOut Thread is created.
     * Connects the server and waits indefinitely for messages until
     * the connection is terminated.
     */
    @Override
    public void run() {

        try(
            Socket socket = new Socket(ipAddress, portNum);
            ObjectOutputStream serverOut = 
                new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream serverIn = 
                new ObjectInputStream(socket.getInputStream());
        
        ){
        
        MessageInfo currMessage = null;

        try{
            currMessage = (MessageInfo) serverIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(agent!=null) {
            System.out.println("Consult the \"help\" menu"+
                               " for more commands");
        }

        bankPort = Integer.parseInt(currMessage.message);
        System.out.println("You are connected with the " + destination +
            " on port: " +bankPort);

        if(agent!=null && destination.equals("Bank")){
            try{
                currMessage = (MessageInfo) serverIn.readObject();
                String agentID = currMessage.message;
                agent.setAgentID(agentID);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        
        (new Thread(new receiveMessages(serverIn))).start();

        while(true){
            try{
                MessageInfo toSend = outQueue.take();
                serverOut.writeObject(toSend);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    } catch( IOException e){
        System.out.println("Invalid host address or port number! Please " +
            "restart the" +
            " " +
            "program and " +
            "input a valid " +
            "address corresponding to the Bank.\nIf the address is " +
            "correct, make sure the Bank or Auction House you are " +
            "trying to connect to" +
            " is running before trying to " +
            "connect agents or auction houses.");
    System.exit(1);                
    }
    }
}
