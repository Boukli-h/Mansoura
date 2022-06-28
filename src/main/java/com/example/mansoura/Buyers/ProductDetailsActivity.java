package com.example.mansoura.Buyers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.mansoura.Model.Products;
import com.example.mansoura.Prevalent.Prevalent;
import com.example.mansoura.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rey.material.widget.Button;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {
    private Button addToCartButton;//5.0
    private Button buyNowButton;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice, productDescription, productName;
    private String productID = "", state = "Normal", Description;

    //delete
    /*private Uri ImageUri;
    private StorageReference ProductImagesRef;
    private String myUrl = "";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
//to receive the product ID
        productID = getIntent().getStringExtra("pid");

        addToCartButton= (Button) findViewById(R.id.pd_add_to_cart_button);          //5.1
        buyNowButton= (Button) findViewById(R.id.pd_buy_now_button);
        numberButton = (ElegantNumberButton) findViewById(R.id.number_btn);
        productImage = (ImageView) findViewById(R.id.product_image_details);
        productName = (TextView) findViewById(R.id.product_name_details);
        productDescription = (TextView) findViewById(R.id.product_description_details);
        productPrice = (TextView) findViewById(R.id.product_price_details);

        getProductDetails(productID);

        addToCartButton.setOnClickListener(new View.OnClickListener() {//5.2
            @Override
            public void onClick(View view)
            {
                addingToCartList();//5.3 add a method

                if (state.equals("Order Placed")|| state.equals("Order Shipped"))
                {
                    Toast.makeText(ProductDetailsActivity.this, "you can add new product once yours get shipped or confirmed", Toast.LENGTH_LONG).show();
                }
                else
                {
                    addingToCartList();
                }
            }
        });

        buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyingItem();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();

    }

    private void addingToCartList()
    {// 5.4 add a time in which a user is purchasing the item
        itemInformation();
        Toast.makeText(ProductDetailsActivity.this, "Added to Cart List", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void buyingItem()
    {// 5.4 add a time in which a user is purchasing the item
        itemInformation();
        Intent intent = new Intent(ProductDetailsActivity.this, ConfirmFinalOrderActivity.class);
        startActivity(intent);
    }

    private void itemInformation()
    {// 5.4 add a time in which a user is purchasing the item
        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate= Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a"); // a stand for am
        saveCurrentTime = currentTime.format(calForDate.getTime());
        productID = saveCurrentDate + saveCurrentTime;




        // then store everything in the database
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();// store data

        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", numberButton.getNumber());
        cartMap.put("discount", "");
        //cartMap.put("description", productDescription);


        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                    .child("Products").child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void getProductDetails(String productID)
    {
        DatabaseReference productsRef= FirebaseDatabase.getInstance().getReference().child("Products");

        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot)
            {
                if (datasnapshot.exists())
                {

                    Products products = datasnapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productPrice.setText(" $ " +products.getPrice());
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CheckOrderState()
    {
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot)
            {
                if(datasnapshot.exists())
                {
                    String shippingState = datasnapshot.child("state").getValue().toString();

                    if (shippingState.equals("shipped"))
                    {
                        state = "Order shipped";
                    }
                    else if (shippingState.equals("not shipped"))
                    {
                        state = "Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
}