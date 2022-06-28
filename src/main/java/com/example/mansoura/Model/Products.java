package com.example.mansoura.Model;

public class Products
{
    private String pname;
    private String description;
    private String price;
    private String quantity;
    private String image;
    private String category;
    private String pid;
    private String date;
    private String time;
    private String productState;
    // use the same value as the firebase database

    public Products()
    {

    }

    public Products(String pname, String description, String price, String quantity, String image, String category, String pid, String date, String time, String productState) {
        this.pname = pname;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.category = category;
        this.pid = pid;
        this.date = date;
        this.time = time;
        this.productState = productState;

    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProductState() {
        return productState;
    }

    public void setProductState(String productState) {
        this.productState = productState;
    }



}
