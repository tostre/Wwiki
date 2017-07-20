package root.tostre.com.wwiki;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by Joern on 15.07.2017.
 */

public class ArticleLoader implements ImageFetcher.ImageFetcherCallback, ArticleFetcher.ArticleFetcherCallback{
    private Article mArticle;
    private ArticleLoaderCallback mCallback;
    private ArticleFetcher mArticleFetcher;
    private ImageFetcher mImageFetcher;

    public ArticleLoader(Context context, ArticleLoaderCallback callback){

        mCallback = callback;
        mArticle = new Article();
        mArticleFetcher = new ArticleFetcher(this);
        mImageFetcher  = new ImageFetcher(context,this);

    }

    public void loadArticle(String articleUrl, String imageUrl){
        mArticleFetcher.execute(articleUrl);
        mImageFetcher.execute(imageUrl);
    }

    @Override
    public void articleTextFetched(String title, String htmlContent) {
        mArticle.setTitle(title);
        mArticle.setHtml(htmlContent);

        if(mArticle.getImage() != null && !mArticle.getHtml().equals(""))
            mCallback.onArticleLoaded(mArticle);

    }

    @Override
    public void imageFetched(Bitmap bitmap) {
        mArticle.setImage(bitmap);

        if(!mArticle.getHtml().equals(""))
            mCallback.onArticleLoaded(mArticle);

    }

    public interface ArticleLoaderCallback{
        abstract void onArticleLoaded(Article article);
    }
}
