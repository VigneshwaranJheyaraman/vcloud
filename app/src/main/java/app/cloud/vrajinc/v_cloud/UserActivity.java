package app.cloud.vrajinc.v_cloud;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import app.cloud.vrajinc.v_cloud.Adapter.TabAdapter;
import app.cloud.vrajinc.v_cloud.DataClasses.VrajUser;
import app.cloud.vrajinc.v_cloud.DataClasses.VrajUserFile;

public class UserActivity extends AppCompatActivity implements UserFileFragment_1.FragmentCommunicator , UserAccountFragment_2.FilesCommunicator{
    UserFileFragment_1 file_fragment;
    TabAdapter tabAdapter;
    TabLayout tabLayout;
    ViewPager tab_view_pager;
    UserAccountFragment_2 accountFragment;
    int[] tabIcons = { R.drawable.cloud_file,R.drawable.cloud_account};
    String[] all_permissions = {Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    int ALL_PERMISSION =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        tab_view_pager = (ViewPager) findViewById(R.id.TabviewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabAdapter = new TabAdapter(getSupportFragmentManager());
        file_fragment = new UserFileFragment_1();
        accountFragment =  new UserAccountFragment_2();
        tabAdapter.addFragmenttoList(file_fragment,"V-Files");
        tabAdapter.addFragmenttoList(accountFragment,"Account and Settings");
        tab_view_pager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(tab_view_pager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        checkForAllPermissions();
    }


    private void checkForAllPermissions() {
        if(!hasAllPermission(this,all_permissions))
        {
            ActivityCompat.requestPermissions(this,all_permissions,ALL_PERMISSION);
        }
    }

    private boolean hasAllPermission(Context context, String[] all_permissions) {
        if(context !=null && all_permissions !=null)
        {
            for(String permission : all_permissions)
            {
                if(ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void sendUserDetail(VrajUser vrajUser, List<VrajUserFile> vrajUserFileList,String user_id) {
        accountFragment.setViewsValue(vrajUser,vrajUserFileList,user_id);
    }

    @Override
    public void sendFilesto_files_fragment(List<VrajUserFile> vrajUserFileList) {
        if(vrajUserFileList != null)
            file_fragment.createinstanceOfAdapter(vrajUserFileList);
        else
            file_fragment.showNoInternetImage();
    }
}
