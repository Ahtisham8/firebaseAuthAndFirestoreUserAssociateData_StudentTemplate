package com.example.wishlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewListActivity extends AppCompatActivity {

    private ArrayList<WishListItem> myList;
    private static final String TAG = "Denna";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        // Get ArrayList of WishListItems from MainActivity (that was pulled from firestore)
        Intent intent = getIntent();
        // get list of data from firebasehelper

        // The ArrayAdapter is what will take the data from the ArrayList and feed it to the ListView
        ArrayAdapter<WishListItem> listAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, myList);

        // This finds the listView and then adds the adapter to bind the data to this view
        ListView listView = (ListView) findViewById(R.id.myWishList);
        listView.setAdapter(listAdapter);

//      Create listener to listen for when an item from the wish list is clicked on
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Creates an intent to go from the full wish list to edit or delete one item
                Intent intent = new Intent(ViewListActivity.this, EditItemActivity.class);

                // Sends the specific object at index i to the Edit activity
                // In this case, it is sending the particular WishListItem object
                intent.putExtra("ITEM_TO_EDIT", myList.get(i));
                startActivity(intent);

            }
        });
    }
}
