package com.wb.nextgenlibrary.data;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.wb.nextgenlibrary.R;

import java.util.List;

/**
 * Created by gzcheng on 4/7/16.
 */
public class TheTakeData{
    public static class
    ShopCategory{
        public int categoryId;
        public String categoryName;
        public List<ShopCategory> childCategories;
        public List<MovieMetaData.ShopItemInterface> products;
    }

    public static class TheTakeProductFrame {
        public FrameImages frameImages;
        public int frameTime;
        public double frameLetterboxRatio;
    }

    public static class FrameImages{
        /*1000pxFrameLink	String	https://img.thetake.com/frame_images/c9994d204a97f30ba94b38887ebdae4068848e64b64660505dd134be44c2bf9a.jpeg
                500pxFrameLink	String	https://img.thetake.com/frame_images/c90d9a64b3ab8d0e1f54a3ca9a801dd0e284f5c40ea603429e8afca11a6ee739.jpeg
                50pxFrameLink	String	https://img.thetake.com/frame_images/bd17430b105f2286bd0c7b04754cd6aea927853d92482f7bd6fb7cf6aa09ffb7.jpeg
        fullSizeFrameLink	String	https://img.thetake.com/frame_images/1f2f2a96e774b50a8563b7d9582c9a76940eaccec42f4d747de7c3547d3870a9.jpeg
                250pxFrameLink	String	https://img.thetake.com/frame_images/d197416f8b5e04ab8b5b2fc53afe843ae09300abc5c41413d176f368f9a330ce.jpeg
                125pxFrameLink	String	https://img.thetake.com/frame_images/fe47367a66b108532eacbb1245d9b13be01b1a6f7c08fda0fd9697e52a40fd9e.jpeg*/
        @SerializedName(value="1000pxLink", alternate={"1000pxFrameLink", "1000pxKeyFrameLink", "1000pxCropLink", "1000pxHeadshotLink"})//"1000pxFrameLink")
        public String image1000px;
        @SerializedName(value="500pxLink", alternate={"500pxFrameLink", "500pxKeyFrameLink", "500pxCropLink", "500pxHeadshotLink"})
        public String image500px;
        @SerializedName(value="fullSizeFrameLink")
        public String image1FullSize;
        @SerializedName(value="250pxLink", alternate={"250pxFrameLink", "250pxKeyFrameLink", "250pxCropLink", "250pxHeadshotLink"})
        public String image250px;
        @SerializedName(value="125pxLink", alternate={"125pxFrameLink", "125pxKeyFrameLink", "125pxCropLink", "125pxHeadshotLink"})
        public String image125px;
        @SerializedName(value="50pxLink", alternate={"50pxFrameLink", "50pxKeyFrameLink", "50pxCropLink", "50pxHeadshotLink"})
        public String image50px;
    }




    public static class TheTakeProduct implements MovieMetaData.ShopItemInterface {
        @SerializedName(value="cropImage", alternate={"cropImages"})
        public FrameImages cropImage;
        @SerializedName(value="productImage", alternate={"productImages"})
        public FrameImages productImage;
        @SerializedName(value="keyFrameImage", alternate={"keyFrameImages", "frameImages"})
        public FrameImages keyFrameImage;
        public String characterId;
        public String actorId;
        public boolean verified;
        public float keyCropProductY;
        public float keyCropProductX;
        public String actorName;
        public String mediaId;
        public String productBrand;
        @SerializedName("unavailable")
        public int bUnavailable;
        public String characterName;
        public boolean soldOut;
        public String mediaName;
        public String purchaseLink;
        public float trendingScore;
        public long keyFrameImageTime;
        public long productId;
        public float keyFrameProductX;
        public float keyFrameProductY;
        public String productPrice;
        public String productName;

        private TheTakeProductDetail productDetail;

        public String getShopItemText(Context context){
            return context.getResources().getString(R.string.shop_at_the_take);
        }

        public String getThumbnailUrl(){
            if (cropImage != null)
                return cropImage.image500px;
            else if (keyFrameImage != null)
                return keyFrameImage.image500px;
            else
                return "";
        }

        public String getProductThumbnailUrl(){
            if (productImage != null)
                return productImage.image500px;
            else
                return "";
        }

        public String getProductName(){
            return productName;
        }

        public String getProductBrand(){
            return productBrand;
        }

        public boolean isVerified(){
            return verified;
        }

        public float getKeyCropProductY(){
            return keyCropProductY;
        }

        public float getKeyCropProductX(){
            return keyCropProductX;
        }

        public TheTakeProductDetail getProductDetail(){
            return productDetail;
        }

        public void setProductDetail(TheTakeProductDetail detail){
            productDetail = detail;
        }

        public long getProductId(){
            return productId;
        }

        public String getProductReportId(){
            return Long.toString(productId);
        }

        public String getShareLinkUrl(){
            if (productDetail != null)
                return productDetail.shareUrl;
            return "";
        }

        public String getPurchaseLinkUrl(){
            if (productDetail != null)
                return productDetail.purchaseLink;
            return "";
        }
    }

    public static class TheTakeProductDetail{
        public FrameImages cropImage;//	Object
        public FrameImages actorImage;//	Object
        public FrameImages posterImage;//	Object
        public FrameImages keyFrameImage;//	Object
        public FrameImages productImage;//	Object

        public List<TheTakeProductDetail> compProducts;//	Array
        public List<TheTakeProduct> accessories;//	Array

        public String productBrand;//	String	Etro
        public int tertiaryCategoryId;//	Integer	42
        public boolean verified;//	Boolean	false
        public String mediaDescription;//	String
        public String shortUrl;//	String	thetake.com/LoisScarf
        public int primaryCategoryId;//	Integer	1
        public double frameLetterboxRatio;//	Number	0.12963
        public int mediaId;//	Integer	421
        @SerializedName("unavailable")
        public int isUnavailable;//	Integer	0
        public String characterName;//	String	Lois Lane
        public boolean soldOut;//	Boolean	false
        public String mediaName	;//String	Man of Steel (Full Movie - Theatrical)
        public double trendingScore;//	Integer	10
        public String shareUrl;//	String	https://thetake.com/product/104741/amy-adams-etro-paisley-print-scarf-man-of-steel-full-movie-theatrical
        public int characterId;//	Integer	941
        public int actorId;//	Integer	1078
        public double keyCropProductY;//	Number	0.6167012264556395
        public double keyCropProductX;//	Number	0.3425107458083145
        public String actorName;//	String	Amy Adams
        public String itunesLink;//	String
        public String primaryCategoryName;//	String	Women's Fashion
        public String tertiaryCategoryName;//	String	Scarves
        public String purchaseLink;//	String	http://www.farfetch.com/shopping/item11136152.aspx
        public int secondaryCategoryId;//	Integer	35
        public int productId;//	Integer	104741
        public String keyFrameProductX;//	Number	0.618824
        public int keyFrameTime;//	Integer	3157000
        public String amazonLink;//	String
        public String keyFrameProductY;//	Number	0.48583
        public String secondaryCategoryName;//	String	Accessories
        public String productName;//	String	Paisley Print Scarf
        public String productPrice;//	String	$242.82
        public String fandangoLink;//	String


        public String getProductImage(){
            return productImage.image500px;
        }
    }

}
