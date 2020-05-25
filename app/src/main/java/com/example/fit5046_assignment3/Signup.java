package com.example.fit5046_assignment3;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Signup extends AppCompatActivity {
    private ScrollView scrollView;
    private Map<String, EditText> etMap = new HashMap<String, EditText>();
    private TextView tv_datepick;
    private Button b_submit;
    private Button b_datepick;
    private String gender = "M";
    private Spinner sp_state;
    private String state;
    private String DOB;
    private int uid;
    private int exitFlag = 0;
    private String signupDate;
    private List<String> username = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        scrollView = (ScrollView) findViewById(R.id.sv_scrollView);
        b_submit = (Button) findViewById(R.id.b_submit);
        b_datepick = (Button) findViewById(R.id.b_datepick);
        etMap.put("et_newUserName",(EditText) findViewById(R.id.et_newUserName));
        etMap.put("et_newPassword",(EditText) findViewById(R.id.et_newPassword));
        etMap.put("et_confirmPassword",(EditText) findViewById(R.id.et_confirmPassword));
        etMap.put("et_first_name",(EditText) findViewById(R.id.et_first_name));
        etMap.put("et_last_name",(EditText) findViewById(R.id.et_last_name));
        etMap.put("et_address",(EditText) findViewById(R.id.et_address));
        etMap.put("et_postcode",(EditText) findViewById(R.id.et_postcode));
        tv_datepick = (TextView) findViewById(R.id.tv_datepick);
        sp_state = (Spinner) findViewById(R.id.sp_state);
        String userName = etMap.get("et_newUserName").getText().toString();
        new AsyncGetUsername().execute();

        //Gender selected(Button selected)
        RadioGroup radgroup = (RadioGroup) findViewById(R.id.radioGroup);
        radgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radbtn = (RadioButton) findViewById(checkedId);
                if (radbtn.getText().equals("Male")) {
                    gender = "M";
                } else {
                    gender = "F";
                }
            }
        });
        //state selected(spinner)
        List<String> list = new ArrayList<String>();
        list.add("NSW");
        list.add("VIC");
        list.add("QLD");
        list.add("ACT");
        list.add("SA");
        list.add("WA");
        list.add("TAS");
        list.add("NT");
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_state.setAdapter(spinnerAdapter);
        sp_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedState = parent.getItemAtPosition(position).toString();
                if (selectedState != null) {
                    state = selectedState;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //DataPicker - After clicked a button
        b_datepick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                int mmo = month + 1;
                String mm;
                if (mmo < 10) {
                    StringBuffer str = new StringBuffer("0");
                    mm = str.append(mmo).toString();
                } else {
                    mm = String.valueOf(mmo);
                }
                String dd = "";
                if (day < 10) {
                    StringBuffer str = new StringBuffer("0");
                    dd = str.append(day).toString();
                } else {
                    dd = String.valueOf(day);
                }
                String spDate = year + "-" + mm + "-" + dd;
                StringBuffer tt = new StringBuffer(spDate);
                signupDate = tt.append("T00:00:00+10:00").toString();
                DatePickerDialog datePicker = new DatePickerDialog(Signup.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        String mon = "";
                        int month = monthOfYear + 1;
                        if (month < 10) {
                            StringBuffer str = new StringBuffer("0");
                            mon = str.append(month).toString();
                        } else {
                            mon = String.valueOf(month);
                        }
                        String day = "";
                        if (dayOfMonth < 10) {
                            StringBuffer str = new StringBuffer("0");
                            day = str.append(dayOfMonth).toString();
                        } else {
                            day = String.valueOf(dayOfMonth);
                        }
                        String dob = year + "-" + mon + "-" + day;
                        tv_datepick.setText(dob);
                        StringBuffer temp = new StringBuffer(dob);
                        DOB = temp.append("T00:00:00+10:00").toString();
                    }
                }, year, month, day);
                DatePicker dp = datePicker.getDatePicker();
                dp.setMaxDate(System.currentTimeMillis());
                datePicker.show();
            }
        });
        //Submit button
        b_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean IsSame = false; //assessment for same username
                String newusername = etMap.get("et_newUserName").getText().toString();
                for (int i = 0; i < username.size(); i++) {
                    if (newusername.equals(username.get(i))) {
                        IsSame = true;
                        etMap.get("et_newUserName").setError("The username is exist");
                        //Toast.makeText(Signup.this, "The username is exist, please enter again", Toast.LENGTH_SHORT).show();
                    }
                }
                if (etMap.get("et_newPassword").getText().toString().equals(etMap.get("et_confirmPassword").getText().toString())) {
                    if ( !IsSame) {
                        Person users = new Person();
                        Credential credential = new Credential();
                        users.setFirstName(etMap.get("et_first_name").getText().toString());
                        users.setSurname(etMap.get("et_last_name").getText().toString());
                        users.setAddress(etMap.get("et_address").getText().toString());
                        users.setPostcode(etMap.get("et_postcode").getText().toString());
                        users.setDob(DOB);
                        users.setGender(gender);
                        users.setState(state);
                        credential.setPasswordHash(MD5.md5(etMap.get("et_confirmPassword").getText().toString()));
                        credential.setUserName(etMap.get("et_newUserName").getText().toString());
                        credential.setSignUpDate(signupDate);
                        users.setCredential(credential);
                        new AsyncPostCredential().execute(credential);
                        new AsyncPostUser().execute(users);
                        /*while (exitFlag != 5) {
                            if (exitFlag == 1) {
                                new AsyncGetUserid().execute(users);
                                exitFlag = 5;
                            }
                        }*/
                        Intent intent = new Intent(Signup.this, Login.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(Signup.this, "There are some mistakes, please check again", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    etMap.get("et_confirmPassword").setError("The password is not match");
                    //Toast.makeText(Signup.this, "The password is not match, please enter again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private class AsyncGetUsername extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... strings) {
            String textResult = "";
            URL url = null;
            HttpURLConnection conn = null;
            final String methodPath = "assignment.credential/findAllUsername";
            try {
                url = new URL(Login.BASE_URL + methodPath);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            //read json file and add it to an array of username
            try {
                JSONArray jsonArray = new JSONArray(textResult);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String temp = jsonObject.getString("username");
                    username.add(temp);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private class AsyncPostUser extends AsyncTask<Person, Void, Person> {
        @Override
        protected Person doInBackground(Person... users) {
            URL url = null;
            HttpURLConnection conn = null;
            final String methodPath = "assignment.person/";
            try {
                Gson gson = new Gson();
                String stringCourseJson = gson.toJson(users[0]);
                //replace the "[]" in Gson format
                String replace = stringCourseJson.replace("[", "").replace("]", "");
                url = new URL(Login.BASE_URL + methodPath);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(replace.getBytes().length);
                conn.setRequestProperty("Content-Type", "application/json");
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(replace);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            exitFlag = 1;
            return users[0];
        }
    }

   /* private class AsyncGetUserid extends AsyncTask<Person, Void, Person> {
        @Override
        protected Person doInBackground(Person... users) {
            String textResult = "";
            URL url = null;
            HttpURLConnection conn = null;
            final String methodPath = "a2.users/getNewUserid";
            try {
                url = new URL(Login.BASE_URL + methodPath);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            try {
                JSONObject jsonObject = new JSONObject(textResult);
                users[0].setPersonId(jsonObject.getString("personid") );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return users[0];
        }
        protected void onPostExecute(Person users) {
            Credential credential = new Credential();
            //credential.setUserid(users);
            credential.setPasswordHash(etMap.get("et_confirmPassword").getText().toString());
            credential.setUserName(etMap.get("et_newUserName").getText().toString());
            credential.setSignUpDate(signupDate);
            new AsyncPostCredential().execute(credential);
        }
    }*/

    private class AsyncPostCredential extends AsyncTask<Credential, Void, Void> {
        @Override
        protected Void doInBackground(Credential... credential) {
            URL url = null;
            HttpURLConnection conn = null;
            final String methodPath = "assignment.credential/";
            try {
                Gson gson = new Gson();
                String stringCourseJson = gson.toJson(credential);
                String a = stringCourseJson.replace("[", "").replace("]", "");
                url = new URL(Login.BASE_URL + methodPath);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(a.getBytes().length);
                conn.setRequestProperty("Content-Type", "application/json");
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(a);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return null;
        }
    }



}
