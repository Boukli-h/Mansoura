package com.example.mansoura.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.rey.material.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mansoura.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.paperdb.Paper;

import static android.text.TextUtils.isEmpty;
import static android.widget.Toast.LENGTH_SHORT;

public class RegisterActivity extends AppCompatActivity {
    private Button CreatAccountButton;
    private EditText InputName, InputPhoneNumber, InputPassword, confirmInputPassword;
    private TextView license, txtWarnPassword;
    private CheckBox agreement;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreatAccountButton= (Button) findViewById(R.id.register_btn);
        InputName= (EditText) findViewById(R.id.register_username_input);
        InputPassword= (EditText) findViewById(R.id.register_password_input);
        confirmInputPassword= (EditText) findViewById(R.id.confirm_password_input);
        InputPhoneNumber= (EditText) findViewById(R.id.register_phone_number_input);
        license = (TextView) findViewById(R.id.license_agreement);
        txtWarnPassword = (TextView) findViewById(R.id.password_not_match);
        agreement = (CheckBox) findViewById(R.id.agreement);

        loadingBar = new ProgressDialog(this);

        CreatAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateAccount();
            }
        });
    }

    private void CreateAccount()
    {
        String name = InputName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
        String confirmPassword = confirmInputPassword.getText().toString();
        if (isEmpty(name)&&isEmpty(phone)&&isEmpty(password)&&isEmpty(confirmPassword)) {
            Toast.makeText(this, "please fill up the missing fields.", LENGTH_SHORT).show();
        }
            else if (isEmpty(name)) {
//toast contains message to be displayed quickly and disappears after a short time
                Toast.makeText(this, "please write your name.", LENGTH_SHORT).show();
            }
            else if (isEmpty(phone)) {
                Toast.makeText(this, "please write your phone number.", LENGTH_SHORT).show();
            }
            else if (isEmpty(password)) {
                Toast.makeText(this, "please write your password.", LENGTH_SHORT).show();
            }
        else if (password.length()<6) {
            Toast.makeText(this, "Password too short.", LENGTH_SHORT).show();
        }
        else if (isEmpty(confirmPassword))
        {
            Toast.makeText(this, "please confirm your password.", LENGTH_SHORT).show();

        }
        else if (!confirmPassword.equals(password))
        {
            txtWarnPassword.setText("* Passwords doesn't match.");
            txtWarnPassword.setVisibility(View.VISIBLE);
        }
        else if(!agreement.isChecked()){
            Toast.makeText(this, "You need to agree to the license agreement.", LENGTH_SHORT).show();
        }
       else {
           loadingBar.setTitle("Create Account");
           loadingBar.setMessage("please wait, while we are checking the credentials");
           loadingBar.setCanceledOnTouchOutside(false);
           loadingBar.show();

           ValidatePhoneNumber(name, phone, password);
        }

    }

    private void ValidatePhoneNumber(final String name, final String phone, final String password)
    {
    final DatabaseReference RootRef;
    RootRef = FirebaseDatabase.getInstance().getReference();

    RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
            if (!(dataSnapshot.child("Users").child(phone).exists()))
            {
                HashMap<String,Object> userdataMap = new HashMap<>();
                userdataMap.put("phone", phone);
                userdataMap.put("password", password);
                userdataMap.put("name", name);
                RootRef.child("Users").child(phone).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Congratulations, your account has been created.", LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(RegisterActivity.this, "Network Error: Please, try again.", LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else
                {
                    Toast.makeText(RegisterActivity.this, "This" + phone+ "already exists", LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again using another phone number.", LENGTH_SHORT).show();
                    Intent intent= new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }
}