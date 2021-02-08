package com.thushan.app2.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thushan.app2.Model.Chat;
import com.thushan.app2.R;
import com.thushan.app2.ViewImage;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>
{
    FirebaseUser firebaseUser;

    private Context context;
    private List<Chat> mChat;
    private String imgURL;

    public static final int msg_type_left = 0;
    public static final int msg_type_right = 1;

    public MessageAdapter(Context context, List<Chat> mChat, String imgURL) {
        this.context = context;
        this.mChat = mChat;
        this.imgURL = imgURL;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == msg_type_right)
        {
            View view = LayoutInflater.from( context ).inflate( R.layout.chat_item_right, parent, false );;
            return new MessageAdapter.ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from( context ).inflate( R.layout.chat_item_left, parent, false );;
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position)
    {
        Chat chat1 = mChat.get( position );

        if(chat1.getMsgType().equals( "text" ))
        {
            holder.profile_image.setVisibility( View.GONE );
            holder.show_message.setVisibility( View.VISIBLE );
            holder.show_message.setText( chat1.getMessage() );

            String time = chat1.getMsgTime();

            if(mChat.get( position ).getSender().equals( firebaseUser.getUid() )) {
                if (chat1.isIsseen()) {
                    holder.txt_seen.setText( time + "  Seen" );
                } else {
                    holder.txt_seen.setText( time + "  Delivered" );
                }
            }
            else {
                holder.txt_seen.setText(time);
            }
        }
        else if(chat1.getMsgType().equals( "image" ))
        {
            holder.profile_image.setVisibility( View.VISIBLE );
            holder.show_message.setVisibility( View.GONE );
            Picasso.get().load( chat1.getMessage() ).into( holder.profile_image );

            String time = chat1.getMsgTime();

            if(mChat.get( position ).getSender().equals( firebaseUser.getUid() )) {
                if (chat1.isIsseen()) {
                    holder.txt_seen.setText( time + "  Seen" );
                } else {
                    holder.txt_seen.setText( time + "  Delivered" );
                }
            }
            else {
                holder.txt_seen.setText( "\n\n\n\n\n\n" + time);
            }
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get( position ).getSender().equals( firebaseUser.getUid() ))
        {
            holder.itemView.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v)
                {
                    notifyDataSetChanged();
                    CharSequence options[] = new CharSequence[]
                            {
                                    "Delete for me", "Cancel", "Delete for everyone"
                            };
                    AlertDialog.Builder builder = new AlertDialog.Builder( holder.itemView.getContext() );
                    builder.setTitle( "Delete message" );

                    builder.setItems( options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            if(i ==0 )
                            {
                                delSentMsg(mChat.get(position).getMsgId());
                            }
                            else if(i == 2)
                            {
                                delMsgAll( mChat.get(position).getMsgId());
                            }
                        }
                    } );
                    builder.show();
                    return false;
                }
            } );
            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(mChat.get( position ).getMsgType().equals( "image" ))
                    {
                        Intent i = new Intent( context, ViewImage.class );
                        i.putExtra( "imgSrc", mChat.get( position ).getMessage() );
                        context.startActivity(i);
                    }

                }
            } );

        }
        else
        {
            holder.itemView.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v)
                {
                    notifyDataSetChanged();
                    CharSequence options[] = new CharSequence[]
                            {
                                    "Delete for me", "Cancel"
                            };
                    AlertDialog.Builder builder = new AlertDialog.Builder( holder.itemView.getContext() );
                    builder.setTitle( "Delete message" );

                    builder.setItems( options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            if(i ==0 )
                            {
                                delRecMsg(mChat.get(position).getMsgId());
                            }
                        }
                    } );
                    builder.show();
                    return false;
                }
            } );
            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(mChat.get( position ).getMsgType().equals( "image" ))
                    {
                        Intent i = new Intent( context, ViewImage.class );
                        i.putExtra( "imgSrc", mChat.get( position ).getMessage() );
                        context.startActivity(i);
                    }

                }
            } );

            }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView show_message, txt_seen;
        public ImageView profile_image;


        public ViewHolder(@NonNull View itemView) {
            super( itemView );

            show_message = itemView.findViewById( R.id.show_message );
            profile_image = itemView.findViewById( R.id.profile_image );
            txt_seen = itemView.findViewById( R.id.txt_seen );
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(mChat.get( position ).getSender().equals( firebaseUser.getUid() ))
        {
            return msg_type_right;
        }
        else
        {
            return msg_type_left;
        }
    }

    private void delMsgAll(final String msgId)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ChatMsg");

        ref.orderByChild( "msgId" ).equalTo( msgId ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void delSentMsg(final String msgId)
    {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("ChatMsg").orderByChild("msgId").equalTo(msgId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = null;

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    key = childSnapshot.getKey();
                }

                ref.child("ChatMsg").child( key ).child( "delSender" ).setValue( true );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void delRecMsg(final String msgId)
    {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("ChatMsg").orderByChild("msgId").equalTo(msgId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = null;

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    key = childSnapshot.getKey();
                }

                ref.child("ChatMsg").child( key ).child( "delReceiver" ).setValue( true );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
