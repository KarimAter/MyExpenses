package com.karim.ater.myexpenses.Helpers;

import android.app.ProgressDialog;

import android.content.ContextWrapper;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

public class CloudOperations {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private Uri dbUri;
    FragmentActivity activity;
    private String databasePath;
    private String directoryPath;
    // <!-- TODO: Handle all scenarios-->

    public CloudOperations(FragmentActivity activity) {
        this.activity = activity;
        ContextWrapper contextWrapper = new ContextWrapper(activity);
        databasePath = contextWrapper.getDatabasePath("MyExpenses.db").getPath();
        directoryPath = contextWrapper.getDatabasePath("MyExpenses.db").getParentFile().getPath();

    }

    public void syncOnFireBase() {
        storage.setMaxUploadRetryTimeMillis(5000);
        dbUri = Uri.fromFile(new File(databasePath));
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Uploading..");
        progressDialog.setIndeterminate(true);
        progressDialog.setMax(100);
        progressDialog.show();
        String userId = MySharedPrefs.getUserId(activity);
        StorageReference dbRef = storageReference.child("databases/" + userId + "Database.db");
        dbRef.putFile(dbUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(activity, "Done..", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        progressDialog.dismiss();
                        Toast.makeText(activity, "Network error", Toast.LENGTH_LONG).show();

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage((int) progress + "% Uploaded..");
                        progressDialog.setProgress((int) progress);
                    }
                })
        ;
    }

    public void backUpFromFireBase() {
        activity.deleteDatabase(databasePath);
        storage.setMaxDownloadRetryTimeMillis(5000);
        Log.d(activity.getClass().getSimpleName(), "downloadFromFirebase: main database deleted");
        downloadFromFirebase();
    }


    private void downloadFromFirebase() {
        String userId = MySharedPrefs.getUserId(activity);
        File dbFile = dbFile = new File(directoryPath, "MyExpenses.db");
        StorageReference dbRef = storageReference.child("databases/" + userId + "Database.db");
        dbRef.getFile(dbFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...

                        Toast.makeText(activity, "Database loaded successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
                Toast.makeText(activity, "Network error", Toast.LENGTH_LONG).show();
            }
        });
    }
}
