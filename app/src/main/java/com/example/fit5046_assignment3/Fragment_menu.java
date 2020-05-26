package com.example.fit5046_assignment3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.SimpleTimeZone;

import static com.example.fit5046_assignment3.Login.BASE_URL;

public class Fragment_menu extends Fragment {
    private View menu;
    private TextView welcome;
    private TextView currentDate;
    private ListView movieList;
    private List<HashMap<String,String>> movieListArray = new ArrayList<>();
    //public static WatchListDatabase database;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        menu = inflater.inflate(R.layout.fragment_main, container, false);
        movieList = menu.findViewById(R.id.listView);
        new AsyncTopFive().execute();
        welcome=menu.findViewById(R.id.welcome);
        currentDate=menu.findViewById(R.id.currentDate);
        SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy");
        Date date = new Date(System.currentTimeMillis());
        currentDate.setText(format.format(date));
        //database ;
        SharedPreferences sp = getActivity().getSharedPreferences(String.valueOf(Login.userid), Context.MODE_PRIVATE);
        String name = sp.getString("firstname","");
        welcome.setText("WelCome ! " + name);
        return menu;
    }

    private class AsyncTopFive extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... voids) {
            String textResult = "";
            URL url = null;
            HttpURLConnection connect = null;
            final String methodPath = "assignment.memoir/findMovieNameReleaseDateAndRatingScoreByPersonId/";
            try{
                url = new URL(BASE_URL + methodPath + Login.userid);
                connect = (HttpURLConnection) url.openConnection();
                connect.setReadTimeout(10000);
                connect.setConnectTimeout(15000);
                connect.setRequestMethod("GET");
                connect.setRequestProperty("Content-Type","application/json");
                connect.setRequestProperty("Accept","application/json");
                Scanner inStream = new Scanner(connect.getInputStream());
                while (inStream.hasNextLine()){
                    textResult += inStream.nextLine();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            //close connection
            finally {
                connect.disconnect();
            }
            return textResult;
        }

        @Override
        protected void onPostExecute(String textResult) {
            super.onPostExecute(textResult);
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(textResult);
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    HashMap<String,String> map = new HashMap<>();
                    map.put("Movie Name",jsonObject.getString("memoirName"));
                    map.put("Release Date",jsonObject.getString("releaseDate"));
                    map.put("Rating Score",jsonObject.getString("ratingScore"));
                    movieListArray.add(map);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            String[] colum = new String[]{"Movie Name","Release Date", "Rating Score"};
            int[] cell = new int[]{R.id.movienameList,R.id.dateList,R.id.scoreList};
            SimpleAdapter movieList = new SimpleAdapter(Fragment_menu.this.getActivity(),movieListArray,R.layout.,)
        }
    }

}
