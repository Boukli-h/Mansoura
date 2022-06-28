package com.example.mansoura.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mansoura.Model.Users;
import com.example.mansoura.Prevalent.Prevalent;
import com.example.mansoura.R;
import com.example.mansoura.Sellers.SellerHomeActivity;
import com.example.mansoura.Sellers.SellerRegistrationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
private Button joinNowButton, loginBotton;
    private ProgressDialog loadingBar;
    private TextView sellerBegin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinNowButton = (Button) findViewById(R.id.main_join_now_btn);
        loginBotton = (Button) findViewById(R.id.main_login_btn);
        sellerBegin= (TextView) findViewById(R.id.seller_begin);

        loadingBar = new ProgressDialog(this);
// Paper library to remember user phone and password
        Paper.init(this);

        loginBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent= new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        sellerBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent= new Intent(MainActivity.this, SellerRegistrationActivity.class);
                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Intent intent= new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });
        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        if (UserPhoneKey != "" && UserPasswordKey != "")
        {
            if (!TextUtils.isEmpty(UserPhoneKey)&& !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAccess(UserPhoneKey, UserPasswordKey);

                loadingBar.setTitle("Already Logged in");
                loadingBar.setMessage("please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null)
        {
            Intent intent= new Intent(MainActivity.this, SellerHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void AllowAccess(final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phone).exists()) {
                    Users usersData = dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if (usersData.getPhone().equals(phone)) {
                        if (usersData.getPassword().equals(password)) {
                            Toast.makeText(MainActivity.this, "logged in Successfully...", LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            // send the user to the homeActivity
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser= usersData; // set the user data. to avoid apps crush
                            startActivity(intent);
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "password is incorrect", LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Account with this" + phone + "number do not exists.", LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}