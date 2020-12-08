package AuctionHouse;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Item {

    // Identifies the different items.
    private String name;
    
    // double values to store bidding values.
    private double currBid;
    private double minBid;
    
    // How much time is left in the auction.
    private long timeLeft;
    
    // Current auctin bidding time remaining.
    private Instant bidTime;
    
    // name of the person bidding.
    private String bidder;
    
    // Auction House ID
    private String auction;
    
    // Item ID.
    private String itemID;
    
    // Defines how long the items are for sale (seconds).
    private final int TIMELIMIT = 30;

    /**
     * Item Constructor.
     * Initializes Item values.
     * 
     * @param name the name of the item.
     * @param price How much the item costs to purchase.
     * @param auction Auction House.
     */
    public Item(String name, int price, String auction){
        this.name = name;
        this.minBid = price;
        this.auction = auction;
        this.timeLeft = TIMELIMIT;
        this.itemID = Integer.toString(price) +
                          name.substring(0,3);
        bidTime = Instant.now();
    }

    /**
     * placeBid method.
     * This method updates the item to keep track
     * of who bid and how much.
     * 
     * @param bidder Which Agent holds the current highest bid.
     * @param price Price of the minimum bid.
     */
    public void placeBid(String bidder, double price){
        this.bidder = bidder;
        this.currBid = price;
        bidTime = Instant.now();
    }

    /**
     * Getter that gets the ID of the Item.
     * @return String that represents the Item.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter that gets the current bid price on the Item.
     * @return the double representing the current bid.
     */
    public double getCurrBid() {
        return currBid;
    }

    /**
     * Getter that gets the starting bid price on the Item.
     * @return the double representing the starting bid.
     */
    public double getMinBid() {
        return minBid;
    }

    /**
     * Getter that gets the ID of the current bidder of the item.
     * @return
     */
    public String getBidder() {
        return bidder;
    }

    /**
     * Getter that gets the ID of the item
     * @return String that represents the ID of the item.
     */
    public String getItem() {
        return itemID;
    }

    /**
     * Getter that gets the time left to bid on the item.
     * @return time left to bid on the item.
     */
    public long getTimeLeft() {
        return timeLeft;
    }

    /**
     * Setter that sets the time left to bid on the example.
     * @param timeLeft time left to bid.
     */
    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    /**
     * Getter that gets the start of the bidding for the item.
     * @return Instant of when the bidding starts.
     */
    public Instant getBidTime() {
        return bidTime;
    }

    /**
     * Setter that stores the current bid amount.
     * @param p Amount bid on this item.
     */
    public void setCurrBid(int p) {
        this.currBid = p;
    }

    /**
     * Setter that stores which Agent has the highest bid on this item.
     * @param id AgentID who holds the highest bid.
     */
    public void setBidder(String id) {
        this.bidder = id;
    }
    
    /**
     * Method that updates the current time with the time that has elapsed.
     * @param curr Instant which holds the current times.
     */
    public void updateTime(Instant curr) {
        Duration d = Duration.between(getBidTime(), curr);
        setTimeLeft(TIMELIMIT - d.getSeconds());
    }

    /**
     * Method that pads a string with spaces to the right of the string.
     * @param s String being padded.
     * @param maxLength Maximum length that the string and the spaces can be.
     * @return String that is padded up to maxLength spaces.
     */
    public String padString(String s, int maxLength) {
        return String.format("%-" + maxLength + "s", s);
    }

    /**
     * Prints the current Item as a String representation.
     * @return
     */
    @Override
    public String toString() {
        return padString(getName(), 20) + " | Current Bid: " + padString(String.valueOf(currBid), 15) +
                " | Minimum Bid: " + padString(String.valueOf(minBid), 15) + " | ID: " + padString(getItem(), 12) + " | Time Left: " + timeLeft;
    }
}

/**
 * Creates the list of items to be used as inventory
 * for the Auction Houses.
 */
class Items {
    public ArrayList<Item> items = new ArrayList<>();

    /**
     * Constructor that creates a List of items each with a random price for each.
     * @param s Scanner containing the file.
     * @param auctionID ID of the Auction House that is calling and creating the
     * List of items.
     */
    public Items(Scanner s, String auctionID) {
        Random r = new Random();
        while(s.hasNextLine()) {
            String name = s.nextLine();
            int price = r.nextInt(75);
            items.add(new Item(name, price, auctionID));
        }
    }
}