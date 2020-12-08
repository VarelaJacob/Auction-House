package AuctionHouse;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Item {

    //
    private String name;
    
    //
    private double currBid;
    private double minBid;
    
    //
    private long timeLeft;
    
    //
    private Instant bidTime;
    
    //
    private String bidder;
    
    //
    private String auction;
    
    //
    private String itemID;
    
    //
    private final int TIMELIMIT = 30;

    /**
     * 
     * @param name
     * @param price
     * @param auction
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
     * 
     * @param bidder
     * @param price
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
     * 
     * @param p
     */
    public void setCurrBid(int p) {
        this.currBid = p;
    }

    /**
     * 
     * @param id
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
 * 
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
        //printItems();
    }
}