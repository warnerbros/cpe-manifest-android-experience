package com.wb.nextgen.network;

import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.data.MovieMetaData.CastHeadShot;
import com.wb.nextgen.data.MovieMetaData.BaselineCastData;
import com.wb.nextgen.data.MovieMetaData.Filmography;
import com.wb.nextgen.util.HttpHelper;
import com.wb.nextgen.util.concurrent.ResultListener;
import com.wb.nextgen.util.concurrent.Worker;
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

                    String bio = getActorBio(params);
                    //List<Filmography> films = getActorFilmnography(params);
                    CastHeadShot headShot = getHeadShot(params);

                    thisData.biography = bio;
                    //thisData.filmogrphies = films;
                    thisData.headShot = headShot;

                    castsInfoMap.put(castId, thisData);

                }


                return castsInfoMap;
            }
        }, l);
    }

    public static void getFilmographyOfPerson(final String castId, ResultListener<List<Filmography>> l) {
        Worker.execute(new Callable<List<Filmography>>() {
            @Override
            public List<Filmography> call() throws Exception {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", castId));
                params.add(new BasicNameValuePair("apikey", baselineAPIKey));

                List<Filmography> films = getActorFilmnography(params);
                if (films != null && films.size() > 0) {
                    for (Filmography film : films) {
                        if (!StringHelper.isEmpty(film.projectId)) {

                        }
                    }
                }


                return films;
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

        String response = HttpHelper.getFromUrl(BASELINE_DOMAIN + Endpoints.GetFilmography, params);

        JSONArray jsonArray = new JSONArray(response);
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                Gson gson = new GsonBuilder().create();
                Filmography film = gson.fromJson(object.toString(), Filmography.class);
                if (film == null)
                    continue;
                MovieMetaData.FilmPoster poster = getFilmDetail(film.projectId);
                if (poster != null)
                    film.setFilmPoster(poster);
                result.add(film);
            }
            //DemoJSONData data = gson.fromJson(object, MovieMetaData..class);
        }


        return result;
    }

    private static CastHeadShot getHeadShot(List<NameValuePair> params) throws JSONException, IOException {

        String result = HttpHelper.getFromUrl(BASELINE_DOMAIN + Endpoints.GetHeadshot, params);
        //JSONObject json = new JSONObject(result);
        JSONArray jsonArray = new JSONArray(result);
        if (jsonArray != null) {
            JSONObject object = (JSONObject) jsonArray.get(0);
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(object.toString(), CastHeadShot.class);

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
