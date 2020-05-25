package com.example.fit5046_assignment3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Login extends AppCompatActivity {
    private EditText edit_UserName;
    private EditText edit_PassWord;

    public static final String BASE_URL = "http://172.16.11.251:8080/Assignment/webresources/";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button b_Login = (Button) findViewById(R.id.Login);
        edit_UserName = (EditText) findViewById(R.id.edit_UserName);
        edit_PassWord = (EditText) findViewById(R.id.edit_PassWord);
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
            URL url = null;
            HttpURLConnection connect = null;
            final String methodPath = "assignment.credential/findByUsernameAndPasswordHash/";
            try{
                url = new URL(BASE_URL + methodPath + username + "/" + password);
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

        protected void onPostExecute(String textResult) {
            super.onPostExecute(textResult);
            if(textResult.equals("[]")){
                Toast.makeText(Login.this,"Incorrect, please type again", Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    public void signUp(View v){
        Intent intent = new Intent(Login.this,Signup.class);
        startActivity(intent);
    }

}
