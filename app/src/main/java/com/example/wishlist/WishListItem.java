package com.example.wishlist;
import android.os.Parcel;
import android.os.Parcelable;

public class WishListItem implements Parcelable {
    private String itemName, itemLocation, docID;
    private int itemPriority;

        // may be implemented later so we can sort by order of importance on list
        // value of 1-3 with 1 being most desired items

    // needed  for the Parcelable code to work
    public static final Parcelable.Creator<WishListItem> CREATOR = new Parcelable.Creator<WishListItem>() {

        @Override
        public WishListItem createFromParcel(Parcel parcel) {
            return new WishListItem(parcel);
        }

        @Override
        public WishListItem[] newArray(int size) {
            return new WishListItem[0];
        }
    };


    /** This is a "constructor" of sorts that is needed with the Parceable interface to
     * tell the intent how to create a WishListItem object when it is received from the intent
     * basically it is setting each instance variable as a String or Int
     * if the instance variables were objects themselves you would need to do more complex code
     *
     * @param parcel    the parcel that is received from the intent
     */

    public WishListItem(Parcel parcel) {
        itemName = parcel.readString();
        itemLocation = parcel.readString();
        itemPriority = parcel.readInt();
        docID = parcel.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    /**
     * This is what is used when we send the Event object through an intent
     * It is also a method that is part of the Parceable interface and is needed
     * to set up the object that is being sent.  Then, when it is received, the
     * other Event constructor that accepts a Parcel reference can "unpack it"
     *
     */
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(itemName);
        dest.writeString(itemLocation);
        dest.writeInt(itemPriority);
        dest.writeString(docID);
    }

    public WishListItem(String itemName, String itemLocation, int itemPriority, String docID) {
        this.itemName = itemName;
        this.itemLocation = itemLocation;
        this.itemPriority = itemPriority;
        this.docID = docID;
    }

    public WishListItem(String itemName, String itemLocation, int itemPriority) {
        this.itemName = itemName;
        this.itemLocation = itemLocation;
        this.itemPriority = itemPriority;
        this.docID = "No docID yet";
    }

    public WishListItem() {
        this.itemName = "";
        this.itemLocation = "";
        this.itemPriority = 5;
        this.docID = "No docID yet";
    }

    public String toString() {
        return itemName + " at " + itemLocation;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }

    public int getItemPriority() {
        return itemPriority;
    }

    public void setItemPriority(int itemPriority) {
        this.itemPriority = itemPriority;
    }

    public String getDocID() {
        return docID;
    }
    public void setDocID(String docID) {
        this.docID = docID;
    }
}
