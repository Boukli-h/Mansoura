package com.example.mansoura.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mansoura.Interface.ItemClickListener;
import com.example.mansoura.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName, txtProductDescription, txtProductPrice, txtProductQuantity; // according to their name in product_items_layout
    public ImageView imageView;
    // create package interface, inside it a file ItemClickListener
    public ItemClickListener listner;// to access the interface


    public ProductViewHolder(View itemView)
    {
        super(itemView);

/* create link between layout and imageView
* we use itemView before findById because we are not inside an activity */
        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.product_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.product_price);
        txtProductQuantity = (TextView)itemView.findViewById(R.id.cart_product_quantity);
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