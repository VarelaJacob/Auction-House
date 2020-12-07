package Agent;

import Messaging.MessageOut;
import Messaging.MessageInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

public class Agent {

    //
    private MessageOut bankLink;

    //
    private HashMap<String, MessageOut> auctionLink;

    //
    private String agentID;

    //
    private boolean isBidding;

    public Agent(){
        bankLink = new MessageOut("Bank");
        auctionLink = new HashMap<>();
    }

    public static void main(String[] args) throws IOException,
        InterruptedException {
            Agent newAgent = new Agent();

            System.out.println("Please type the Bank's address and port number:"
                + "\n<IP address/host name> <Port>" );
            
            BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in));
            String[] bankInfo = null;
            boolean isBankValid = false;
            int portNum = -1;
            String ipAddress;

            while( !isBankValid ){
                bankInfo = stdIn.readLine().split(" ");

                if(bankInfo.length != 2) {
                    System.out.println("Invalid bank info");
                }
                else{
                    try{
                        portNum = Integer.parseInt(bankInfo[1]);

                        if(portNum > 65535 ||
                           portNum < 1){
                            System.out.println("invalid port num");
                        }
                        else{
                            isBankValid = true;
                        }
                    } catch (NumberFormatException e){
                        System.out.println("invalid port num.");
                    }                    
                }
            }

            ipAddress = bankInfo[0];
            newAgent.bankLink.setHostAndPort(ipAddress, portNum);
            newAgent.bankLink.outQueue.put(
                new MessageInfo("agent", "create account", null, 0, 0)
            );
            newAgent.bankLink.setAgent(newAgent);

            newAgent.startBankThread();

            String inputStrings;

            while(true){
                if( (inputStrings = stdIn.readLine()).equals("exit") ){
                    newAgent.terminateAgent();
                    continue;
                }
                String[] userInput = inputStrings.split(" ");

                newAgent.processOutbox(
                    userInput[0],
                    String.join(
                        " ",
                        Arrays.copyOfRange(userInput, 1, userInput.length)
                    )
                );
            }
    }
    /******** */
    private void startBankThread(){
        (new Thread((Runnable) bankLink)).start();
    }

    /****** */
    public synchronized void setIsBidding(boolean status){
        isBidding = status;
    }

    /****** */
    private void terminateAgent(){
        if (isBidding){
            System.out.println("Cannot exit the agent, bidding is in progress" +
                    ".\nPlease terminate the bid before exiting");
        }else {
            System.out.println("Exiting the Agent, bye");
            System.exit(1);
        }
    }

    /****** */
    public void setAgentID(String agentID){
        this.agentID = agentID;
    }

    /****** */
    public void removeAuctionHouse(String auctionID){
        if (auctionLink.containsKey(auctionID)){
            auctionLink.remove(auctionID);
        }
    }

    /****** */
    public void connectToAuctionHouse(String auctionID, String hostName,
                                      int portNumber) throws IOException,
            InterruptedException {
        auctionLink.putIfAbsent(auctionID,new
                MessageOut("Auction " +
                "House " + auctionID));
                auctionLink.get(auctionID).setHostAndPort(hostName,
                portNumber);
                auctionLink.get(auctionID).setAgent(this);
        (new Thread((Runnable) auctionLink.get(auctionID))).start();

        processOutbox(auctionID,"ID "+agentID);
    }

    /****** */
    private void processOutbox(String destination, String message)
            throws InterruptedException, IOException {
        switch (destination.toLowerCase()) {


            case "help":
                printHelpMenu();
                break;

            //
            case "bank":
                if (message != null) {
                    bankLink.outQueue.put(new MessageInfo(
                            "agent",
                            message, null, null,
                            0));
                } else {
                    System.out.println("Please give a valid command for the " +
                            "Bank to process");
                }
                break;

                //
            case "auction_house":
                if (message.equals("connect")){
                    BufferedReader stdIn2 =
                            new BufferedReader(new InputStreamReader(System.in));
                    System.out.println("Please provide an auction house ID, " +
                            "its address and port " +
                            "number on which the agent should connect: ");
                    String[] info =  stdIn2.readLine().split(" ");
                    try {
                        String auctionID = info[0];
                        String hostName = info[1];
                        int portNumber = Integer.parseInt(info[2]);

                        auctionLink.putIfAbsent(auctionID,
                                new MessageOut(
                                        "Auction " +
                                                "House " + auctionID));
                        auctionLink.get(auctionID)
                                .setHostAndPort(hostName, portNumber);
                        (new Thread(auctionLink
                                .get(auctionID))).start();
                    } catch (Exception e){
                        System.out.println("Invalid information given, please" +
                                " double check");
                    }
                }
                else{
                    System.out.println("Invalid Auction House Command");
                }
                break;
            
            //
            case "bank_port":
                //
                System.out.println(bankLink.bankPort);
                break;

            //
            case "current":
                if (message.equals("AH connections")) {
                    System.out.println("Here are the auction houses you are " +
                            "currently connected to:");
                    if (auctionLink.keySet().isEmpty()) {
                        System.out.println("You are not currently connected " +
                                "to any Auction House");
                    } else {
                        for (String str : auctionLink
                                .keySet()) {
                            System.out.println("\t- " + str);
                        }
                    }
                }
                break;

            default:
                //
                if(destination.toLowerCase().contains("ah-")){
                    try {
                        if ( auctionLink.containsKey(destination) ) {
                            auctionLink
                                    .get(destination)
                                    .outQueue
                                    .put(new MessageInfo("agent",
                                            message, null,
                                            0, 0));
                        } else {
                            System.out.println("No connection with the given " +
                                    "Auction House exists. " +
                                    "Make sure you create a connection " +
                                    "before making any request.");
                        }
                    }catch (Exception e){
                        System.out.println("Invalid instructions for an " +
                                "Auction House.\nPlease input help for " +
                                "more information");
                    }
                }
                else{
                    System.out.println("Wrong Input Argument, please type <help> " +
                            "for all available options.");
                }
                return;
        }
    }

    private void printHelpMenu() {
    }
}
