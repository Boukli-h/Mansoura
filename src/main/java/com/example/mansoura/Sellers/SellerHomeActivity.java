package com.example.mansoura.Sellers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mansoura.Buyers.MainActivity;
import com.example.mansoura.Model.Products;
import com.example.mansoura.R;
import com.example.mansoura.SellerHomeVerifiedProductActivity;
import com.example.mansoura.ViewHolder.ItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SellerHomeActivity extends AppCompatActivity {
    //1.0 COPIED values from AdminCheckNewProductActivity
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;
    private Button verifiedProductBtn;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;

                case R.id.navigation_add:
                    Intent intentCat = new Intent(SellerHomeActivity.this, SellerProductCategoryActivity.class);
                    startActivity(intentCat);
                    return true;

                case R.id.navigation_logout:

                    final FirebaseAuth mAuth;
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();


                    Intent intentMain = new Intent(SellerHomeActivity.this, MainActivity.class);
                    intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentMain);
                    finish();
                    return true;
            }
            return false;
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//1.1 copied the findViewByID and put the appropriate ID
        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        verifiedProductBtn = findViewById(R.id.verified_product_button);
        recyclerView = findViewById(R.id.seller_home_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        verifiedProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent= new Intent(SellerHomeActivity.this, SellerHomeVerifiedProductActivity.class);
                startActivity(intent);
                finish(); //ziada
            }
        });
    }
// 2.0 create OnStart method to retrieve all unverified products to the seller, and allow him to delete and upload other products

    @Override
    protected void onStart()
    {
        super.onStart();
// 2.1 copy and past all the code in OnStart method from AdminCheckNewProductActivity
        // 2.2 change orderByChild from "productState" to "sid".
        // it must be equal to the sid of the seller. so change the "not approved" to Firebase Auth
        //2.3 change productViewHolder to ItemViewHolder
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unverifiedProductsRef.orderByChild("sid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()), Products.class)
                .build();
// retrieve all product from the database
        FirebaseRecyclerAdapter<Products, ItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ItemViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull final Products model) {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductStatus.setText(model.getProductState());
                        holder.txtProductPrice.setText("Price = " + model.getPrice());
                        Picasso.get().load(model.getImage()).into(holder.imageView);
// allow admin to approve products
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // ask the admin whether he accept the product or not
                                final String productID = model.getPid();

                                CharSequence[] options = new CharSequence[]
//pass the button in which the admin will click
                                        {
                                                "Yes",
                                                "No"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(SellerHomeActivity.this);
                                builder.setTitle("Do you want to Delete this Product?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position) {
                                        if(position == 0)
                                        {
                                            deleteProduct(productID);
                                        }
                                        if(position ==1)
                                        {

                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.
                                seller_item_view, parent, false);
                        return new ItemViewHolder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

                    //4.0 create the method
                    private void deleteProduct(String productID) {
//4.1 copied the code from  AdminCheckNewProductActivity and modify it
                        unverifiedProductsRef.child(productID)
                                .removeValue()// modification
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        Toast.makeText(SellerHomeActivity.this, "This item has been deleted successfully.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
    }
