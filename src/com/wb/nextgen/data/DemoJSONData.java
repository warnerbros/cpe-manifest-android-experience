package com.wb.nextgen.data;

import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.util.utils.StringHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gzcheng on 3/3/16.
 */
public class DemoJSONData {

    public class JSONVideo{
        @SerializedName("file_url")
        public String fileUrl;
        @SerializedName("interstitial_file_url")
        public String interstitialFileUrl;
        @SerializedName("delivery_format")
        public String deliveryFormat;
    }

    public class ScenesInfo{
        public int startTime;
        public int endTime;
        @SerializedName("people")
        public String[] actorIdsList;
        public List<ActorInfo> actorsList ;
        public ScenesInfo(){

        }
    }

    public static class ActorInfo{
        public String id;
        @SerializedName("job_function")
        public String jobFunction;
        @SerializedName("billing_block_order")
        public int billingBlockOrder;
        @SerializedName("character")
        public String character;
        @SerializedName("name")
        public String realName;
        @SerializedName("thumbnail_image")
        public String thumbnailImageResource;
        @SerializedName("full_image")
        public String fullImageResource;
        @SerializedName("biography")
        public String biography;
        @SerializedName("filmography")
        public Filmography[] actorFilmography;

        public String getThumbnailUri(){
            if (StringHelper.isEmpty(thumbnailImageResource))
                return "";
            String otherPath = "android.resource://com.wb.nextgen/drawable/" + thumbnailImageResource;
            if (otherPath.endsWith(".jpg") )
                otherPath = otherPath.substring(0, otherPath.length() - 4);
            return otherPath;
        }

        public String getFullImageUri(){
            if (StringHelper.isEmpty(fullImageResource))
                return "";
            String otherPath = "android.resource://com.wb.nextgen/drawable/" + fullImageResource;
            if (otherPath.endsWith(".jpg") )
                otherPath = otherPath.substring(0, otherPath.length() - 4);
            return otherPath;

        }
    }

    public class Filmography{
        public String title;
        @SerializedName("poster_image")
        public String posterImageUrl;
        @SerializedName("external_url")
        public String movieInfoUrl;
    }

    public class MovieJSONData {
        public String title;
        @SerializedName("people")
        public List<ActorInfo> actorsInfo;
    }

    @SerializedName("video")
    public JSONVideo mainFeatureVideo;

    @SerializedName("scenes")
    public ScenesInfo[] scenesInfos;

    @SerializedName("metadata")
    public MovieJSONData movieMetaData;

    public static DemoJSONData createFromUri(String fileUri){
        AssetManager am = NextGenApplication.getContext().getAssets();
        StringBuilder sb = new StringBuilder();
        InputStream in = null;
        try {
            in = am.open("man_of_steel.json");
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
            String jsonText = sb.toString();
            Gson gson = new GsonBuilder().create();
            DemoJSONData data = gson.fromJson(jsonText, DemoJSONData.class);

            HashMap<String, ActorInfo> actorMap = new HashMap<String, ActorInfo>();
            for(ActorInfo info : data.movieMetaData.actorsInfo){
                actorMap.put(info.id, info);
            }

            for(ScenesInfo scene : data.scenesInfos){
                if (scene.actorsList == null)
                    scene.actorsList = new ArrayList<ActorInfo>();
                for (String actorId : scene.actorIdsList) {
                    if (actorMap.containsKey(actorId)) {
                        scene.actorsList.add(actorMap.get(actorId));
                    }
                }
            }

            return data;
        }catch (Exception ex){
            return null;
        }
    }
}
