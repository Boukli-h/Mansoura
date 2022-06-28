package com.example.mansoura.Buyers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mansoura.Admin.AdminMaintainProductsActivity;
import com.example.mansoura.Model.Products;
import com.example.mansoura.Prevalent.Prevalent;
import com.example.mansoura.R;
import com.example.mansoura.Sellers.SellerProductCategoryActivity;
import com.example.mansoura.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView; // according to content_home.xml
    RecyclerView.LayoutManager layoutManager;

    private String type = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle!= null)
        {// receive the intent
            type = getIntent().getExtras().get("Admin").toString();
        }

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        Paper.init(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Mansoura");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                {
                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
/* Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.*/
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

            userNameTextView.setText(Prevalent.currentOnlineUser.getName());
            Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);


        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef.orderByChild("productState").equalTo("Approved"), Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position,
                                                    @NonNull final Products model)
                    {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Price = $ " + model.getPrice());
                        Picasso.get().load(model.getImage()).into(holder.imageView);
//get a unique ID for each product, by setting a clickListener on itemView
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                if(type.equals("Admin"))
                                {
                                    Intent intent = new Intent(HomeActivity.this, AdminMaintainProductsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                else
                                {
                                    Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                            }
                        });
                    }
//onCreateViewHolder create an instance of a viewHolder class for every item inside a recyclerView
                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
/* create view object and generate layoutInflater, which need a context.
*we use the ViewGroup to get the context.
* inflate method contain should 3 arguments inside; the item_layout, the ViewGroup named parent, and a boolean
* ViewGroup is a parent of all layouts exp: (Relative, Constraint, and Linear)
* it can be used to group different views inside it
*/
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.
                                product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this add items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem item = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected (MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart)
        {    if (!type.equals("Admin"))
        {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        }
        }
        else if (id == R.id.nav_search)
        {   if (!type.equals("Admin"))
        {
            Intent intent = new Intent(HomeActivity.this, SearchProductsActivity.class);
            startActivity(intent);
        }
        }
        else if (id == R.id.nav_categories)
        {
            Intent intent = new Intent(HomeActivity.this, ProductByCategoryActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_settings)
        {   if (!type.equals("Admin"))
        {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        } else if (id == R.id.nav_logout)
        {   if (!type.equals("Admin"))
        {
            Paper.book().destroy();

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
