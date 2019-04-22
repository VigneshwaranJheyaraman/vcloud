package app.cloud.vrajinc.v_cloud;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Toast;

import java.util.List;

import app.cloud.vrajinc.v_cloud.Adapter.UserAllFilesAdapter;
import app.cloud.vrajinc.v_cloud.DataClasses.VrajUserFile;
import app.cloud.vrajinc.v_cloud.DataClasses.VrajUserFileDBHelper;

public class User_All_FilesActivity extends AppCompatActivity {
    RecyclerView user_all_files_recycler_view;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    List<VrajUserFile> users_all_files;
    VrajUserFileDBHelper vrajUserFileDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__all__files);
        initviews();
    }

    private void initviews() {
        Intent from_account =getIntent();
        String uname = from_account.getStringExtra("uname");

        //This is an example
        //Toast.makeText(this,tname,Toast.LENGTH_LONG).show();

        user_all_files_recycler_view = (RecyclerView) findViewById(R.id.user_all_files_recycler_view);
        user_all_files_recycler_view.setHasFixedSize(true);
        vrajUserFileDBHelper = new VrajUserFileDBHelper(User_All_FilesActivity.this);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        user_all_files_recycler_view.setLayoutManager(staggeredGridLayoutManager);
        //user all files from database created on AsyncTask
        users_all_files = vrajUserFileDBHelper.getAllfiles(uname);
        UserAllFilesAdapter userAllFilesAdapter = new UserAllFilesAdapter(User_All_FilesActivity.this,users_all_files);
        user_all_files_recycler_view.setAdapter(userAllFilesAdapter);
    }
}
