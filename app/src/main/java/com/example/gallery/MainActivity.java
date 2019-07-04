package com.example.gallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements AuthenticationDialog.AuthenticationListener {

    private String token = null;
    private AppPreferences appPreferences;
    private AuthenticationDialog dialog;
    private Button loginButton;
    private View info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.btn_login);
        info = findViewById(R.id.info);
        appPreferences = new AppPreferences(this);
        token = appPreferences.getString(AppPreferences.TOKEN);
        if (token!=null){
            getUserInfoByAccessToken(token);
        }
    }
    public void onClick(View view) {
        if(token!=null)
        {
            logout();
        }
        else {
            AuthenticationDialog authenticationDialog = new AuthenticationDialog(this, this);
            authenticationDialog.setCancelable(true);
            authenticationDialog.show();
        }
    }

    private void getUserInfoByAccessToken(String token) {
        new RequestInstagramAPI().execute();
    }


    @Override
    public void onTokenReceived(String authentication_token) {
        if (authentication_token == null)
            return;
        appPreferences.putString(AppPreferences.TOKEN, authentication_token);
        token = authentication_token;
        getUserInfo(token);
    }

    private void getUserInfo(String token){
        new RequestInstagramAPI().execute();
    }

    private class RequestInstagramAPI extends AsyncTask<Void, String, String>{

        @Override
        protected String doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(getResources().getString(R.string.get_user_info_url) + token);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response!=null){
                try {
                    JSONObject json = new JSONObject(response);
                    Log.e("response", json.toString());
                    JSONObject jsonData = json.getJSONObject("data");
                    if (jsonData.has("id")){
                        //сохранение данных пользователя
                        appPreferences.putString(AppPreferences.USER_ID, jsonData.getString("id"));
                        appPreferences.putString(AppPreferences.USER_NAME, jsonData.getString("username"));
                        appPreferences.putString(AppPreferences.PROFILE_PIC, jsonData.getString("profile_picture"));

                        login();
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Ошибка входа!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void login() {
        loginButton.setText("LOGOUT");
        info.setVisibility(View.VISIBLE);
        ImageView pic = findViewById(R.id.pic);
        Picasso.with(this).load(appPreferences.getString(AppPreferences.PROFILE_PIC)).into(pic);
        TextView id = findViewById(R.id.name);
        id.setText(appPreferences.getString(AppPreferences.USER_ID));
        TextView name = findViewById(R.id.name);
        name.setText(appPreferences.getString(AppPreferences.USER_NAME));
    }
    public void logout() {
        loginButton.setText("INSTAGRAM LOGIN");
        token = null;
        info.setVisibility(View.GONE);
        appPreferences.clear();
    }
}
