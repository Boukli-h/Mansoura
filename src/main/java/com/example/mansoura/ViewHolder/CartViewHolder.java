package com.example.mansoura.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mansoura.Interface.ItemClickListener;
import com.example.mansoura.R;
import com.rey.material.widget.ImageView;
import com.squareup.picasso.Picasso;

import io.grpc.Context;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName, txtProductPrice, txtProductQuantity;
   // public ImageView imageView;
    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView)
    {
        super(itemView);

//        imageView = (ImageView) itemView.findViewById(R.id.cart_product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.cart_product_name);
        txtProductPrice = (TextView)itemView.findViewById(R.id.cart_product_price);
        txtProductQuantity = (TextView)itemView.findViewById(R.id.cart_product_quantity);
    }


    @Override
    public void onClick(View view)
    {
     itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }
}
