package com.tabian.firebaseuploadimage;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by User on 2/13/2017.
 */

public class UploadActivity extends AppCompatActivity {

    private static final String TAG = "UploadActivity";

    //declare variables
    private ImageView image;
    private EditText imageName;
    private Button btnUpload,btnNext,btnBack;
    private ProgressDialog mProgressDialog;

    private final static int mWidth = 512;
    private final static int mLength = 512;

    private ArrayList<String> pathArray;
    private int array_position;

    private StorageReference mStorageRef;
    private FirebaseAuth auth;

    public UploadActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_layout);
        image = (ImageView) findViewById(R.id.uploadImage);
        btnBack = (Button) findViewById(R.id.btnBackImage);
        btnNext = (Button) findViewById(R.id.btnNextImage);
        btnUpload = (Button) findViewById(R.id.btnUploadImage);
        imageName = (EditText) findViewById(R.id.imageName);
        pathArray = new ArrayList<>();
        mProgressDialog = new ProgressDialog(UploadActivity.this);
        auth = FirebaseAuth.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        checkFilePermissions();

        addFilePaths();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(array_position > 0){
                    Log.d(TAG, "onClick: Back an Image.");
                    array_position = array_position - 1;
                    loadImageFromStorage();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(array_position < pathArray.size() - 1){
                    Log.d(TAG, "onClick: Next Image.");
                    array_position = array_position + 1;
                    loadImageFromStorage();
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Uploading Image.");
                mProgressDialog.setMessage("Uploading Image...");
                mProgressDialog.show();

                //get the signed in user
                FirebaseUser user = auth.getCurrentUser();
                String userID = user.getUid();

                String name = imageName.getText().toString();
                if(!name.equals("")){
                    Uri uri = Uri.fromFile(new File(pathArray.get(array_position)));
                    StorageReference storageReference = mStorageRef.child("images/users/" + userID + "/" + name + ".jpg");
                    storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            toastMessage("Upload Success");
                            mProgressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toastMessage("Upload Failed");
                            mProgressDialog.dismiss();
                        }
                    })
                    ;
                }

            }
        });

    }

    /**
     * Add the file paths you want to use into the array
     */
    private void addFilePaths(){
        Log.d(TAG, "addFilePaths: Adding file paths.");
        String path = System.getenv("EXTERNAL_STORAGE");
        pathArray.add(path+"/Pictures/Portal/image1.jpg");
        pathArray.add(path+"/Pictures/Portal/image2.jpg");
        pathArray.add(path+"/Pictures/Portal/image3.jpg");
        loadImageFromStorage();
    }

    private void loadImageFromStorage()
    {
        try{
            String path = pathArray.get(array_position);
            File f=new File(path, "");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            image.setImageBitmap(b);
        }catch (FileNotFoundException e){
            Log.e(TAG, "loadImageFromStorage: FileNotFoundException: " + e.getMessage() );
        }

    }

    private void checkFilePermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = UploadActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += UploadActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}


























