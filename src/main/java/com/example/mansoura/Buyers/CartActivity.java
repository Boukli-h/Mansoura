package com.example.mansoura.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mansoura.Model.Cart;
import com.example.mansoura.Model.Products;
import com.example.mansoura.Prevalent.Prevalent;
import com.example.mansoura.R;
import com.example.mansoura.Sellers.SellerAddNewProductActivity;
import com.example.mansoura.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.paging.FirebaseDataSource;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rey.material.widget.ImageView;
import com.squareup.picasso.Picasso;

public class CartActivity extends AppCompatActivity
{   //1 declare recyclerView and databaseRef, then go to onCreate
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
   /* FirebaseDatabase firebaseDatabase;
    private DatabaseReference cartListRef;*/

    private Button NextProcessBtn;
    private TextView txtTotalAmount, txtMsg1;

    private double overTotalPrice = 0;
    //private String imageView;
    /*private Uri ImageUri;
    private String productRandomKey;*/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        //2
        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        // or we can write in a single line the 2 forwarded lines
        // recyclerView.setLayoutManager(new LinearLayoutManager(this));
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProcessBtn = (Button) findViewById(R.id.next_process_btn);
        txtTotalAmount= (TextView) findViewById(R.id.total_price);
        txtMsg1= (TextView) findViewById(R.id.msg1);
        // at this time, I created cart_item_layout.xml
        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Intent intent= new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });
    }
    //3 display items in cart list
    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();
        //3.1.1 get information from Cart List inside database firebase
        // we can also write it separatly: FirebaseDatabase= FirebaseDatabase.getInstance();
        // cartListRef= FirebaseDatabase.getReference("Products");
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List");

        //3.1 need to create Cart.java class include informations about the items in the cart
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                        .child(Prevalent.currentOnlineUser.getPhone()).child("Products"), Cart.class)
                .build();
        //3.2 create java class CartViewHolder, represent inf in cart item layout xml
        //4
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            //5.2
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model)
            {
                //Picasso.get().load(model.getImage()).into(holder.imageView);
                holder.txtProductQuantity.setText("Qauntity: "+ model.getQuantity());
                holder.txtProductPrice.setText("Price = $ "+ model.getPrice() + "");
                holder.txtProductName.setText(model.getPname());


                int oneTypeProductTPrice=(Integer.parseInt(model.getPrice().replaceAll("\\D+",""))) * Integer.parseInt(model.getQuantity());
                //double oneTypeProductTPrice=Double.valueOf(model.getPrice()) * Double.valueOf(model.getQuantity());

                overTotalPrice += oneTypeProductTPrice;

                txtTotalAmount.setText("Shopping Cart (Total Price $" + String.valueOf(overTotalPrice) + ")");

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[]= new CharSequence[]
                                {
                                        "Edit",
                                        "Remove"
                                };
                        final AlertDialog.Builder builder= new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options:");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if(i==0)
                                {
                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                if(i== 1)
                                {
                                    cartListRef.child("User View")
                                            .child(Prevalent.currentOnlineUser.getPhone())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(CartActivity.this, "Item removed successfully", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });

                                }
                            }
                        });
                        builder.show();

                    }
                });
            }
            //5.1
            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // 5.1.1 access our cart item layout here
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
//6
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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
                    String userName = datasnapshot.child("name").getValue().toString();

                    if (shippingState.equals("shipped"))
                    {
                        txtTotalAmount.setText("Dear" + userName + "/n order is shipped successfully.");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setText("Congratulation, your order has been shipped successfully, soon you will receive it at your door.");
                        txtMsg1.setVisibility(View.VISIBLE);
                        NextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "you can purchase more product once you receive your final order.", Toast.LENGTH_SHORT).show();
                    }
                    else if (shippingState.equals("not shipped"))
                    {
                        txtTotalAmount.setText("Shipping State = Not Shipped.");
                        recyclerView.setVisibility(View.GONE);

                       // txtMsg1.setVisibility(View.VISIBLE);
                        NextProcessBtn.setVisibility(View.VISIBLE);

                        //Toast.makeText(CartActivity.this, "you can purchase more product once you receive your first one.", Toast.LENGTH_SHORT).show();

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