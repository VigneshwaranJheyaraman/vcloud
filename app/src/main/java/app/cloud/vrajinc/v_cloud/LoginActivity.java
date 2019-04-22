package app.cloud.vrajinc.v_cloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button login_btn;
    ProgressDialog loading;
    public static final String API_URL = "http://vrajinc.ddns.net:8080/vcloud_api/";
    String API_TOKEN_URL = API_URL+"userauth/login/?format=json";
    static String USER_AUTH_TOKEN=null;
    static String USER_PRIMARY_INDEX=null;
    public static final String AUTH_PREFERENCES = "AuthenticationPreference";
    SharedPreferences login_details_pref;
    public static final String SP_AUTH_KEY ="auth_token";
    public static final String SP_USER_ID ="user_pk";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initviews();
        loginButtonClickListener();
    }

    private void loginButtonClickListener() {
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u_name = username.getText().toString();
                String u_pwd = password.getText().toString();
                AuthTokenDownloader atd = new AuthTokenDownloader();
                try
                {
                    Toast.makeText(getApplicationContext(),"The process is started",Toast.LENGTH_SHORT).show();
                    atd.execute(API_TOKEN_URL,username.getText().toString(),password.getText().toString());
                    loadProgress();
                }
                catch (Exception e){
                    e.printStackTrace();
                    Log.i("Execute error",e.getMessage());
                }
            }
        });
    }

    private void initviews() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login_btn = (Button) findViewById(R.id.login_button);
        login_details_pref = getSharedPreferences(AUTH_PREFERENCES, Context.MODE_PRIVATE);
        loading = new ProgressDialog(this);
    }
    public class AuthTokenDownloader extends AsyncTask<String,Void,String>{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try
            {
                JSONObject userauth = new JSONObject(s);
                USER_AUTH_TOKEN = userauth.getString("key");
                USER_PRIMARY_INDEX = userauth.getString("pk");
                Log.i("post execute progress",USER_AUTH_TOKEN);
                Log.i("post execute progress",USER_PRIMARY_INDEX);
                if(USER_AUTH_TOKEN == Integer.toString(200))
                {
                    loading.setMessage("Sorry your details aren't valid, Please provide the legitimate credentials");
                }
                else
                {
                    loading.setMessage("Hurray!!!, Welcome to V-Cloud");
                    SharedPreferences.Editor login_pref_editor = login_details_pref.edit();
                    login_pref_editor.putString(SP_AUTH_KEY,USER_AUTH_TOKEN);
                    login_pref_editor.putString(SP_USER_ID,USER_PRIMARY_INDEX);
                    login_pref_editor.apply();
                    Intent to_user_file = new Intent(getApplicationContext(),UserActivity.class);
                    to_user_file.putExtra(SP_AUTH_KEY,USER_AUTH_TOKEN);
                    to_user_file.putExtra(SP_USER_ID,USER_PRIMARY_INDEX);
                    to_user_file.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(to_user_file);
                }
                loading.dismiss();

                //This is an example and it works
                //Toast.makeText(getApplicationContext(),USER_AUTH_TOKEN,Toast.LENGTH_LONG).show();

            }
            catch (Exception e){
                e.printStackTrace();
                Log.i("Post Execute error",e.getMessage());
            }
        }

        @Override
        protected String doInBackground(String... input_urls) {
            StringBuffer result= new StringBuffer("");
            try {
                URL api_url = new URL(input_urls[0]);
                JSONObject post_data = new JSONObject();
                post_data.put("username",input_urls[1]);
                post_data.put("password",input_urls[2]);
                HttpURLConnection apiURLConnection = (HttpURLConnection) api_url.openConnection();
                apiURLConnection.setReadTimeout(15000);
                apiURLConnection.setConnectTimeout(15000);
                apiURLConnection.setRequestMethod("POST");
                apiURLConnection.setDoInput(true);
                apiURLConnection.setDoOutput(true);
                OutputStream output_post_data = apiURLConnection.getOutputStream();
                BufferedWriter out_post_data_writer = new BufferedWriter(new OutputStreamWriter(output_post_data,"UTF-8"));
                out_post_data_writer.write(getPostDataString(post_data));
                out_post_data_writer.flush();
                out_post_data_writer.close();
                output_post_data.close();
                Log.i("background progress",output_post_data.toString());
                //Toast.makeText(getApplicationContext(),"The process is background",Toast.LENGTH_SHORT).show();
                // Response
                int response_code = apiURLConnection.getResponseCode();
                if(response_code == HttpURLConnection.HTTP_OK)
                {
                    Log.i("verification progress",Integer.toString(response_code));
                    //Toast.makeText(getApplicationContext(),"The input is valid",Toast.LENGTH_SHORT).show();
                    BufferedReader input_reader = new BufferedReader(new InputStreamReader(apiURLConnection.getInputStream()));
                    String line ="";
                    while ((line = input_reader.readLine()) != null)
                    {
                        result.append(line);
                        break;
                    }
                    input_reader.close();
                    Log.i("bg2 progress",result.toString());
                }
                else
                {
                    result = new StringBuffer("key: "+response_code);
                    loading.setProgress(0);
                }
                apiURLConnection.disconnect();
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.i("background error",e.getMessage());
            }
            return result.toString();
        }
    }
    private void loadProgress(){
        loading.setMax(100);
        loading.setCanceledOnTouchOutside(false);
        loading.setMessage("Kindly, wait as the details are being verified");
        loading.setTitle("Verification and Validation");
        loading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        loading.show();
        final Handler loading_thread_handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                loading.incrementProgressBy(1);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    while(loading.getProgress() <= loading.getMax())
                    {
                        Thread.sleep(200);
                        loading_thread_handler.sendMessage(loading_thread_handler.obtainMessage());
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.i("progress error",e.getMessage());
                }
            }
        }).start();
    }
    public static String getPostDataString(JSONObject post_data) throws Exception{
        StringBuilder post_data_format = new StringBuilder();
        boolean first_data = true;
        Iterator<String> iterator = post_data.keys();
        while(iterator.hasNext())
        {
            String key = iterator.next();
            Object value = post_data.get(key);
            if(first_data)
            {
                first_data=false;
            }
            else
            {
                post_data_format.append("&");
            }
            post_data_format.append(URLEncoder.encode(key,"UTF-8"));
            post_data_format.append("=");
            post_data_format.append(URLEncoder.encode(value.toString(),"UTF-8"));
        }
        return post_data_format.toString();
    }
}
