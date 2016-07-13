package com.wb.nextgen.network;

import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.data.MovieMetaData.FilmPoster;
import com.wb.nextgen.data.MovieMetaData.CastHeadShot;
import com.wb.nextgen.data.MovieMetaData.CastSocialMedia;
import com.wb.nextgen.data.MovieMetaData.BaselineCastData;
import com.wb.nextgen.data.MovieMetaData.Filmography;
import com.wb.nextgen.data.TheTakeData;
import com.wb.nextgen.util.HttpHelper;
import com.wb.nextgen.util.concurrent.ResultListener;
import com.wb.nextgen.util.concurrent.Worker;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.NextGenLogger;
import com.wb.nextgen.util.utils.StringHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
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

    private static String API_KEY_KEY = "baseline_api_key";

    private static final String BASELINE_DOMAIN = "http://baselineapi.com/api";

    private static class Endpoints {
        static final String GetCredits = "/ProjectAllCredits";
        static final String GetBio = "/ParticipantBioShort";
        static final String GetHeadshot = "/ParticipantHeadshot";
        static final String GetSocialMedia = "/ParticipantSocialMedia";
        static final String GetFilmography = "/ParticipantFilmCredit";
        static final String GetFilmPoster = "/ProjectFilmPoster";

        static final String GetProfileImages = "/ParticipantProfileImages";
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
                baselineAPIKey = jsonObject.getString(API_KEY_KEY);

            } catch (Exception ex) {
                NextGenLogger.e("ss", ex.getLocalizedMessage());
            }
        }
    }

    public static void getCastActorsPhotos(final List<String> castsIDs, ResultListener<HashMap<String, BaselineCastData>> l) {
        Worker.execute(new Callable<HashMap<String, BaselineCastData>>() {
            @Override
            public HashMap<String, BaselineCastData> call() throws Exception {
                HashMap<String, BaselineCastData> castsInfoMap = new HashMap<String, BaselineCastData>();
                for (String castId : castsIDs) {
                    BaselineCastData thisData = new BaselineCastData();

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("id", castId));
                    params.add(new BasicNameValuePair("apikey", baselineAPIKey));

                    //String bio = getActorBio(params);
                    //List<Filmography> films = getActorFilmnography(params);
                    List<CastHeadShot> headShots = getHeadShot(params);
                    List<CastSocialMedia> socialMedias = getCastSocialMedia(params);
                    thisData.setSocialMedium(socialMedias);

                    // thisData.biography = bio;
                    //thisData.filmogrphies = films;
                    thisData.headShots = headShots;

                    castsInfoMap.put(castId, thisData);

                }


                return castsInfoMap;
            }
        }, l);
    }

    public static void getCastActorsData(final List<String> castsIDs, ResultListener<HashMap<String, BaselineCastData>> l) {

        Worker.execute(new Callable<HashMap<String, BaselineCastData>>() {
            @Override
            public HashMap<String, BaselineCastData> call() throws Exception {
                HashMap<String, BaselineCastData> castsInfoMap = new HashMap<String, BaselineCastData>();
                for (String castId : castsIDs) {
                    BaselineCastData thisData = new BaselineCastData();

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("id", castId));
                    params.add(new BasicNameValuePair("apikey", baselineAPIKey));

                    //String bio = getActorBio(params);
                    //List<Filmography> films = getActorFilmnography(params);
                    List<CastHeadShot> headShots = getHeadShot(params);
                    List<CastSocialMedia> socialMedias = getCastSocialMedia(params);
                    thisData.setSocialMedium(socialMedias);

                   // thisData.biography = bio;
                    //thisData.filmogrphies = films;
                    thisData.headShots = headShots;

                    castsInfoMap.put(castId, thisData);

                }


                return castsInfoMap;
            }
        }, l);
    }


    private static List<CastSocialMedia> getCastSocialMedia(List<NameValuePair> params) throws JSONException, IOException {

        String result = HttpHelper.getFromUrl(BASELINE_DOMAIN + Endpoints.GetSocialMedia, params);


        Type listType = new TypeToken<ArrayList<CastSocialMedia>>() {
        }.getType();

        Gson gson = new GsonBuilder().create();

        List<CastSocialMedia> socialMedium = gson.fromJson(result, listType);


        return socialMedium;
    }

    public static void getFilmographyAndBioOfPerson(final String castId, ResultListener<BaselineCastData> l) {
        Worker.execute(new Callable<BaselineCastData>() {
            @Override
            public BaselineCastData call() throws Exception {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", castId));
                params.add(new BasicNameValuePair("apikey", baselineAPIKey));
                BaselineCastData thisData = new BaselineCastData();

                thisData.biography = getActorBio(params);

                List<Filmography> films = getActorFilmnography(params);
                List<FilmPoster> posters = getFilmographyPostesSync(films);



                thisData.filmogrphies = films;
                return thisData;
            }
        }, l);
    }


    private static String getActorBio(List<NameValuePair> params) throws JSONException, IOException {

        String result = HttpHelper.getFromUrl(BASELINE_DOMAIN + Endpoints.GetBio, params);
        JSONArray jsonArray = new JSONArray(result);
        if (jsonArray != null) {
            JSONObject object = (JSONObject) jsonArray.get(0);
            return object.getString(Keys.ShortBio);
        }


        return null;
    }

    private static List<Filmography> getActorFilmnography(List<NameValuePair> params) throws JSONException, IOException {

        List<Filmography> result = new ArrayList<Filmography>();

        HashMap<String, String> projectIdHash = new HashMap<String, String>();
        String response = HttpHelper.getFromUrl(BASELINE_DOMAIN + Endpoints.GetFilmography, params);

        JSONArray jsonArray = new JSONArray(response);
        if (jsonArray != null) {
            for (int i = jsonArray.length() - 1; i >= 0 && result.size() <= 10; i--) {
                try {
                    JSONObject object = (JSONObject) jsonArray.get(i);
                    Gson gson = new GsonBuilder().create();
                    Filmography film = gson.fromJson(object.toString(), Filmography.class);
                    if (film == null)
                        continue;
                    if (!projectIdHash.containsKey(film.projectId)) {
                        result.add(film);
                        projectIdHash.put(film.projectId, film.projectId);
                    }
                } catch (JsonSyntaxException jex){
                    NextGenLogger.e(F.TAG_API, jex.getLocalizedMessage());
                } catch (JSONException jex2){
                    NextGenLogger.e(F.TAG_API, jex2.getLocalizedMessage());
                }
            }
            //DemoJSONData data = gson.fromJson(object, MovieMetaData..class);
        }


        return result;
    }


    private static List<FilmPoster> getFilmographyPostesSync(final List<Filmography> films){
        List<FilmPoster> resultList = new ArrayList<FilmPoster>();
        for (Filmography film: films) {
            FilmPoster poster = null;
            if (!film.isFilmPosterRequest()) {
                try {
                    poster = getFilmDetail(film.projectId);
                } catch (Exception ex) {
                }
                if (poster != null && !StringHelper.isEmpty(poster.imageId)) {
                    film.setFilmPoster(poster);
                } else {
                    poster = FilmPoster.getDefaultEmptyPoster();
                }
            }
            resultList.add(poster);
        }
        return resultList;
    }

    private static List<CastHeadShot> getHeadShot(List<NameValuePair> params) throws JSONException, IOException {

        String result = HttpHelper.getFromUrl(BASELINE_DOMAIN + Endpoints.GetProfileImages, params);
        //JSONObject json = new JSONObject(result);
        JSONArray jsonArray = new JSONArray(result);
        if (jsonArray != null) {

            Type listType = new TypeToken<ArrayList<CastHeadShot>>() {
            }.getType();

            Gson gson = new GsonBuilder().create();

            List<CastHeadShot> headShots = gson.fromJson(result, listType);

            return headShots;

        }

        return null;
    }

    private static MovieMetaData.FilmPoster getFilmDetail(String projectId) throws JSONException, IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", projectId));
        params.add(new BasicNameValuePair("apikey", baselineAPIKey));

        String result = HttpHelper.getFromUrl(BASELINE_DOMAIN + Endpoints.GetFilmPoster, params);
        JSONArray jsonArray = new JSONArray(result);
        if (jsonArray != null && jsonArray.length() > 0) {
            JSONObject object = (JSONObject) jsonArray.get(0);
            try {
                Gson gson = new GsonBuilder().create();
                return gson.fromJson(object.toString(), MovieMetaData.FilmPoster.class);
            }catch (Exception ex){
                return null;
            }

        }

        return null;
    }
}
