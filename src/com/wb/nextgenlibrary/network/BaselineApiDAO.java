package com.wb.nextgenlibrary.network;

import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.data.MovieMetaData.CastHeadShot;
import com.wb.nextgenlibrary.data.MovieMetaData.BaselineCastData;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by gzcheng on 4/4/16.
 */
public class BaselineApiDAO {

	private static String baselineAPIKey;
    private static String xAPIKey;

    private static String API_KEY_KEY = "baseline_api_key";
    private static String X_API_KEY_KEY = "x-api-key";
    private static String X_STUDIO_KEY = "X-Studio";

    private static final String BASELINE_DOMAIN = "http://baselineapi.com/api";
    private static final String X_API_DOMAIN = "https://vic57ayytg.execute-api.us-west-2.amazonaws.com/staging";

    private static class Endpoints {
        static final String GetCredits = "/ProjectAllCredits";
        static final String GetBio = "/ParticipantBioShort";
        static final String GetHeadshot = "/ParticipantHeadshot";
        static final String GetSocialMedia = "/ParticipantSocialMedia";
        static final String GetFilmography = "/ParticipantFilmCredit";
        static final String GetFilmPoster = "/ProjectFilmPoster";

        static final String GetProfileImages = "/ParticipantProfileImages";
    }

    private static class XEndpoints {
        static final String GetFilmCredits = "/film/credits";
        static final String GetTalentDetail = "/talent";
        static final String GetTalentImages = "/talent/images";

    }

    private static class Keys {
        static final String ParticipantID = "PARTICIPANT_ID";
        static final String FullName = "FULL_NAME";
        static final String Credit = "CREDIT";
        static final String CreditGroup = "CREDIT_GROUP";
        static final String ShortBio = "SHORT_BIO";
        static final String MediumURL = "MEDIUM_URL";
        static final String LargeURL = "LARGE_URL";
        static final String FullURL = "FULL_URL";
        static final String ProjectID = "PROJECT_ID";
        static final String ProjectName = "PROJECT_NAME";
        static final String Handle = "HANDLE";
        static final String URL = "URL";
    }


    public static void init() {
        if (StringHelper.isEmpty(baselineAPIKey)) {
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
            } finally {
                try {
                    if (in != null)
                        in.close();
                } catch (IOException e) {
                }
            }
            try {

                JSONObject jsonObject = new JSONObject(sb.toString());
                baselineAPIKey = jsonObject.getString(API_KEY_KEY);
                xAPIKey = jsonObject.getString(X_API_KEY_KEY);

            } catch (Exception ex) {
                NextGenLogger.e("ss", ex.getLocalizedMessage());
            }
        }
    }


    final static private List<ResultListener<Boolean>> actorsImagesListener = new ArrayList<>();

    public static void getCastActorsImages(final List<MovieMetaData.CastData> castsDatas, final ResultListener<Boolean> l) {        // to get actors images at NGE loading, adding listeners will be called when each actor's images returns
        if (l != null){
            if (!actorsImagesListener.contains(l))
                actorsImagesListener.add(l);
        }
        if (actorsImagesListener.size() <= 1) {

            Worker.execute(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    for (MovieMetaData.CastData castData : castsDatas) {


                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("id", castData.getBaselineActorId()));


                        List<NameValuePair> headers = new ArrayList<NameValuePair>();
                        headers.add(new BasicNameValuePair(X_API_KEY_KEY, xAPIKey));
                        headers.add(new BasicNameValuePair(X_STUDIO_KEY, NextGenExperience.getStudioXAPIKey()));

                        String talentImageResult = HttpHelper.getFromUrl(X_API_DOMAIN + XEndpoints.GetTalentImages, params, headers, true, true);
                        Type listType = new TypeToken<ArrayList<CastHeadShot>>() {
                        }.getType();

                        Gson gson = new GsonBuilder().create();

                        List<CastHeadShot> headShots = gson.fromJson(talentImageResult, listType);

                        if (castData.getBaselineCastData() == null) {
                            castData.baselineCastData = new BaselineCastData();
                        }
                        castData.baselineCastData.headShots = headShots;
                        for (ResultListener<Boolean> listener : actorsImagesListener){
                            listener.onResult(true);
                        }

                    }


                    return true;

                }


            }, new ResultListener<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    for (ResultListener<Boolean> listener : actorsImagesListener){
                        listener.onResult(result);
                    }
                    actorsImagesListener.removeAll(actorsImagesListener);
                }

                @Override
                public <E extends Exception> void onException(E e) {
                    for (ResultListener<Boolean> listener : actorsImagesListener){
                        listener.onException(e);
                    }
                    actorsImagesListener.removeAll(actorsImagesListener);
                }
            });
        }
    }


    public static void getFilmographyAndBioOfPerson(final String castId, ResultListener<BaselineCastData> l) {
        Worker.execute(new Callable<BaselineCastData>() {
            @Override
            public BaselineCastData call() throws Exception {
                return getFilmographyAndBioOfPersonSync(castId);
            }
        }, l);
    }

    private static BaselineCastData getFilmographyAndBioOfPersonSync(String castId) throws IOException{
        BaselineCastData castData;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", castId));


        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        headers.add(new BasicNameValuePair(X_API_KEY_KEY, xAPIKey));
        headers.add(new BasicNameValuePair(X_STUDIO_KEY, NextGenExperience.getStudioXAPIKey()));

        String userdataResult = HttpHelper.getFromUrl(X_API_DOMAIN + XEndpoints.GetTalentDetail, params, headers, true, true);

        Gson gson = new GsonBuilder().create();
        try {
            castData = gson.fromJson(userdataResult, BaselineCastData.class);
            castData.filterText();
        }catch (Exception ex){
            castData = new BaselineCastData();
        }



        return castData;
    }


}
