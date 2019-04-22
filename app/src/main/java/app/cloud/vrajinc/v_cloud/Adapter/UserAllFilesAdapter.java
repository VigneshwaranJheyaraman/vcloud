package app.cloud.vrajinc.v_cloud.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import app.cloud.vrajinc.v_cloud.DataClasses.VrajUserFile;
import app.cloud.vrajinc.v_cloud.R;

public class UserAllFilesAdapter extends RecyclerView.Adapter<UserAllFilesAdapter.UserAllFilesViewHolder> {
    List<VrajUserFile> users_all_files_list;
    Context context;
    ProgressDialog progressDialog;
    String SERVRER_URL = "http://vrajinc.ddns.net:8080";
    public UserAllFilesAdapter(Context context, List<VrajUserFile> users_all_files_list)
    {
        this.context = context;
        this.users_all_files_list = users_all_files_list;
    }
    @NonNull
    @Override
    public UserAllFilesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_all_files_card,viewGroup,false);
        UserAllFilesViewHolder userAllFilesViewHolder = new UserAllFilesViewHolder(v);
        return userAllFilesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserAllFilesViewHolder userAllFilesViewHolder, final int i) {
        userAllFilesViewHolder.user_file_name.setText(users_all_files_list.get(i).getFile_name());
        Picasso.get().load(SERVRER_URL+users_all_files_list.get(i).getFile_location()).into(userAllFilesViewHolder.user_file_image);
        userAllFilesViewHolder.user_file_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternetAvailable())
                {
                    if(!new File(Environment.getExternalStorageDirectory().toString()+FileDownloadToLocalStorageTask.APP_DIRECTORY_NAME+"/"+users_all_files_list.get(i).getFile_name()).exists())
                    {
                        FileDownloadToLocalStorageTask fileDownloadToLocalStorageTask = new FileDownloadToLocalStorageTask();
                        fileDownloadToLocalStorageTask.execute(SERVRER_URL+users_all_files_list.get(i).getFile_location(),users_all_files_list.get(i).getFile_name());
                    }
                    else {
                        Toast.makeText(context,"File already Download.",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(context,"Bruhhhhhh... No Internet.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean checkInternetAvailable()
    {
        ConnectivityManager connectivityManager =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo internetAvailability = connectivityManager.getActiveNetworkInfo();
        if(internetAvailability != null)
        {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return this.users_all_files_list.size();
    }

    public class UserAllFilesViewHolder extends RecyclerView.ViewHolder
    {
        ImageView user_file_image;
        TextView user_file_name;

        public UserAllFilesViewHolder(@NonNull View itemView) {
            super(itemView);
            user_file_image = (ImageView) itemView.findViewById(R.id.user_file_image);
            user_file_name = (TextView) itemView.findViewById(R.id.user_file_name);
        }
    }

    private void showProgressDialog(int id) {
        switch (id)
        {
            case 0:
                progressDialog= new ProgressDialog(context);
                progressDialog.setMessage("Hold on a sec...");
                progressDialog.setIndeterminate(false);
                progressDialog.setMax(100);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(true);
                progressDialog.show();
        }
    }
    public  class FileDownloadToLocalStorageTask extends AsyncTask<String,String,String> {
        int horizontal_progress_bar = 0;
        public static final String APP_DIRECTORY_NAME = "/V-Cloud";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(horizontal_progress_bar);
        }

        @Override
        protected String doInBackground(String... strings) {
            int count;
            try
            {
                URL file_download_url = new URL(strings[0]);
                URLConnection file_download_connection = file_download_url.openConnection();
                file_download_connection.connect();
                int size_of_the_file = file_download_connection.getContentLength();
                InputStream inputStream_file = new BufferedInputStream(file_download_url.openStream(),8192);
                File path = new File(Environment.getExternalStorageDirectory().toString()+APP_DIRECTORY_NAME);
                if(!path.exists())
                    path.mkdirs();
                OutputStream download_file_outputstream = new FileOutputStream(path.getAbsolutePath()+"/"+strings[1]);
                byte data[] = new byte[1024];
                long total = 0L;
                while((count = inputStream_file.read(data)) != -1)
                {
                    total  += count;
                    publishProgress(""+(int) ((total * 100)/size_of_the_file));
                    download_file_outputstream.write(data,0,count);
                }
                download_file_outputstream.flush();
                download_file_outputstream.close();
                inputStream_file.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();

                //this is an example
                Log.i("file download error",e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            Toast.makeText(context,"Download Completed",Toast.LENGTH_LONG).show();
        }
    }
}
