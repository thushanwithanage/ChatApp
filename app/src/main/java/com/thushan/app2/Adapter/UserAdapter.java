package com.thushan.app2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.thushan.app2.MessageActivity;
import com.thushan.app2.Model.Users;
import com.thushan.app2.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements Filterable
{
    private Context context;
    private List<Users> mUsers;
    private List<Users> mUsersAll;
    private boolean isChat;

    public UserAdapter(Context context, List<Users> mUsers, boolean isChat) {
        this.context = context;
        this.mUsers = mUsers;
        this.isChat = isChat;

        this.mUsersAll = new ArrayList<>( mUsers );
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( context ).inflate( R.layout.user_item, parent, false );;
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Users user1 = mUsers.get( position );
        holder.username.setText( user1.getUsername() );

        if(user1.getImageURL().equals( "default" ))
        {
            holder.imageView.setImageResource( R.mipmap.ic_launcher );
        }
        else
        {
            //Picasso.get().load( user1.getImageURL() ).into( holder.imageView );
            Picasso.get().load(user1.getImageURL())
                    .resize(100, 100)
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) (holder.imageView).getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            holder.imageView.setImageDrawable(imageDrawable);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }

        // Status check

        if(isChat)
        {
            if(user1.getStatus().equals( "online" ))
            {
                holder.imageViewOn.setVisibility( View.VISIBLE );
                holder.imageViewOff.setVisibility( View.GONE );
                holder.last_seen.setVisibility( View.GONE );
            }
            else
            {
                holder.imageViewOff.setVisibility( View.VISIBLE );
                holder.imageViewOn.setVisibility( View.GONE );
                holder.last_seen.setVisibility( View.VISIBLE );
                holder.last_seen.setText( user1.getLastseen() );
            }
        }
        else
        {
            holder.imageViewOff.setVisibility( View.GONE );
            holder.imageViewOn.setVisibility( View.GONE );
        }

        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( context, MessageActivity.class );
                i.putExtra( "userid", user1.getId() );
                context.startActivity( i );
            }
        } );
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView username, last_seen;
        public ImageView imageView,imageViewOn,imageViewOff ;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );

            username = itemView.findViewById( R.id.txtun1 );
            imageView = itemView.findViewById( R.id.imgpro1 );
            imageViewOn = itemView.findViewById( R.id.imgStatusOn );
            imageViewOff = itemView.findViewById( R.id.imgStatusOff );
            last_seen = itemView.findViewById( R.id.txt_ls );
        }
    }


    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Users> filteredList = new ArrayList<>();
            if(charSequence.toString().isEmpty())
            {
                filteredList.addAll( mUsersAll );
            }
            else
            {
                for(Users user : mUsersAll)
                {
                    if(user.getUsername().toLowerCase().contains( charSequence.toString().toLowerCase() ))
                    {
                        filteredList.add( user );
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mUsers.clear();
            mUsers.addAll( (Collection<? extends Users>) filterResults.values );
            notifyDataSetChanged();
        }
    };
}
