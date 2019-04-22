package app.cloud.vrajinc.v_cloud;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.cloud.vrajinc.v_cloud.Adapter.UserTopFilesAdapter;
import app.cloud.vrajinc.v_cloud.DataClasses.VrajUser;
import app.cloud.vrajinc.v_cloud.DataClasses.VrajUserDBHelpher;
import app.cloud.vrajinc.v_cloud.DataClasses.VrajUserFile;
import app.cloud.vrajinc.v_cloud.DataClasses.VrajUserFileDBHelper;

public class UserFileFragment_1 extends Fragment {
    String auth_token,user_id;
    FragmentCommunicator fragmentCommunicator;
    List<VrajUserFile> user_file_list;
    RecyclerView recyclerView;
    VrajUser current_vraj_user;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    VrajUserFileDBHelper vrajUserFileDBHelper;
    VrajUserDBHelpher vrajUserDBHelpher;
    ImageView noInternetImage;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.user_fragment_1,container,false);
        getIntentfromlogin();
        user_file_list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.top_userfiles_recycler_view);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        noInternetImage = view.findViewById(R.id.noInternetImage);
        vrajUserFileDBHelper = new VrajUserFileDBHelper(getContext());
        vrajUserDBHelpher = new VrajUserDBHelpher(getContext());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if(checkInternetAvailable())
        {
            FileDownloader fileDownloader = new FileDownloader();
            fileDownloader.execute("http://vrajinc.ddns.net:8080/vcloud_api/getuserfile/"+user_id+"/?format=json");
            createinstanceOfAdapter(user_file_list);
        }
        else
        {
            try
            {
                current_vraj_user = vrajUserDBHelpher.getUser(auth_token);
                user_file_list.clear();
                user_file_list = null;
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        fragmentCommunicator.sendUserDetail(current_vraj_user,user_file_list,user_id);
                    }
                };
                Handler h = new Handler();
                h.postDelayed(r,1000);
                showNoInternetImage();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    public void showNoInternetImage() {
        recyclerView.setVisibility(View.INVISIBLE);
        noInternetImage.setVisibility(View.VISIBLE);
    }


    public void createinstanceOfAdapter(List<VrajUserFile> user_file_list) {
        UserTopFilesAdapter userTopFilesAdapter = new UserTopFilesAdapter(getContext(),user_file_list);
        recyclerView.setAdapter(userTopFilesAdapter);
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

    public class FileDownloader extends AsyncTask<String,Void,String> {
        URL file_get_url;
        HttpURLConnection file_url_connection;
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder response = new StringBuilder();
            try {
                file_get_url = new URL(urls[0]);
                file_url_connection = (HttpURLConnection) file_get_url.openConnection();
                file_url_connection.setReadTimeout(15000);
                file_url_connection.setConnectTimeout(15000);
                String Header = "Token "+auth_token;

                //This is an example
                Log.i("User progress",Header);

                file_url_connection.setRequestProperty("Authorization","Token "+auth_token);
                file_url_connection.setRequestProperty("Content-Type","application/json");
                file_url_connection.setRequestMethod("GET");
                file_url_connection.setUseCaches(false);

                //The download process
                int response_code = file_url_connection.getResponseCode();

                //This is an example
                Log.i("progress post",Integer.toString(response_code));
                if(response_code == HttpURLConnection.HTTP_OK)
                {
                    InputStream inputStream = file_url_connection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    int data = inputStreamReader.read();
                    while(data != -1)
                    {
                        char c = (char) data;
                        response.append(c);
                        Log.i("char progress",Character.toString(c));
                        data = inputStreamReader.read();
                    }
                    Log.i("User Fragment1 progress",response.toString());
                }
                else
                {
                    Log.i("progress error",Integer.toString(response_code));
                    response = new StringBuilder("{user: "+response_code+"}");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.i("User Fragment 1 error",e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("User Fragment 1 error",e.getMessage());
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if(s != null)
                {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray user = jsonObject.getJSONArray("user");
                    JSONObject curr_user = user.getJSONObject(0);
                    current_vraj_user = new VrajUser(curr_user.getString("username"),curr_user.getString("first_name"),curr_user.getString("last_name"),curr_user.getString("email"),curr_user.getString("my_face"),curr_user.getString("about_me"),curr_user.getString("who_am_i"),auth_token);
                    try
                    {
                        if(vrajUserDBHelpher.doesUserExist(current_vraj_user.getUsername()) == 0)
                        {
                            vrajUserDBHelpher.insertUser(current_vraj_user.getUsername(),current_vraj_user.getFirst_name(),current_vraj_user.getLast_name(),current_vraj_user.getEmail(),current_vraj_user.getMy_face(),current_vraj_user.getAbout_me(),current_vraj_user.getWho_am_i(),current_vraj_user.getAuth_token());

                            //this is an example
                            Log.i("user db progress", "Database stored");
                        }
                    }
                    catch (Exception e)
                    {
                        //this is an example
                        Log.i("vraj userdb error",e.getMessage());
                    }
                    //This is an example
                    //Toast.makeText(getContext(),"Welcom to V-Cloud Mr. "+current_vraj_user.getUsername(),Toast.LENGTH_LONG).show();
                    Log.i("progress post",current_vraj_user.getFirst_name());

                    JSONArray userfiles = jsonObject.getJSONArray("userfiles");
                    for(int i =0;i<userfiles.length();i++)
                    {
                        JSONObject file = userfiles.getJSONObject(i);
                        VrajUserFile each_file = new VrajUserFile(current_vraj_user.getUsername(),file.getString("user_file_name"),file.getString("user_file_url"),file.getString("uploaded_date"));
                        try {
                            if(vrajUserFileDBHelper.isInsertionPossible(each_file.getFile_name()) == 0) {
                                vrajUserFileDBHelper.insertFiles(each_file.getUser(), each_file.getFile_name(), each_file.getFile_location(), each_file.getUploaded_date());
                            }
                        }catch (Exception e)
                        {

                            //This is an example
                            Log.i("Database error",e.getMessage());
                        }
                        if (i<10)
                        {
                            user_file_list.add(each_file);
                        }
                        else
                        {
                            continue;
                        }
                    }
                    fragmentCommunicator.sendUserDetail(current_vraj_user,user_file_list,user_id);
                }
                else
                {

                    //This is an example
                    //Toast.makeText(getContext(),"Kindly Upload files to view and access",Toast.LENGTH_LONG).show();
                    Log.i("Json progress","no files");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("json error",e.getMessage());
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try
        {
            fragmentCommunicator = (FragmentCommunicator) getActivity();
        }
        catch (Exception e)
        {

            //This is an example
            Log.i("send user detail error",e.getMessage());

        }
    }

    public interface FragmentCommunicator
    {
        void sendUserDetail(VrajUser vrajUser,List<VrajUserFile> vrajUserFileList,String user_id);
    }

    private void getIntentfromlogin() {
        Intent from_login = getActivity().getIntent();
        auth_token = from_login.getStringExtra(LoginActivity.SP_AUTH_KEY);
        user_id = from_login.getStringExtra(LoginActivity.SP_USER_ID);

        //This is an example and it works
        //Toast.makeText(getContext(),auth_token,Toast.LENGTH_LONG).show();
        //Toast.makeText(getContext(),user_pk,Toast.LENGTH_LONG).show();

    }

    private float dptoPixles(int i, UserFileFragment_1 userFileFragment_1) {
        return i * (userFileFragment_1.getResources().getDisplayMetrics().density);
    }
}
