package com.example.mansoura.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mansoura.Buyers.MainActivity;
import com.example.mansoura.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SellerRegistrationActivity extends AppCompatActivity {//1.0
    private Button sellerLoginBegin;
    private EditText nameInput, phoneInput, emailInput,passwordInput, addressInput;
    private Button registerButton;
    private FirebaseAuth mAuth; //4.0 should add dependency for it
    private ProgressDialog loadingBar; //5.0
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);
//4.1
        mAuth = FirebaseAuth.getInstance();
//5.1
        loadingBar = new ProgressDialog(this);

//2.0
        sellerLoginBegin = findViewById(R.id.seller_already_have_account_btn);
        nameInput= findViewById(R.id.seller_name);
        phoneInput= findViewById(R.id.seller_phone);
        emailInput= findViewById(R.id.seller_email);
        passwordInput= findViewById(R.id.seller_password);
        addressInput= findViewById(R.id.seller_address);
        registerButton= findViewById(R.id.seller_register_btn);

        sellerLoginBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent= new Intent(SellerRegistrationActivity.this, SellerLoginActivity.class);
                startActivity(intent);
                finish(); //ziada
            }
        });
    //3.0 when the seller clique on register button
    registerButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {//3.2 call a method
    registerSeller();
        }
    });
    }
    //3.1 create a method
    private void registerSeller(){
        final String name = nameInput.getText().toString();
        final String phone = phoneInput.getText().toString();
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();
        final String address = addressInput.getText().toString();
//3.2 condition
        if(!name.equals("") && !phone.equals("")&& !email.equals("") && !password.equals("") && !address.equals(""))
        {  //5.3
            loadingBar.setTitle("Creating Seller Account");
            loadingBar.setMessage("please wait, while we are checking the credentials");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);
//4.2
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {//4.3 always test the task if it's successful, then in this case; save the informations inside the fbdbase
            if(task.isSuccessful())
            {//4.3.1 first, create a database reference to it
            final DatabaseReference rootRef;
            rootRef = FirebaseDatabase.getInstance().getReference();
            //4.3.2 create a unique Id
            String sid = mAuth.getCurrentUser().getUid();
            //4.3.3 create a hashMap table
                HashMap<String, Object> sellerMap = new HashMap<>(); // give it a name sellerMap
                sellerMap.put("sid", sid);// give the name sid to seller Id first, and past the uid to it
                sellerMap.put("password", password);
                sellerMap.put("phone", phone);
                sellerMap.put("email", email);
                sellerMap.put("address", address);
                sellerMap.put("name", name);
            //4.4 create a separate child seller
            rootRef.child("Sellers").child(sid).updateChildren(sellerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    loadingBar.dismiss();
                    Toast.makeText(SellerRegistrationActivity.this, "you are Registered Successfully.", Toast.LENGTH_SHORT).show();

                   Intent intent = new Intent(SellerRegistrationActivity.this, SellerHomeActivity.class);
                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
            });
            }
            }
        });
        }
        else
        {
            Toast.makeText(this, "Please, complete the registration form.", Toast.LENGTH_SHORT).show();
        }
    }

}