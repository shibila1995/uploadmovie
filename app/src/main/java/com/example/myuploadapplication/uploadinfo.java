package com.example.myuploadapplication;

public class uploadinfo {
    public String imageName;
    public String imageURL;
    public  String description;
//need to declare spinner



    public uploadinfo(){}

    public uploadinfo(String name, String url,String description) {
        this.imageName = name;
        this.imageURL = url;
        this.description=description;

    }

    public String getImageName() {
        return imageName;
    }
    public String getImageURL() {
        return imageURL;
    }
    public String getDescription() {
        return description;
    }
}
