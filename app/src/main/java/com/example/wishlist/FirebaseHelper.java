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
    public final String TAG = "Denna";
    private static String uid = null;            // var will be updated for currently signed in user

    // Create the reference to FirebaseAuth and FirebaseFirestore that will allow us to access
    // the current user and their data anywhere through a variable of this class
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<WishListItem> myItems = new ArrayList<>();

    public FirebaseHelper() {
        // get a reference to or the instance of the auth and firestore elements
        // these lines of code establish the connections to the auth and database we are linked to
        // based on the json file
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void attachReadDataToUser() {
        // this method will help us avoid the issues we saw in the video with aysnch method calls
        // first verify there is a logged in user, and if there is, set them up to read data
        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getUid();       // we should have already updated this in MainActivity
            readData(new FirestoreCallback() {
                @Override
                public void onCallback(ArrayList<WishListItem> myList) {
                    Log.i(TAG, "Inside attachReadDataToUser, onCallBack" + myList.toString());
                }
            });
        }
    }

    public void addUserToFirestore(String name, String newUID) {
        // This will add a document with the uid of the current user to the to collection called "users"
        // For this we will create a Hash map since there are only two fields - a name and the uid value

        // the docID of the document we are adding will be = the uid of the current user
        // similar to how I said "we are making a new folder for this user"
        Map<String, Object> user = new HashMap<>();

        // put data into my object using a key value pair where I label each item I put in the Map
        // the key "name" is the key that is used to label the data in firestore
        // the parameter value of name is passed in to be the value assigned to name in firestore
        user.put("name", name);

        // this will create a new document in the collection "users" and assign it a docID that
        // is = to newID
        db.collection("users").document(newUID)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(TAG, name + "'s user account added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error adding user account", e);
                    }
                });

    }

    public void addData(WishListItem wish) {
        // add a WishListItem to the data
        // This method will be overloaded and the other method will incorporate the interface to
        // handle asynch calls for reading data to keep myItems AL up to date

        addData(wish, new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<WishListItem> myList) {
                Log.i(TAG, "Inside addData, finished: " + myList.toString());
            }
        });
    }

    // This method will do the actual work of adding the WishListItem to database
    private void addData(WishListItem w, FirestoreCallback firestoreCallback) {
        db.collection("users").document(uid).collection("myWishList")
                .add(w)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // we are going to update the document we JUST added by
                        // editing the docID instance var so that it knows what
                        // the value is for its docID in firestore

                        // in the onSuccess method, the documentReference parameter
                        // contains a reference to the newly created document.  We
                        // can us this to extract the docID from firestore
                        db.collection("users").document(uid).collection("myWishList")
                                .document(documentReference.getId())
                                .update("docID", documentReference.getId());
                        Log.i(TAG, "just added " + w.getItemName());

                        // If we want the arrayList to be updated NOW, we call
                        // readData.  If we don't care about continuing our work, then
                        // you don't need to call readData

                        // later on, practice experiment with commenting this line out, see
                        // how it is different
                        readData(firestoreCallback);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error adding element", e);
                    }
                });
    }


    public ArrayList<WishListItem> getWishListItems() {
        return myItems;
    }

    public void editData(WishListItem w) {
        // edit WishListItem w to the database
        // this method is overloaded and incorporates the interface to handle the asynch calls
        editData(w, new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<WishListItem> myList) {
                Log.i(TAG, "Inside editData, onCallback " + myList.toString());
            }
        });
    }

    private void editData(WishListItem w, FirestoreCallback firestoreCallback) {
        String docId = w.getDocID();
        db.collection("users").document(uid).collection("myWishList")
                .document(docId)
                .set(w)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(TAG, "Success updating document");
                        readData(firestoreCallback);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Error updating document", e);
                    }
                });
    }

    public void deleteData(WishListItem w) {
        // delete item w from database
        // this method is overloaded and incorporates the interface to handle the asynch calls
        deleteData(w, new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<WishListItem> myList) {
                Log.i(TAG, "Inside deleteData, onCallBack" + myList.toString());
            }
        });
    }

    public void deleteData(WishListItem w, FirestoreCallback firestoreCallback) {
        // delete item w from database
        String docId = w.getDocID();
        db.collection("users").document(uid).collection("myWishList")
                .document(docId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(TAG, w.getItemName() + " successfully deleted");
                        readData(firestoreCallback);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Error deleting document", e);
                    }
                });
    }

    public void updateUid(String uid) {

    }

    /* https://www.youtube.com/watch?v=0ofkvm97i0s
    This video is good!!!   Basically he talks about what it means for tasks to be asychronous
    and how you can create an interface and then using that interface pass an object of the interface
    type from a callback method and access it after the callback method.  It also allows you to delay
    certain things from occuring until after the onSuccess is finished.
     */

    private void readData(FirestoreCallback firestoreCallback) {
        myItems.clear();    // clear out the AL so that it can get a fresh copy of the data
        db.collection("users").document(uid).collection("myWishList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // loop through ALL elements in the snapshot and convert them to WishListItems
                            // and then add them to my AL
                            for (DocumentSnapshot doc: task.getResult()) {
                                // processing every resulting document from the query we made
                                // this is a snapshot of the data at this moment in time when we
                                // requested to get the data

                                // toObject allows us to pull the document from firestore and directly
                                // convert into a Java object using the constructor
                                WishListItem w = doc.toObject(WishListItem.class);
                                myItems.add(w);
                            }
                            // still inside the onComplete method, I want to call my onCallback method
                            // so that the interface can do its job and let whoever is waiting know the
                            // asych method is done
                            Log.i(TAG, "Success reading data: " + myItems.toString());
                            firestoreCallback.onCallback(myItems);
                        }
                    }
                });

    }

    //https://stackoverflow.com/questions/48499310/how-to-return-a-documentsnapshot-as-a-result-of-a-method/48500679#48500679
    public interface FirestoreCallback {
        void onCallback(ArrayList<WishListItem> myList);
    }
}

