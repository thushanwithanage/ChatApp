package com.thushan.app2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thushan.app2.Adapter.MessageAdapter;
import com.thushan.app2.Model.Chat;
import com.thushan.app2.Model.Users;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MessageActivity extends AppCompatActivity {

    private TextView txtun, txtls;
    private ImageView imgpro;
    private ImageButton btnSend, btnImage;
    private EditText message;
    private RecyclerView recyclerView;

    FirebaseUser firebaseUser;
    DatabaseReference ref;
    FirebaseStorage mStorage;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    String userid;

    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_message );

        imgpro = findViewById( R.id.propic1 );
        txtun = findViewById( R.id.txtun2 );
        btnSend = findViewById( R.id.btn_send );
        btnImage = findViewById( R.id.btn_image1 );
        message = findViewById( R.id.text_send );
        txtls = findViewById( R.id.txt_ls2 );

        recyclerView = findViewById( R.id.recycler_view );
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getApplicationContext() );
        linearLayoutManager.setStackFromEnd( true );
        recyclerView.setLayoutManager( linearLayoutManager );

        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("ChatUsers").child( userid );

        mStorage = FirebaseStorage.getInstance();

        ref.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Users user1 = snapshot.getValue(Users.class);
                txtun.setText( "     " + user1.getUsername() );

                if(user1.getStatus().equals( "online" ))
                {
                    txtls.setText( "\n\n\n\t\t\t\t\t\t\tOnline" );
                }
                else if(user1.getStatus().equals( "offline" ))
                {
                    txtls.setText( "\n\n\n\t\t\t\t\t\t\t" + user1.getLastseen() );
                }


                if(user1.getImageURL().equals( "default" ))
                {
                    imgpro.setImageResource( R.mipmap.ic_launcher );
                }
                else
                {
                    Picasso.get().load( user1.getImageURL() ).into( imgpro );
                }

                readMessage(firebaseUser.getUid(), userid, user1.getImageURL());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

        btnSend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message.getText().toString();
                if(!msg.equals( "" ))
                {
                    sendMessage(firebaseUser.getUid(), userid, msg);
                    message.setText("");
                }
            }
        } );

        btnImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent( MessageActivity.this, SendImage.class );
                i.putExtra( "sender", firebaseUser.getUid() );
                i.putExtra( "receiver", userid );
                startActivity( i );
                finish();

                message.setText("");
            }
        } );

        seenMessage( userid );
    }

    private void seenMessage(String userId)
    {
        ref = FirebaseDatabase.getInstance().getReference("ChatMsg");

        seenListener = ref.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue( Chat.class );

                    if(chat.getReceiver().equals( firebaseUser.getUid() ) && chat.getSender().equals( userid ))
                    {
                        HashMap<String, Object> hashMap = new HashMap<>(  );
                        hashMap.put( "isseen", true );

                        dataSnapshot.getRef().updateChildren( hashMap );
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void sendMessage(String sender, String receiver, String msg)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ChatMsg");

        String msgTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String msgDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        HashMap<String, Object> hashmap = new HashMap<>();

        hashmap.put( "sender", sender );
        hashmap.put( "receiver", receiver );
        hashmap.put( "message", msg );
        hashmap.put( "isseen", false );
        hashmap.put( "delSender", false );
        hashmap.put( "delReceiver", false );
        hashmap.put( "msgType", "text" );
        hashmap.put( "msgDate", msgDate );
        hashmap.put( "msgTime", msgTime );
        hashmap.put( "msgId", ref.push().getKey() );

        ref.push().setValue( hashmap );

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList").child( firebaseUser.getUid() ).child( userid );

        chatRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    chatRef.child( "id" ).setValue( userid );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void readMessage(final String myid, final String userid, final String imageurl)
    {
        mChat = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference("ChatMsg");

        ref.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    assert chat != null;

                    if((chat.getReceiver().equals( myid ) && chat.getSender().equals( userid ) && !chat.isDelReceiver() ||
                            (chat.getReceiver().equals( userid ) && chat.getSender().equals( myid ) && !chat.isDelSender()) ))
                    {
                        mChat.add( chat );
                    }

                    messageAdapter = new MessageAdapter( MessageActivity.this, mChat, imageurl );
                    recyclerView.setAdapter( messageAdapter );

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void checkStatus(String status)
    {
        String time1 = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String date1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        ref = FirebaseDatabase.getInstance().getReference("ChatUsers").child( firebaseUser.getUid() );
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put( "status", status );
        hashMap.put( "lastseen", date1 + " " + time1 );
        ref.updateChildren( hashMap );
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkStatus( "online" );
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkStatus( "online" );
    }

    @Override
    protected void onPause() {
        super.onPause();
        ref.removeEventListener( seenListener );
        checkStatus( "offline" );
    }
}