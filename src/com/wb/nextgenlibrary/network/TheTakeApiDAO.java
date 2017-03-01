package com.wb.nextgenlibrary.network;

import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.data.TheTakeData.TheTakeProduct;
import com.wb.nextgenlibrary.data.TheTakeData.TheTakeProductDetail;
import com.wb.nextgenlibrary.data.TheTakeData.ShopCategory;
import com.wb.nextgenlibrary.data.TheTakeData.TheTakeProductFrame;
import com.wb.nextgenlibrary.util.HttpHelper;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;
import com.wb.nextgenlibrary.util.concurrent.Worker;
import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;
import com.wb.nextgenlibrary.util.utils.StringHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by gzcheng on 4/6/16.
 */
public class TheTakeApiDAO {
    private static final String THETAKE_DOMAIN = "https://thetake.p.mashape.com";
    private static String theTakeAPIKey;
    private static String theTakeMediaId;
    private static String API_KEY_KEY = "thetake_api_key";

    private static class Endpoints {
        static final String PrefetchProductFrames = "/frames/listFrames";
        static final String PrefetchProductCategories = "/categories/listProductCategories";
        static final String GetFrameProducts = "/frameProducts/listFrameProducts";
        static final String GetCategoryProducts = "/products/listProducts";
        static final String GetProductDetails = "/products/productDetails";
    }

    private static class Keys {
        static final String Media = "media";
        static final String Time = "time";
        static final String Category = "category";
        static final String Product = "product";
        static final String Limit = "limit";
        static final String Start = "start";
    }

    private static final List<ShopCategory> categories = new ArrayList<ShopCategory>();

    public static void init(){
        theTakeMediaId = NextGenExperience.getMovieMetaData().getIdentifierForExternalAPI(MovieMetaData.THE_TAKE_MANIFEST_IDENTIFIER);
        if (StringHelper.isEmpty(theTakeAPIKey)) {
            AssetManager am = NextGenExperience.getApplicationContext().getAssets();
            StringBuilder sb = new StringBuilder();
            InputStream in = null;
            try {
                in = am.open("APIKeysConfig.json");
                InputStreamReader is = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(is);
                String read = br.readLine();

                while (read != null) {
                    sb.append(read);
                    read = br.readLine();
                }
            } catch (IOException e) {
                // NextGenLogger.e(F.TAG, "ResourceUtils.getStringFromAssets: " + e.getMessage(), e);
            } finally {
                try {
                    if (in != null)
                        in.close();
                } catch (IOException e) {
                    // NextGenLogger.e(F.TAG, "ResourceUtils.getStringFromAssets: " + e.getMessage(), e);
                }
            }
            try {

                JSONObject jsonObject = new JSONObject(sb.toString());
                theTakeAPIKey = jsonObject.getString(API_KEY_KEY);

            } catch (Exception ex) {
                NextGenLogger.e("ss", ex.getLocalizedMessage());
            }
        }
    }


    private static String getFromUrl(String url, List <NameValuePair> params) throws IOException {
        List<NameValuePair> headerValues = new ArrayList<NameValuePair>();
        headerValues.add(new BasicNameValuePair("X-Mashape-Key", theTakeAPIKey));
        return HttpHelper.getFromUrl(url, params, headerValues, true, true);
    }

    public static void fetchProductFrames(final long targetTimeCode, final int start, final int count, ResultListener<List<TheTakeProductFrame>> l) {
        Worker.execute(new Callable<List<TheTakeProductFrame>>() {
            @Override
            public List<TheTakeProductFrame> call() throws Exception {

                /*if (categories != null)
                    return categories;*/

                try {

                    long lastTimeCode = 0L;
                    boolean hasMore = true;
                    List<TheTakeProductFrame> resultList = new ArrayList<TheTakeProductFrame>();
                    int modStart = start;

                    while (lastTimeCode < targetTimeCode && hasMore) {

                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair(Keys.Media, theTakeMediaId));
                        params.add(new BasicNameValuePair(Keys.Limit, Integer.toString(count)));
                        params.add(new BasicNameValuePair(Keys.Start, Integer.toString(modStart)));
                        //params.add(new BasicNameValuePair(Keys.StartTime, Long.toString(startMilliSecond)));
                        //params.add(new BasicNameValuePair(Keys.EndTime, Long.toString(endMilliSecond)));

                        String result = getFromUrl(THETAKE_DOMAIN + Endpoints.PrefetchProductFrames, params);

                        Type listType = new TypeToken<ArrayList<TheTakeProductFrame>>() {
                        }.getType();

                        Gson gson = new GsonBuilder().create();

                        List<TheTakeProductFrame> productFrames = gson.fromJson(result, listType);

                        if (productFrames == null || productFrames.size() == 0){
                            hasMore = false;
                        }else{
                            if (productFrames.size() < count)
                                hasMore = false;
                            resultList.addAll(productFrames);
                            lastTimeCode = productFrames.get(productFrames.size() -1).frameTime;
                        }

                    }

                    return resultList;

                } catch (Exception ex) {

                }

                return null;
            }
        }, l);
    }

    public static void prefetchProductCategories(ResultListener<List<ShopCategory>> l) {
        Worker.execute(new Callable<List<ShopCategory>>() {
            @Override
            public List<ShopCategory> call() throws Exception {
                if (categories.size() > 0)
                    return categories;

                List<ShopCategory> resultList = new ArrayList<ShopCategory>();
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair(Keys.Media, theTakeMediaId));

                    String result = getFromUrl(THETAKE_DOMAIN + Endpoints.PrefetchProductCategories, params);

                    Type listType = new TypeToken<ArrayList<ShopCategory>>() {
                    }.getType();

                    Gson gson = new GsonBuilder().create();

                    resultList = gson.fromJson(result, listType);


                    categories.addAll(resultList);
                    //return category;
                } catch (Exception ex) {
                    NextGenLogger.d(F.TAG_API, ex.getLocalizedMessage());

                }

                return categories;
            }
        }, l);
    }

    public static void getFrameProducts(final double frameTime, ResultListener<List<MovieMetaData.ShopItemInterface>> l){
        Worker.execute(new Callable<List<MovieMetaData.ShopItemInterface>>() {
            @Override
            public List<MovieMetaData.ShopItemInterface> call() throws Exception {
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair(Keys.Media, theTakeMediaId));
                    params.add(new BasicNameValuePair(Keys.Time, Double.toString(frameTime)));

                    String result = getFromUrl(THETAKE_DOMAIN + Endpoints.GetFrameProducts, params);
                    Type listType = new TypeToken<ArrayList<MovieMetaData.ShopItemInterface>>() {
                    }.getType();

                    Gson gson = new GsonBuilder().create();

                    List<MovieMetaData.ShopItemInterface> products = gson.fromJson(result, listType);

                    return products;
                } catch (Exception ex) {
                    NextGenLogger.d(F.TAG_API, ex.getLocalizedMessage());

                }

                return null;
            }
        }, l);

    }

    public static void getCategoryProducts(final int categoryId, ResultListener<List<MovieMetaData.ShopItemInterface>> l){
        Worker.execute(new Callable<List<MovieMetaData.ShopItemInterface>>() {
            @Override
            public List<MovieMetaData.ShopItemInterface> call() throws Exception {
                try {

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair(Keys.Media, theTakeMediaId));
                    params.add(new BasicNameValuePair(Keys.Category, Integer.toString(categoryId) ));

                    String result = getFromUrl(THETAKE_DOMAIN + Endpoints.GetCategoryProducts, params);
                    Type listType = new TypeToken<ArrayList<MovieMetaData.ShopItemInterface>>() {
                    }.getType();

                    Gson gson = new GsonBuilder().create();

                    List<MovieMetaData.ShopItemInterface> resultList = gson.fromJson(result, listType);
                    return resultList;

                } catch (Exception ex) {
                    NextGenLogger.d(F.TAG_API, ex.getLocalizedMessage());
                }

                return null;
            }
        }, l);

    }

    public static void getProductDetails(final long productId, ResultListener<TheTakeProductDetail> l){
        Worker.execute(new Callable<TheTakeProductDetail>() {
            @Override
            public TheTakeProductDetail call() throws Exception {
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair(Keys.Media, theTakeMediaId));
                    params.add(new BasicNameValuePair(Keys.Product, Long.toString(productId)));

                    String result = getFromUrl(THETAKE_DOMAIN + Endpoints.GetProductDetails, params);
                    Gson gson = new GsonBuilder().create();
                    return gson.fromJson(result, TheTakeProductDetail.class);
                } catch (Exception ex) {
                    NextGenLogger.d(F.TAG_API, ex.getLocalizedMessage());
                }

                return null;
            }
        }, l);

    }

}
