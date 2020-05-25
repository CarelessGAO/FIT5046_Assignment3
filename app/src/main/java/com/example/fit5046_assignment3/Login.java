package com.example.fit5046_assignment3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Login extends AppCompatActivity {
    private EditText edit_UserName;
    private EditText edit_PassWord;
    private CheckBox ck_Username;
    public static int userid;
    public static Person personInfo = new Person("GetInformation");
    SharedPreferences sp;

    public static final String BASE_URL = "http://172.16.11.251:8080/Assignment/webresources/";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sp = getSharedPreferences("personInfo",MODE_PRIVATE);
        Button b_Login = (Button) findViewById(R.id.Login);
        edit_UserName = (EditText) findViewById(R.id.edit_UserName);
        edit_PassWord = (EditText) findViewById(R.id.edit_PassWord);
        ck_Username = (CheckBox) findViewById(R.id.ck_remUsername);
        if (sp.getString("rememberUsername","").equals("true")){
            ck_Username.setChecked(true);
        }
        if (ck_Username.isChecked()){
            edit_UserName.setText(sp.getString("username",""));
        }
        b_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncLogin().execute(edit_UserName.getText().toString(),edit_PassWord.getText().toString());
            }
        });
    }
    //To do the Async part, the first String will transfer to "doInBackground". the Second String will transfer to "On Post Execute"
    private class AsyncLogin extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String textResult = "";
            String username = strings[0];
            String password = strings[1];
            String md5 = MD5.md5(password);
            URL url = null;
            HttpURLConnection connect = null;
            final String methodPath = "assignment.credential/findByUsernameAndPasswordHash/";
            try{
                url = new URL(BASE_URL + methodPath + username + "/" + md5);
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
            try {
                //find the current userid
                String replace = textResult.replace("[", "").replace("]", "");
                JSONObject jsonOb1 = new JSONObject(replace);
                String objectUserid = jsonOb1.getString("credentialId");//credentialId equals personid in database
                JSONObject jsonOb2 = new JSONObject(objectUserid);
                userid = Integer.parseInt(jsonOb2.getString("credentialId"));
                String uid = jsonOb2.getString("credentialId");
                SharedPreferences sharedPreferences = Login.this.getSharedPreferences(uid,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("firstname",jsonOb2.getString("firstName"));
                editor.putString("email",username+"@gmail.com");
                editor.commit();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return textResult;
        }

        protected void onPostExecute(String textResult) {
            super.onPostExecute(textResult);
            if(textResult.equals("[]")){
                Toast.makeText(Login.this,"Incorrect, please type again", Toast.LENGTH_SHORT).show();
            }
            else{
                new AsyncGetUser().execute(userid);
                if (ck_Username.isChecked()){
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username",edit_UserName.getText().toString());
                    editor.putString("rememberUsername","true");
                    editor.commit();
                }else {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("rememberUsername","false");
                    editor.commit();
                }
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    public void signUp(View v){
        Intent intent = new Intent(Login.this,Signup.class);
        startActivity(intent);
    }

    private class AsyncGetUser extends AsyncTask<Integer,Void,Person>{
        @Override
        protected Person doInBackground(Integer... userid) {
            String textResult = "";
            URL url = null;
            HttpURLConnection connect = null;
            final String methodPath = "assignment.person/findByPersonId/" + userid[0];
            try{
                url = new URL(BASE_URL + methodPath);
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
            String add = "";
            try {
                JSONArray jsonArray = new JSONArray(textResult);
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                personInfo.setFirstName(jsonObject.getString("firstName"));
                personInfo.setSurname(jsonObject.getString("surname"));
                personInfo.setAddress(jsonObject.getString("address"));
                personInfo.setPostcode(jsonObject.getString("postcode"));
                personInfo.setDob(jsonObject.getString("dob"));
                personInfo.setGender(jsonObject.getString("gender"));
                personInfo.setState(jsonObject.getString("state"));
                personInfo.setPersonId(jsonObject.getString("personId"));
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }
    }

}
