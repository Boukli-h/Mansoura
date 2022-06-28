package com.example.mansoura.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mansoura.Buyers.HomeActivity;
import com.example.mansoura.Buyers.MainActivity;
import com.example.mansoura.R;

public class AdminHomeActivity extends AppCompatActivity {
    private Button LogoutBtn, CheckOrdersBtn, maintainProductsBtn, checkApprovedOrdersBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

          LogoutBtn = (Button) findViewById(R.id.admin_logout_btn);
        CheckOrdersBtn = (Button) findViewById(R.id.check_orders_btn);
        maintainProductsBtn = (Button) findViewById(R.id.maintain_btn); // setOnClickListener for each button, to send the user somewhere
        checkApprovedOrdersBtn = (Button) findViewById(R.id.check_approved_product_btn);

        maintainProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // send the user to HomeActivity
                Intent intent = new Intent(AdminHomeActivity.this, HomeActivity.class);
                intent.putExtra("Admin", "Admin");
                startActivity(intent);
            }
        });
        //whenever the user click on a button, then we send it to main activity
        LogoutBtn.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
                                                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                  startActivity(intent);
                                                  finish();
                                              }
                                          });
        CheckOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminNewOrdersActivity.class);
                startActivity(intent);

            }
        });
        checkApprovedOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminCheckNewProductsActivity.class);
                startActivity(intent);

            }
        });
    }
}