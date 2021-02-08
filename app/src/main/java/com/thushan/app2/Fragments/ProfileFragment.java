package com.thushan.app2.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.thushan.app2.Model.Users;
import com.thushan.app2.R;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment{

    private TextView username;
    private ImageView propic;
    DatabaseReference ref;
    StorageReference storageRef;
    FirebaseUser firebaseUser;

    private static final int image_request = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    public ProfileFragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate( R.layout.fragment_profile, container, false );

        username = view.findViewById( R.id.usernamer );
        propic = view.findViewById( R.id.profile_image2 );

        storageRef = FirebaseStorage.getInstance().getReference("ChatUsers");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        ref = FirebaseDatabase.getInstance().getReference("ChatUsers").child( firebaseUser.getUid() );

        ref.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Users user1 = snapshot.getValue(Users.class);
                username.setText( user1.getUsername() );

                if(user1.getImageURL().equals( "default" ))
                {
                    propic.setImageResource( R.mipmap.ic_launcher );
                }
                else
                {
                    //Picasso.get().load( user1.getImageURL() ).into( propic );
                    Picasso.get().load(user1.getImageURL())
                            .resize(100, 100)
                            .into(propic, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Bitmap imageBitmap = ((BitmapDrawable) propic.getDrawable()).getBitmap();
                                    RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(container.getResources(), imageBitmap);
                                    imageDrawable.setCircular(true);
                                    imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                    propic.setImageDrawable(imageDrawable);
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
        
        propic.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        } );

        setHasOptionsMenu(true);
        return view;
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser( intent, "Select Image from here..."), image_request);
    }

    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog( getContext() );
        progressDialog.setMessage( "Uploading" );
        progressDialog.show();

        if(imageUri != null)
        {

            final StorageReference fileRef = storageRef.child( imageUri.getLastPathSegment() );

            uploadTask = fileRef.putFile( imageUri );
            uploadTask.continueWithTask( new Continuation <UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            } ).addOnCompleteListener( new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        ref = FirebaseDatabase.getInstance().getReference("ChatUsers").child( firebaseUser.getUid() );

                        HashMap<String, Object> map = new HashMap<>(  );
                        map.put( "imageURL", mUri );
                        ref.updateChildren( map );

                        progressDialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG);
                    }
                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG);
                    progressDialog.dismiss();
                }
            } );

        }
        else
        {
            Toast.makeText(getContext(), "Select an image", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if(requestCode == image_request && resultCode == RESULT_OK && data != null && data.getData() != null )
        {
            imageUri = data.getData();

            if(uploadTask != null && uploadTask.isInProgress())
            {
                Toast.makeText( getContext(), "Upload in progres..", Toast.LENGTH_LONG );
            }
            else
            {
                uploadImage();
            }

        }
    }
}