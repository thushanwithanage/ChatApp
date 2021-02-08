package com.thushan.app2.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thushan.app2.Adapter.UserAdapter;
import com.thushan.app2.Model.ChatList;
import com.thushan.app2.Model.Users;
import com.thushan.app2.R;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private UserAdapter userAdapter;
    private List<Users> mUsers;
    private List<ChatList> usersList;
    private RecyclerView recyclerView;

    FirebaseUser firebaseUser;
    DatabaseReference ref;

    public ChatsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate( R.layout.fragment_chats, container, false );

        recyclerView = view.findViewById( R.id.recycler_view2 );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference("ChatList").child( firebaseUser.getUid() );

        ref.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatList chatList = dataSnapshot.getValue(ChatList.class);
                    usersList.add( chatList );

                }
                getchatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

        return view;
    }

    private void getchatList() {
        mUsers = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference("ChatUsers");
        ref.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Users user = dataSnapshot.getValue(Users.class);
                    for(ChatList chatList : usersList)
                    {
                        if(user.getId().equals( chatList.getId() ))
                        {
                            mUsers.add( user );
                        }
                    }

                }

                userAdapter = new UserAdapter( getContext(), mUsers, true );
                recyclerView.setAdapter( userAdapter );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }
}