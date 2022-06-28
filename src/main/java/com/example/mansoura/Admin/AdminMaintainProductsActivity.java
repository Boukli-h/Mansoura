package com.example.mansoura.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mansoura.R;
import com.example.mansoura.Sellers.SellerProductCategoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity
{
    private Button applyChangeBtn, deleteBtn;
    private EditText name, price, description;
    private ImageView imageView;
    private String productID = ""; //1.0
    private DatabaseReference productsRef; // 2.0 create a reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        productID = getIntent().getStringExtra("pid"); //1.1
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID); //2.1 whith the help of this reference we will retrieve the specific ID

        applyChangeBtn = findViewById(R.id.aply_changes_btn);
        name = findViewById(R.id.product_name_maintain);
        price = findViewById(R.id.product_price_maintain);
        description = findViewById(R.id.product_description_maintain);
        imageView = findViewById(R.id.product_image_maintain);
        deleteBtn = findViewById(R.id.delete_product_btn); //6.0

    // 3.0 create a method
        displaySpecificProductInfo();

        applyChangeBtn.setOnClickListener(new View.OnClickListener() //4.0
        {
            @Override
            public void onClick(View view)
            {
        applyChanges(); //4.1
            }
        });
    deleteBtn.setOnClickListener(new View.OnClickListener() //6.1
    {
        @Override
        public void onClick(View view)
        { // 6.2 create a method
            deleteThisProduct();
        }
    });
    }

    private void deleteThisProduct()
    { // 6.3 remove value from database
        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {// 6.4 tell the admin by a toast msg then send him to admCategoryAct
                Intent intent = new Intent(AdminMaintainProductsActivity.this, SellerProductCategoryActivity.class);
                startActivity(intent);
                finish();

                Toast.makeText(AdminMaintainProductsActivity.this, "The Product is deleted successfully.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyChanges()
    { //4.2
        String pName = name.getText().toString();
        String pPrice = price.getText().toString();
        String pDescription = description.getText().toString();

        if(pName.equals(""))
        {
            Toast.makeText(this, "Write down Product Name", Toast.LENGTH_SHORT).show();
        }
        else if(pPrice.equals(""))
        {
            Toast.makeText(this, "Write down Product Name", Toast.LENGTH_SHORT).show();
        }
        else if(pDescription.equals(""))
        {
            Toast.makeText(this, "Write down Product Name", Toast.LENGTH_SHORT).show();
        }
        else // do the change, I copied the HashMap from AdminAddNewProd
            {
                HashMap<String, Object> productMap = new HashMap<>();
                //Product Id stored in productRandomKey, and so on
                productMap.put("pid", productID);
                productMap.put("description", pDescription);
                productMap.put("price", pPrice);
                productMap.put("pname", pName);

                productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() { //5.0
                    @Override
                    public void onComplete(@NonNull Task<Void> task) { // 5.1 notify the user
                        Toast.makeText(AdminMaintainProductsActivity.this, "Changes Applied Successfully.", Toast.LENGTH_SHORT).show();
                // 5.2 send the user to
                        Intent intent = new Intent(AdminMaintainProductsActivity.this, SellerProductCategoryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
    }

    private void displaySpecificProductInfo() {
        productsRef.addValueEventListener(new ValueEventListener() //3.1  used to receive events about data changes at a location
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
        if (datasnapshot.exists())
        {   //3.2
            String pName = datasnapshot.child("pname").getValue().toString();
            String pPrice = datasnapshot.child("price").getValue().toString();
            String pDescription = datasnapshot.child("description").getValue().toString();
            String pImage = datasnapshot.child("image").getValue().toString();
            //3.3
            name.setText(pName);
            price.setText(pPrice);
            description.setText(pDescription);
            Picasso.get().load(pImage).into(imageView);

        }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}