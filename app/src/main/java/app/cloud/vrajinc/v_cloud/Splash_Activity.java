package app.cloud.vrajinc.v_cloud;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class Splash_Activity extends AppCompatActivity {
    ImageView splash_video;
    SharedPreferences login_pref;
    String auth_token,user_pk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_);
        initviews();
        if(auth_token !=null && user_pk != null) {
            start_splash_to_user();
        }
        else
        {
            start_splash_to_login();
        }
    }

    private void start_splash_to_user() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent to_user = new Intent(Splash_Activity.this,UserActivity.class);
                to_user.putExtra(LoginActivity.SP_AUTH_KEY,auth_token);
                to_user.putExtra(LoginActivity.SP_USER_ID,user_pk);
                startActivity(to_user);
                Splash_Activity.this.finish();
            }
        },2000);
    }

    private void start_splash_to_login() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Splash_Activity.this.startActivity(new Intent(Splash_Activity.this,LoginActivity.class));
                Splash_Activity.this.finish();
            }
        },2000);
    }

    private void initviews() {
        splash_video = (ImageView) findViewById(R.id.splash_video);
        login_pref = getSharedPreferences(LoginActivity.AUTH_PREFERENCES, Context.MODE_PRIVATE);
        auth_token = login_pref.getString(LoginActivity.SP_AUTH_KEY,null);
        user_pk = login_pref.getString(LoginActivity.SP_USER_ID,null);
    }
}
