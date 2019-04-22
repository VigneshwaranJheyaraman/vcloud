package app.cloud.vrajinc.v_cloud.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Random;

import app.cloud.vrajinc.v_cloud.DataClasses.VrajUserFile;
import app.cloud.vrajinc.v_cloud.ImageViewer_Zoomed;
import app.cloud.vrajinc.v_cloud.R;

public class UserTopFilesAdapter extends RecyclerView.Adapter<UserTopFilesAdapter.UserFileViewHolder> {
    List<VrajUserFile> userFileArrayList;
    ProgressDialog progressDialog;
    Context current_context;
    public static final String API_URL ="http://vrajinc.ddns.net:8080";
    public UserTopFilesAdapter(Context context, List<VrajUserFile> userFileArrayList)
    {
        this.current_context = context;
        this.userFileArrayList = userFileArrayList;
    }
    @NonNull
    @Override
    public UserFileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = (View) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_top_files_card,viewGroup,false);
        CardView relativeLayout =(CardView) v.findViewById(R.id.user_file_layout);
        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(5,getRandomColor());
        gd.setCornerRadius(10f);
        relativeLayout.setBackground(gd);
        UserFileViewHolder userFileViewHolder = new UserFileViewHolder(v);
        return userFileViewHolder;
    }

    private int getRandomColor() {
        Random random = new Random();
        int color = Color.argb(255,random.nextInt(),random.nextInt(),random.nextInt());
        return color;
    }

    @NonNull
    @Override
    public void onBindViewHolder(final UserFileViewHolder viewHolder, final int i) {
        viewHolder.file_title.setText(userFileArrayList.get(i).getFile_name());
        final String file_detail = "This file was uploaded by "+userFileArrayList.get(i).getUser()+" at "+userFileArrayList.get(i).getUploaded_date();
        viewHolder.file_detail.setText(file_detail);
        if (userFileArrayList.get(i).getFile_name().toLowerCase().contains("jpg") ||userFileArrayList.get(i).getFile_name().toLowerCase().contains("jpeg") ||userFileArrayList.get(i).getFile_name().toLowerCase().contains("png"))
        {
            viewHolder.file_img.setImageResource(R.drawable.img);
            viewHolder.file_img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new ImageViewer_Zoomed().display(v.getContext(),viewHolder.file_img,userFileArrayList.get(i).getFile_location());
                    return false;
                }
            });
        }
        else if(userFileArrayList.get(i).getFile_name().toLowerCase().contains("mp4") ||userFileArrayList.get(i).getFile_name().toLowerCase().contains("avi")||userFileArrayList.get(i).getFile_name().toLowerCase().contains("gif")||userFileArrayList.get(i).getFile_name().toLowerCase().contains("mkv"))
        {
            viewHolder.file_img.setImageResource(R.drawable.mp4);
        }
        else {
            viewHolder.file_img.setImageResource(R.drawable.docx);
        }
        viewHolder.file_download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!new File(Environment.getExternalStorageDirectory().toString()+FileDownloadToLocalStorageTask.APP_DIRECTORY_NAME+"/"+userFileArrayList.get(i).getFile_name()).exists())
                {
                    FileDownloadToLocalStorageTask fileDownloadToLocalStorageTask = new FileDownloadToLocalStorageTask();
                    fileDownloadToLocalStorageTask.execute(API_URL+userFileArrayList.get(i).getFile_location(),userFileArrayList.get(i).getFile_name());
                }
                else
                {
                    Toast.makeText(current_context,"File already Downloaded.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userFileArrayList.size();
    }
    public class UserFileViewHolder extends RecyclerView.ViewHolder
    {
        TextView file_title,file_detail;
        ImageView file_img;
        ImageButton file_download_btn;
        public UserFileViewHolder(@NonNull View itemView) {
            super(itemView);
            file_title = (TextView) itemView.findViewById(R.id.file_title);
            file_detail = (TextView) itemView.findViewById(R.id.file_detail);
            file_img = (ImageView) itemView.findViewById(R.id.file_img);
            file_download_btn = (ImageButton) itemView.findViewById(R.id.file_download);
        }
    }

    private void showProgressDialog(int id) {
        switch (id)
        {
            case 0:
                progressDialog= new ProgressDialog(current_context);
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
            Toast.makeText(current_context,"Download Completed",Toast.LENGTH_LONG).show();
        }
    }
}
