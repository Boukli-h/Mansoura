package com.example.mansoura.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mansoura.Interface.ItemClickListener;
import com.example.mansoura.R;


public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{ // past the code from productViewHolder. and add the txtProductStatus to it
    public TextView txtProductName, txtProductDescription, txtProductPrice, txtProductStatus; // according to their name in product_items_layout
    public ImageView imageView;
    public ItemClickListener listner;// to access the interface


    public ItemViewHolder(View itemView)
    {
        super(itemView);

// create link between layout and imageView
        imageView = (ImageView) itemView.findViewById(R.id.product_seller_image);
        txtProductName = (TextView) itemView.findViewById(R.id.seller_product_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_seller_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.product_seller_price);
        txtProductStatus = (TextView) itemView.findViewById(R.id.seller_product_state);
    }

    public void setItemClickListner(ItemClickListener listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onClick(view, getAdapterPosition(), false);
    }
}