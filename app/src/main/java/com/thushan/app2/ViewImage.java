package com.thushan.app2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.thushan.app2.Adapter.MessageAdapter;

public class ViewImage extends AppCompatActivity {

    private ImageButton back;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_image );

        Intent k = getIntent();
        String imgSrc = k.getStringExtra("imgSrc");

        photo = findViewById( R.id.view_photo );
        back = findViewById( R.id.btn_back2 );

        Picasso.get().load(imgSrc ).into( photo );

        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( ViewImage.this, MessageAdapter.class );
                finish();
            }
        } );
    }
}