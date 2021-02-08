package com.thushan.app2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.thushan.app2.Model.Users;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SendImage extends AppCompatActivity {

    private static final int gallary_code = 1;
    private ImageButton btnSend, btnBack;
    private ImageView photo, receiverPic;
    private TextView txt_un;
    String senderId;
    String receiverId;
    Uri imageUri;

    FirebaseUser firebaseUser;
    DatabaseReference ref;
    FirebaseStorage mStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_send_image );

        btnSend = findViewById( R.id.send_image_db );
        photo = findViewById( R.id.send_image );
        btnBack = findViewById( R.id.btn_back1 );
        receiverPic = findViewById( R.id.rec_pro_pic );
        txt_un = findViewById( R.id.txt_rec_name );

        mStorage = FirebaseStorage.getInstance();

        Intent intent2 = getIntent();
        senderId = intent2.getStringExtra("sender");
        receiverId = intent2.getStringExtra("receiver");

        openImage();

        btnSend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        } );

        btnBack.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i2 = new Intent( SendImage.this, MessageActivity.class );
                i2.putExtra( "userid",  receiverId);
                startActivity( i2 );
                finish();
            }
        } );

        ref = FirebaseDatabase.getInstance().getReference("ChatUsers").child( receiverId );
        ref.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Users user1 = snapshot.getValue(Users.class);
                txt_un.setVisibility( View.VISIBLE );
                txt_un.setText( "     " + user1.getUsername() );

                if(user1.getImageURL().equals( "default" ))
                {
                    txt_un.setText( "     " + user1.getUsername() );
                }
                else
                {
                    txt_un.setText( "     " + user1.getUsername() );
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        } );


    }

    private void openImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult( Intent.createChooser( intent, "Select Image from here..."), gallary_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == gallary_code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap( getContentResolver(), imageUri );
                photo.setImageBitmap( bitmap );
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage()
    {
        // Adding timestamp
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        StorageReference filepath = mStorage.getReference().child( "ChatImages" ).child( ts + imageUri.getLastPathSegment() );
        filepath.putFile( imageUri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener( new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ChatMsg");

                        String msgTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                        String msgDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        
                        HashMap<String, Object> hashmap = new HashMap<>();

                        hashmap.put( "sender", senderId );
                        hashmap.put( "receiver", receiverId );
                        hashmap.put( "message", task.getResult().toString() );
                        hashmap.put( "isseen", false );
                        hashmap.put( "delSender", false );
                        hashmap.put( "delReceiver", false );
                        hashmap.put( "msgType", "image" );
                        hashmap.put( "msgDate", msgDate );
                        hashmap.put( "msgTime", msgTime );
                        hashmap.put( "msgId", ref.push().getKey() );

                        ref.push().setValue( hashmap );

                        //message.setText("");

                        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList").child( senderId ).child( receiverId );

                        chatRef.addListenerForSingleValueEvent( new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.exists())
                                {
                                    chatRef.child( "id" ).setValue( receiverId );
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        } );
                    }
                } );
            }
        } );
        Intent i = new Intent( SendImage.this, MessageActivity.class );
        i.putExtra( "userid",  receiverId);
        startActivity( i );
        finish();
    }

}