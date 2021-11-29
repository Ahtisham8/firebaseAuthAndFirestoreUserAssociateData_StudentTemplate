package com.example.wishlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditItemActivity extends AppCompatActivity {

    private EditText itemNameET, itemLocation;
    private WishListItem w;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        itemLocation = findViewById(R.id.itemLocation);
        itemNameET = findViewById(R.id.itemNameET);

        Intent intent = getIntent();
        w = intent.getParcelableExtra("ITEM_TO_EDIT");
        itemNameET.setText(w.getItemName());
        itemLocation.setText(w.getItemLocation());
    }

    public void updateData(View v) {
        String newName = itemNameET.getText().toString();
        String newLocation = itemLocation.getText().toString();
        String docID = w.getDocID();
        w.setItemLocation(newLocation);
        w.setItemName(newName);
        // firebaseHelper code
        Toast.makeText(this, "Data updated", Toast.LENGTH_SHORT).show();
    }


    public void showList(View v) {
        // will go to activity that displays all data in a listview
        Intent intent = new Intent(EditItemActivity.this, ViewListActivity.class);
        startActivity(intent);
    }

    public void deleteItem(View v) {
        // firebaseHelper code
        Toast.makeText(this, "Data updated", Toast.LENGTH_SHORT).show();
    }

    public void goHome(View v) {
        Intent intent = new Intent(EditItemActivity.this, MainActivity.class);
        startActivity(intent);
    }
}