package com.example.mansoura.Sellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mansoura.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SellerAddNewProductActivity extends AppCompatActivity {
    //1-widgets
    private String CategoryName, Description, Price, Pname, saveCurrentDate, saveCurrentTime;
    private Button AddNewProductButton;
    private ImageView InputProductImage;
    private EditText InputProductName, InputProductDescription, InputProductPrice;
    private static final int GalleryPick = 1;
    private Uri ImageUri; /*uniform resource identifier (URI) scheme that provides a way to include
     data in-line in Web pages as if they were external resources*/
    private String productRandomKey, downloadImageUrl;
    //6.1.1 create a folder to add all our products images
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef, sellersRef;
    private ProgressDialog loadingBar;
    private String sName, sAddress, sPhone, sEmail, sID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_new_product);

        CategoryName = getIntent().getExtras().get("category").toString();
//6.1.2 the folder is Product Images
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        sellersRef = FirebaseDatabase.getInstance().getReference().child("Sellers");
//2-
        AddNewProductButton = (Button) findViewById(R.id.add_new_product);
        InputProductImage = (ImageView) findViewById(R.id.select_product_image);
        InputProductName = (EditText) findViewById(R.id.product_name);
        InputProductDescription = (EditText) findViewById(R.id.product_description);
        InputProductPrice = (EditText) findViewById(R.id.product_price);
        loadingBar = new ProgressDialog(this); // initialize the loadingBar
//3-
        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // from here the user select product image. call the methode
                OpenGallery();
            }
        });
//4-
        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });
        sellersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            sName= dataSnapshot.child("name").getValue().toString();
                            sAddress= dataSnapshot.child("address").getValue().toString();
                            sPhone= dataSnapshot.child("phone").getValue().toString();
                            sID= dataSnapshot.child("sid").getValue().toString();
                            sEmail= dataSnapshot.child("email").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    //3.1- create the method to choose picture from gallery
    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }
    //3.2 get a result from a previous activity, it contains 3 param: the request code,
// result code, and data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
// if the result is capturing Image
        if (resultCode == RESULT_OK && requestCode == GalleryPick && data != null) {
            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);
        }
    }
    //4.1
    private void ValidateProductData() {
        Description = InputProductDescription.getText().toString();
        Price = InputProductPrice.getText().toString();
        Pname = InputProductName.getText().toString();

        if (ImageUri == null)
        {
            Toast.makeText(this, "Product image is mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Please write product description...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price))
        {
            Toast.makeText(this, "Please write product Price...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Pname))
        {
            Toast.makeText(this, "Please write product name...", Toast.LENGTH_SHORT).show();
        }
        else
        {
//5-
            StoreProductInformation();
        }
    }
    //5.1
    private void StoreProductInformation()
    {   loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Please wait, while we are adding the new product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
//5.2 creat a calendar to display the time the product was released
        Calendar calendar = Calendar.getInstance();
//5.2.1 get a current date, and past a date format
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
//5.2.2 same thing with time. a: for the am, pm
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());
//5.3 create a unique random key: combination between date and time of each product
        productRandomKey = saveCurrentDate + saveCurrentTime;
//6- store link to imageuri inside firebase storage and display it to the user
//6.1 create storageReference by the name of filePath. link of the product images
        final StorageReference filePath = ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(ImageUri);
// 6.2- if any failure occur
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
// get the error message
            {
//6.2.1 get the error message
                String message = e.toString();
//6.2.2 display it to the seller
                Toast.makeText(SellerAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SellerAddNewProductActivity.this, "Product Image uploaded Successfully...",
                        Toast.LENGTH_SHORT).show();
// get the link of the image and store it inside the firebase database
                Task<Uri>  urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful()) {
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(SellerAddNewProductActivity.this, "got the Product image Url Successfully...", Toast.LENGTH_SHORT).show();
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }
    //A HashMap however, store items in "key/value" pairs, and you can access them by an index of another type
    private void SaveProductInfoToDatabase()
    {
        HashMap<String, Object> productMap = new HashMap<>();
        //Product Id stored in productRandomKey, and so on
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", CategoryName);
        productMap.put("price", Price);
        productMap.put("pname", Pname);
        productMap.put("sellerName", sName);
        productMap.put("sellerAddress", sAddress);
        productMap.put("sellerPhone", sPhone);
        productMap.put("sellerEmail", sEmail);
        productMap.put("sid", sID);
        productMap.put("productState", "Not Approved");
        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(SellerAddNewProductActivity.this, SellerProductCategoryActivity.class);
                            startActivity(intent);
                            loadingBar.dismiss();
                            Toast.makeText(SellerAddNewProductActivity.this, "Product is added successfully..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {   loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(SellerAddNewProductActivity.this, "Error: "
                                    + message, Toast.LENGTH_SHORT).show();
                        }}

                });
    }

    public static class HomeActivity {
    }
}
