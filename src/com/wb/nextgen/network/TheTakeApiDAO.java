package com.wb.nextgen.network;

import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.data.TheTakeData.TheTakeProduct;
import com.wb.nextgen.data.TheTakeData.TheTakeProductDetail;
import com.wb.nextgen.data.TheTakeData.TheTakeCategory;
import com.wb.nextgen.data.TheTakeData.TheTakeProductFrame;
import com.wb.nextgen.util.HttpHelper;
import com.wb.nextgen.util.concurrent.ResultListener;
import com.wb.nextgen.util.concurrent.Worker;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.NextGenLogger;
import com.wb.nextgen.util.utils.StringHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
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

    private static final List<TheTakeCategory> categories = new ArrayList<TheTakeCategory>();

    public static void init(){
        theTakeMediaId = NextGenApplication.getMovieMetaData().getIdentifierForExternalAPI(MovieMetaData.THE_TAKE_MANIFEST_IDENTIFIER);
        if (StringHelper.isEmpty(theTakeAPIKey)) {
            AssetManager am = NextGenApplication.getContext().getAssets();
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
        return HttpHelper.getFromUrl(url, params, headerValues);
    }

    public static void fetchProductFrames(final int start, final int count, ResultListener<List<TheTakeProductFrame>> l) {
        Worker.execute(new Callable<List<TheTakeProductFrame>>() {
            @Override
            public List<TheTakeProductFrame> call() throws Exception {

                /*if (categories != null)
                    return categories;*/

                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair(Keys.Media, theTakeMediaId));
                    params.add(new BasicNameValuePair(Keys.Limit, Integer.toString(count)));
                    params.add(new BasicNameValuePair(Keys.Start, Integer.toString(start)));
                    //params.add(new BasicNameValuePair(Keys.StartTime, Long.toString(startMilliSecond)));
                    //params.add(new BasicNameValuePair(Keys.EndTime, Long.toString(endMilliSecond)));

                    String result = getFromUrl(THETAKE_DOMAIN + Endpoints.PrefetchProductFrames, params);

                    Type listType = new TypeToken<ArrayList<TheTakeProductFrame>>() {
                    }.getType();

                    Gson gson = new GsonBuilder().create();

                    List<TheTakeProductFrame> productFrames = gson.fromJson(result, listType);
                    //TheTakeProductFrame
                    /*JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray != null && jsonArray.length() > 0){
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                        }
                    }*/
                    return productFrames;

                } catch (Exception ex) {

                }

                return null;
            }
        }, l);
    }

    public static void prefetchProductCategories(ResultListener<List<TheTakeCategory>> l) {
        Worker.execute(new Callable<List<TheTakeCategory>>() {
            @Override
            public List<TheTakeCategory> call() throws Exception {
                if (categories.size() > 0)
                    return categories;

                List<TheTakeCategory> resultList = new ArrayList<TheTakeCategory>();
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair(Keys.Media, theTakeMediaId));

                    String result = getFromUrl(THETAKE_DOMAIN + Endpoints.PrefetchProductCategories, params);

                    Type listType = new TypeToken<ArrayList<TheTakeCategory>>() {
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

    public static void getFrameProducts(final double frameTime, ResultListener<Object> l){
        Worker.execute(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair(Keys.Media, theTakeMediaId));
                    params.add(new BasicNameValuePair(Keys.Time, Double.toString(frameTime)));

                    String result = getFromUrl(THETAKE_DOMAIN + Endpoints.GetFrameProducts, params);
                } catch (Exception ex) {
                    NextGenLogger.d(F.TAG_API, ex.getLocalizedMessage());

                }

                return null;
            }
        }, l);

    }

    public static void getCategoryProducts(final int categoryId, ResultListener<List<TheTakeProduct>> l){
        Worker.execute(new Callable<List<TheTakeProduct>>() {
            @Override
            public List<TheTakeProduct> call() throws Exception {
                try {

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair(Keys.Media, theTakeMediaId));
                    params.add(new BasicNameValuePair(Keys.Category, Integer.toString(categoryId) ));

                    String result = getFromUrl(THETAKE_DOMAIN + Endpoints.GetCategoryProducts, params);
                    Type listType = new TypeToken<ArrayList<TheTakeProduct>>() {
                    }.getType();

                    Gson gson = new GsonBuilder().create();

                    List<TheTakeProduct> resultList = gson.fromJson(result, listType);
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
/*

    getJSONWithPath("/frames/listFrames", parameters: ["media": mediaId, "limit": "9999"], successBlock: { (result) -> Void in
            if let frames = result["result"] as? [NSDictionary] {
                for frameInfo in frames {
                    if let frameTime = frameInfo["frameTime"] as? Double {
                        self._frameTimes[frameTime] = frameInfo
                    }
                }
            }
        }, errorBlock: nil)
    }


    func prefetchProductCategories() {
        productCategories.removeAll()

        getJSONWithPath("/categories/listProductCategories", parameters: ["media": mediaId], successBlock: { (result) in
            if let categories = result["result"] as? [NSDictionary] {
                for categoryInfo in categories {
                    self.productCategories.append(TheTakeCategory(info: categoryInfo))
                }
            }
        }, errorBlock: nil)
    }

    func closestFrameTime(timeInSeconds: Double) -> Double {
        let timeInMilliseconds = timeInSeconds * 1000
        var closestFrameTime = -1.0

        if _frameTimes.count > 0 && _frameTimes[timeInMilliseconds] == nil {
            let frameTimeKeys = _frameTimes.keys.sort()
            let frameIndex = frameTimeKeys.indexOfFirstObjectPassingTest({ $0 > timeInMilliseconds })
            closestFrameTime = frameTimeKeys[max(frameIndex - 1, 0)]
        } else {
            closestFrameTime = timeInMilliseconds
        }

        return closestFrameTime
    }

    func getFrameProducts(frameTime: Double, successBlock: (products: [TheTakeProduct]) -> Void) -> NSURLSessionDataTask? {
        if frameTime >= 0 && _frameTimes[frameTime] != nil {
            return getJSONWithPath("/frameProducts/listFrameProducts", parameters: ["media": mediaId, "time": String(frameTime)], successBlock: { (result) -> Void in
                if let productList = result["result"] as? NSArray {
                    var products = [TheTakeProduct]()
                    for productInfo in productList {
                        if let productData = productInfo as? NSDictionary {
                            products.append(TheTakeProduct(data: productData))
                        }
                    }

                    successBlock(products: products)
                }
            }) { (error) -> Void in

            }
        }

        return nil
    }

    func getCategoryProducts(categoryId: String, successBlock: (products: [TheTakeProduct]) -> Void) -> NSURLSessionDataTask? {
        return getJSONWithPath("/products/listProducts", parameters: ["category": categoryId, "media": mediaId], successBlock: { (result) -> Void in
            if let productList = result["result"] as? NSArray {
                var products = [TheTakeProduct]()
                for productInfo in productList {
                    if let productData = productInfo as? NSDictionary {
                        products.append(TheTakeProduct(data: productData))
                    }
                }

                successBlock(products: products)
            }
        }) { (error) -> Void in

        }
    }

    func getProductDetails(productId: String, successBlock: (product: TheTakeProduct) -> Void) -> NSURLSessionDataTask {
        return getJSONWithPath("/products/productDetails", parameters: ["product": productId], successBlock: { (result) -> Void in
            successBlock(product: TheTakeProduct(data: result))
        }) { (error) -> Void in

        }
    }*/
}
