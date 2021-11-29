package com.example.wishlist;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * The purpose of this class is to hold ALL the code to communicate with Firebase.  This class
 * will connect with Firebase auth and Firebase firestore.  Each class that needs to verify
 * authentication OR access data from the database will reference a variable of this class and
 * call a method of this class to handle the task.  Essentially this class is like a "gopher" that
 * will go and do whatever the other classes want or need it to do.  This allows us to keep all
 * our other classes clean of the firebase code and also avoid having to update firebase code
 * in many places.  This is MUCH more efficient and less error prone.
 */
public class FirebaseHelper {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    public final String TAG = "Denna";
    private static String uid = null;            // var will be updated for currently signed in user
                                                // inside MainActivity with the mAuth var


    private ArrayList<WishListItem> myItems = new ArrayList<>();

    public FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        attachReadDataToUser();
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }


    public void attachReadDataToUser() {
        // needed for asynch method in reading data
        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getCurrentUser().getUid();
            readData(new FirestoreCallback() {
                @Override
                public void onCallback(ArrayList<WishListItem> myList) {
                    Log.d(TAG, "Inside attachReadDataToUser " + myList.toString());
                }
            });
        }
        else {
            Log.d(TAG, "No one logged in");
        }
    }

    public void editData(WishListItem w) {
        // update the WishListItem w with the new data in parameter
        String docId = w.getDocID();
        db.collection("users").document(uid).collection("myWishList")
                .document(docId)
                .set(w)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, w.getItemName() + " successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Error updating document", e);
                    }
                });
        readData(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<WishListItem> myList) {
                Log.d(TAG, "Inside readData for edit method: " + myList.toString());
            }
        });


    }

    public void deleteData(WishListItem w) {
        // delete the WishListItem w from list
        String docId = w.getDocID();
        db.collection("users").document(uid).collection("myWishList")
                .document(docId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, w.getItemName() + " successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Error deleting document", e);
                    }
                });

        readData(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<WishListItem> myList) {
                Log.d(TAG, "Inside readData for delete method: " + myList.toString());
            }
        });

    }

    public void addUserToFirestore(String name, String newUID) {
        Log.d(TAG, "Inside addUserToFirestore");

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        // Add a new document with a docID equal to authenticated user's UID
        db.collection("users").document(newUID)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, name + "'s user account added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding user account", e);
                    }
                });
    }

    public void addData(WishListItem wish) {
        db.collection("users").document(uid).collection("myWishList")
                .add(wish)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                // documentReference contains a reference to the newly created Document if done successfully
                public void onSuccess(DocumentReference documentReference) {
                    db.collection("users").document(uid).collection("myWishList").
                            document(documentReference.getId()).update("docID", documentReference.getId());
                            // sets the DocID key for the WishListItem that was just added
                    Log.i(TAG, "just added "+ wish.getItemName());
                }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Denna", "Error adding document", e);
                    }
                });


        readData(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<WishListItem> myList) {
                Log.d(TAG, "Inside readData for addData method: " + myList.toString());
            }
        });
    }

    public ArrayList<WishListItem> getWishListItems() {
        Log.d(TAG, "wishList returned from firebasehelper");
        return myItems;
    }

    public void updateUid(String uid) {
        this.uid = uid;
    }

    /* https://www.youtube.com/watch?v=0ofkvm97i0s
    This video is good!!!   Basically he talks about what it means for tasks to be asychronous
    and how you can create an interface and then using that interface pass an object of the interface
    type from a callback method and access it after the callback method.  It also allows you to delay
    certain things from occuring until after the onSuccess is finished.
     */

    private void readData(FirestoreCallback firestoreCallback) {
        myItems.clear();
        db.collection("users").document(uid).collection("myWishList")
        .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                WishListItem wishListItem = doc.toObject(WishListItem.class);
                                myItems.add(wishListItem);
                            }
                            firestoreCallback.onCallback((myItems));
                        }
                        else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    //https://stackoverflow.com/questions/48499310/how-to-return-a-documentsnapshot-as-a-result-of-a-method/48500679#48500679
    public interface FirestoreCallback {
        void onCallback(ArrayList<WishListItem> myList);
    }
}

