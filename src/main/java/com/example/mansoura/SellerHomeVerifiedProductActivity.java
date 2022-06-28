package com.example.mansoura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mansoura.Admin.AdminMaintainProductsActivity;
import com.example.mansoura.Buyers.HomeActivity;
import com.example.mansoura.Buyers.MainActivity;
import com.example.mansoura.Buyers.ProductDetailsActivity;
import com.example.mansoura.Model.Products;
import com.example.mansoura.Sellers.SellerHomeActivity;
import com.example.mansoura.Sellers.SellerProductCategoryActivity;
import com.example.mansoura.ViewHolder.ItemViewHolder;
import com.example.mansoura.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SellerHomeVerifiedProductActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVerified;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference verifiedProductsRef;
    private Button unverifiedProductBtn;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;

                case R.id.navigation_add:
                    Intent intentCat = new Intent(SellerHomeVerifiedProductActivity.this, SellerProductCategoryActivity.class);
                    startActivity(intentCat);
                    return true;

                case R.id.navigation_logout:

                    final FirebaseAuth mAuth;
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();


                    Intent intentMain = new Intent(SellerHomeVerifiedProductActivity.this, MainActivity.class);
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
        setContentView(R.layout.activity_seller_home_verified_product);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//1.1 copied the findViewByID and put the appropriate ID
        verifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        unverifiedProductBtn = findViewById(R.id.unverified_product_button);
        recyclerViewVerified = findViewById(R.id.seller_home_verified_recyclerview);
        recyclerViewVerified.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewVerified.setLayoutManager(layoutManager);

        unverifiedProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent= new Intent(SellerHomeVerifiedProductActivity.this, SellerHomeActivity.class);
                startActivity(intent);
                finish(); //ziada
            }
        });
    }

    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(verifiedProductsRef.orderByChild("productState").equalTo("Approved"), Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ItemViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position,
                                                    @NonNull final Products model)
                    {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductStatus.setText(model.getProductState());
                        holder.txtProductPrice.setText("Price = " + model.getPrice());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {

                                    Intent intent = new Intent(SellerHomeVerifiedProductActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.
                                seller_item_view, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return new ItemViewHolder(view);
                    }
                };
        recyclerViewVerified.setAdapter(adapter);
        adapter.startListening();
    }

}