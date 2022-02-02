package com.example.wishlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class AddItemActivity extends AppCompatActivity {

    private String itemName, itemLocation;
    private int itemPriority;
    private EditText nameET, locationET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        nameET = findViewById(R.id.itemNameET);
        locationET = findViewById(R.id.itemLocation);

    }

    public void seeList(View v) {
        // will go to activity that displays all data in a listview
        Intent intent = new Intent(AddItemActivity.this, ViewListActivity.class);
        startActivity(intent);
    }

    public void goHome(View v) {
        Intent intent = new Intent(AddItemActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void addData(View v) {
        itemName = nameET.getText().toString();
        itemLocation = locationET.getText().toString();
        itemPriority = 1;       // may update later w spinner

        WishListItem wishListItem = new WishListItem(itemName, itemLocation, itemPriority);
        //insert firebaseHelper code to addData
        MainActivity.firebaseHelper.addData(wishListItem);
        nameET.setText("");
        locationET.setText("");

    }
}