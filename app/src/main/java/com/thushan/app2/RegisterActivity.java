package com.thushan.app2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtuname, txtpass, txtemail;
    private TextView txtlogin;
    private Button btnReg, btnLogout, btnUpload, btnFB;
    private ImageView propic;

    FirebaseAuth auth;
    DatabaseReference ref;
    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;

    ProgressDialog progressDialog;

    private static final int gallary_code = 1;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );

        mDatabase = FirebaseDatabase.getInstance();
        ref = mDatabase.getReference().child( "ChatUsers" );
        mStorage = FirebaseStorage.getInstance();

        txtuname = findViewById( R.id.txtuname );
        txtpass = findViewById( R.id.txtpass );
        txtemail = findViewById( R.id.txtemail );
        txtlogin = findViewById( R.id.txtLogin );
        propic = findViewById( R.id.img_reg );

        btnUpload = findViewById( R.id.btnUImage );

        btnUpload.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult( Intent.createChooser( intent, "Select Image from here..."), gallary_code);
            }
        } );

        btnReg = findViewById( R.id.btnReg );

        auth = FirebaseAuth.getInstance();

        txtlogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( RegisterActivity.this, LoginActivity.class );
                startActivity( i );
                finish();
            }
        } );

    }

    private void registerUser(final String username, String email, String password)
    {
        auth.createUserWithEmailAndPassword( email, password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    String time1 = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                    String date1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userid = firebaseUser.getUid();

                    ref = FirebaseDatabase.getInstance().getReference("ChatUsers").child( userid );

                    HashMap<String, String> hashmap = new HashMap<>();
                    hashmap.put( "id", userid );
                    hashmap.put( "username", username );
                    hashmap.put( "imageURL", "default" );
                    hashmap.put( "status", "online" );
                    hashmap.put( "lastseen", date1 + " " + time1 );

                    ref.setValue( hashmap ).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Intent i = new Intent( RegisterActivity.this, LoginActivity.class );
                                i.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                                startActivity( i );
                                finish();
                            }
                        }
                    } );
                }
                else
                {
                    Toast.makeText( RegisterActivity.this, "Invalid email or password", Toast.LENGTH_LONG ).show();
                }
            }
        } );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == gallary_code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap( getContentResolver(), imageUri );
                propic.setImageBitmap( bitmap );
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        btnReg.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user = txtuname.getText().toString();
                final String email = txtemail.getText().toString();
                final String pass = txtpass.getText().toString();

                if (TextUtils.isEmpty( user ) || TextUtils.isEmpty( email ) || TextUtils.isEmpty( pass )) {
                    Toast.makeText( RegisterActivity.this, "Please fill these fields ", Toast.LENGTH_LONG ).show();
                }
                else
                    {

                        auth.createUserWithEmailAndPassword( email, pass ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                    final String userid = firebaseUser.getUid();

                                    //Adding timestamp
                                    Long tsLong = System.currentTimeMillis()/1000;
                                    String ts = tsLong.toString();

                                    StorageReference filepath = mStorage.getReference().child( "ChatUsers" ).child( ts + imageUri.getLastPathSegment() );
                                    filepath.putFile( imageUri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener( new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {

                                                    ref = FirebaseDatabase.getInstance().getReference();

                                                    ref.child( "ChatUsers" ).child( userid ).child( "id" ).setValue( userid );
                                                    ref.child( "ChatUsers" ).child( userid ).child( "username" ).setValue( user );
                                                    ref.child( "ChatUsers" ).child( userid ).child( "status" ).setValue( "offline" );
                                                    ref.child( "ChatUsers" ).child( userid ).child( "imageURL" ).setValue( task.getResult().toString() );

                                                    Toast.makeText( RegisterActivity.this, "Record inserted successfully", Toast.LENGTH_SHORT ).show();

                                                }
                                            } );
                                        }
                                    } );

                                } else {
                                    Toast.makeText( RegisterActivity.this, "Invalid email or password", Toast.LENGTH_LONG ).show();
                                }
                            }
                        } );


                }
            }
        } );

    }


}