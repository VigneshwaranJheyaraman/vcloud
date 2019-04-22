package app.cloud.vrajinc.v_cloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.cloud.vrajinc.v_cloud.DataClasses.VrajUser;
import app.cloud.vrajinc.v_cloud.DataClasses.VrajUserFile;

public class UserAccountFragment_2 extends Fragment {
    TextView user_username,user_email_id, user_who_am_i, user_about_me,user_full_name;
    ImageView user_profile_pic;
    ImageButton user_file_view_btn, user_storage_sense_btn, user_logout_btn;
    Button user_file_upload_btn;
    String SERVER_URL ="http://vrajinc.ddns.net:8080";
    List<VrajUserFile> vrajUserFileList;
    FilesCommunicator user_file_communicator;
    VrajUser user;
    ProgressDialog progressDialog;
    String user_id;
    public static final int GALLERY_IMAGE_INTENT =1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_fragment_2,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vrajUserFileList = new ArrayList<>();
        user_username =  (TextView) view.findViewById(R.id.user_username);
        user_email_id = (TextView) view.findViewById(R.id.user_email_id);
        user_who_am_i = (TextView) view.findViewById(R.id.user_who_am_i);
        user_about_me = (TextView) view.findViewById(R.id.user_about_me);
        user_full_name = (TextView) view.findViewById(R.id.user_full_name);
        user_file_view_btn = (ImageButton) view.findViewById(R.id.user_files_btn);
        user_storage_sense_btn = (ImageButton) view.findViewById(R.id.user_storage_btn);
        user_logout_btn = (ImageButton) view.findViewById(R.id.user_logout_btn);
        user_profile_pic = (ImageView) view.findViewById(R.id.user_profile_pic);
        user_file_upload_btn = (Button) view.findViewById(R.id.user_file_upload);
        buttonlistener();
    }

    public void setViewsValue(VrajUser current_user, List<VrajUserFile> vrajUserFiles,String user_id) {
        user_username.setText(current_user.getUsername());
        user_email_id.setText(current_user.getEmail());
        user_full_name.setText(current_user.getFirst_name()+" "+current_user.getLast_name());
        user_who_am_i.setText(current_user.getWho_am_i());
        user_about_me.setText(current_user.about_me);
        Picasso.get().load(SERVER_URL+current_user.getMy_face()).into(user_profile_pic);
        this.vrajUserFileList = vrajUserFiles;
        this.user = current_user;
        this.user_id = user_id;
        if(vrajUserFileList != null)
            user_file_communicator.sendFilesto_files_fragment(vrajUserFileList);
        else
            user_file_communicator.sendFilesto_files_fragment(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try
        {
            user_file_communicator =(FilesCommunicator) getActivity();
        }
        catch (Exception e)
        {

            //this is an example
            e.printStackTrace();
            Log.i("file_comm error",e.getMessage());

        }
    }

    interface FilesCommunicator
    {
        void sendFilesto_files_fragment(List<VrajUserFile> vrajUserFileList);
    }

    public boolean checkInternetAvailable()
    {
        ConnectivityManager connectivityManager =  (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo internetAvailability = connectivityManager.getActiveNetworkInfo();
        if(internetAvailability != null)
        {
            return true;
        }
        else {
            return false;
        }
    }
    private void buttonlistener() {
        user_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternetAvailable())
                {
                    UserAccountLogoutTask userAccountLogoutTask = new UserAccountLogoutTask();
                    userAccountLogoutTask.execute(LoginActivity.API_URL+"userauth/logout/?format=json");
                }
                else
                {
                    Toast.makeText(getContext(),"Bruhhhhh.... No Internet",Toast.LENGTH_LONG).show();
                }
            }
        });
        user_file_view_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_all_file_activity =  new Intent(getContext(),User_All_FilesActivity.class);
                to_all_file_activity.putExtra("uname",user_username.getText());
                startActivity(to_all_file_activity);
            }
        });
        user_storage_sense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Under construction",Toast.LENGTH_LONG).show();
            }
        });
        user_file_upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_gallery = new Intent();
                to_gallery.setType("image/*");
                to_gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(to_gallery,"Select an image to upload to V-Cloud"),GALLERY_IMAGE_INTENT);
            }
        });
    }

    private void showProgressDialog(int id) {
        switch (id) {
            case 0:
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Hold on a sec...");
                progressDialog.setIndeterminate(false);
                progressDialog.setMax(100);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(true);
                progressDialog.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GALLERY_IMAGE_INTENT)
        {
            Toast.makeText(getContext(),"Sorry Currently under Construction",Toast.LENGTH_LONG).show();
        }
    }

    public class UserAccountLogoutTask extends AsyncTask<String,Void,String>
    {
        URL url;
        HttpURLConnection httpURLConnection;
        StringBuilder result = new StringBuilder();
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try
            {
                JSONObject logout_response = new JSONObject(s);
                if(logout_response.getString("detail") != null)
                {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(LoginActivity.AUTH_PREFERENCES,Context.MODE_PRIVATE).edit();
                    editor.remove(LoginActivity.SP_AUTH_KEY);
                    editor.remove(LoginActivity.SP_USER_ID);
                    editor.apply();
                    Intent to_login = new Intent(getActivity(),LoginActivity.class);
                    to_login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(to_login);
                }
            }catch (Exception e)
            {
                //this is an example
                Log.i("logout error",e.getMessage());
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL(strings[0]);
                JSONObject post_data = new JSONObject();
                post_data.put("username",user.getUsername());
                post_data.put("password","***");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                OutputStream post_data_output_stream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(post_data_output_stream,"UTF-8"));
                bufferedWriter.write(LoginActivity.getPostDataString(post_data));
                bufferedWriter.flush();
                bufferedWriter.close();
                post_data_output_stream.close();
                int response_code = httpURLConnection.getResponseCode();
                if(response_code  == HttpURLConnection.HTTP_OK)
                {
                    BufferedReader input_stream_reader = new BufferedReader(new InputStreamReader( httpURLConnection.getInputStream()));
                    String line ="";
                    while((line = input_stream_reader.readLine()) != null)
                    {
                        result.append(line);
                        break;
                    }
                    input_stream_reader.close();
                    Log.i("logout progress", result.toString());
                }
                else
                {
                    result = new StringBuilder("key :"+response_code);
                }
                httpURLConnection.disconnect();

            }catch (Exception e)
            {

                //This is an example
                Log.i("logout error", e.getMessage());
            }
            return result.toString();
        }
    }


}
