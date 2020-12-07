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

public class MessageOut implements Runnable{
    
    //
    public BlockingQueue<MessageInfo> outQueue;

    //
    public int bankPort;

    //
    private int portNum;

    //
    private String ipAddress;

    //
    private String destination;
    
    //
    private AuctionHouse auction = null;
    private Agent agent = null;

    /******** */
    public MessageOut(String destination){
        outQueue = new LinkedBlockingQueue<MessageInfo>();
        this.destination = destination;
    }

    /******* */
    public void setHostAndPort(String ipAddress, int portNum) {
        this.ipAddress = ipAddress;
        this.portNum   = portNum;
    }

    /******* */
    public void setAuction(AuctionHouse auction){
        this.auction = auction;
    }

    /******* */
    public void setAgent(Agent agent){
        this.agent = agent;
    }

    public class receiveMessages implements Runnable{
        //
        ObjectInputStream serverInput;
        
        /******/
        public receiveMessages(ObjectInputStream serverInput){
            this.serverInput = serverInput;
        }

        /***** */
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

        /***** */
        private void parseReceivedMessage(MessageInfo currMessage)
            throws IOException, InterruptedException {
                
                if(auction != null){
//                    auction.processBankInfo(currMessage);
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
// altered from line 280                
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
