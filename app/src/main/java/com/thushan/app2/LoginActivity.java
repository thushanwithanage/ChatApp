package com.thushan.app2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {

    private EditText txtemail, txtpass;
    private TextView txtreg;
    private Button btnLogin;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference ref;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null)
        {
            Intent i = new Intent( LoginActivity.this, MainActivity.class );
            startActivity( i );
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        txtemail = findViewById( R.id.txtemail2 );
        txtpass = findViewById( R.id.txtpass2 );
        txtreg = findViewById( R.id.txtReg );
        btnLogin = findViewById( R.id.btnLogin );

        auth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btnLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtemail.getText().toString();
                String pass = txtpass.getText().toString();

                if(TextUtils.isEmpty( email ) || TextUtils.isEmpty( pass ) )
                {
                    Toast.makeText( LoginActivity.this, "Please fill the fields", Toast.LENGTH_LONG ).show();
                }
                else
                {
                    auth.signInWithEmailAndPassword( email, pass ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                Intent i = new Intent( LoginActivity.this, MainActivity.class );
                                i.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                                startActivity( i );
                                finish();
                            }
                            else
                            {
                                Toast.makeText( LoginActivity.this, "Login failed ", Toast.LENGTH_LONG ).show();
                            }
                        }
                    } );
                }
            }
        } );

        txtreg.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( LoginActivity.this, RegisterActivity.class );
                startActivity( i );
                finish();
            }
        } );


    }
}