package root.tostre.com.wwiki;

import android.graphics.Bitmap;

public class Article {

    private String title = "Welcome!";
    private String extract = "Try searching for an article in the upper right!";
    private String url = "";
    private Bitmap image = null;
    private String imgUrl = "";





    public Article(){

    }


    public String getTitle() {
        return title;
    }

    public String getExtract() {

        return extract;
    }

    public String getUrl() {
        return url;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setExtract(String extract) {
        this.extract = extract;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
