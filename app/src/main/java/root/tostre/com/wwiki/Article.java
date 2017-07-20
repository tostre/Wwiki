package root.tostre.com.wwiki;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable {

    private String title = "Welcome!";
    private String extract = "Try searching for an article in the upper right!";
    private String url = "";
    private Bitmap image = null;
    private String imgUrl = "";
    private String html = "";

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

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }



    protected Article(Parcel in) {
        title = in.readString();
        extract = in.readString();
        url = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
        imgUrl = in.readString();
        html = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(extract);
        dest.writeString(url);
        dest.writeParcelable(image, flags);
        dest.writeString(imgUrl);
        dest.writeString(html);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
