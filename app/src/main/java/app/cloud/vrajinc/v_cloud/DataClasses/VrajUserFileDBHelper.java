package app.cloud.vrajinc.v_cloud.DataClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class VrajUserFileDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "VrajUserFiles.db";
    public static final String TABLE_NAME = "vrajuserfile";
    public static final String USR_FILE_COLUMN_USER_NAME = "user_name";
    public static final String USR_FILE_COLUMN_FILE_NAME = "file_name";
    public static final String USR_FILE_COLUMN_FILE_LOC = "file_loc";
    public static final String USR_FILE_COLUMN_FILE_UP_DATE = "file_upload_date";

    public VrajUserFileDBHelper(Context context)
    {
        super(context,DB_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+
                "(id integer primary key,"+USR_FILE_COLUMN_USER_NAME+" text,"+USR_FILE_COLUMN_FILE_NAME+" text, "+USR_FILE_COLUMN_FILE_LOC+" text, "+USR_FILE_COLUMN_FILE_UP_DATE+" text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP table "+TABLE_NAME);
        onCreate(db);
    }
    public void insertFiles(String uname, String fname,String floc,String up_date)
    {
        SQLiteDatabase current_db = this.getWritableDatabase();
        ContentValues db_contents = new ContentValues();
        db_contents.put(USR_FILE_COLUMN_USER_NAME, uname);
        db_contents.put(USR_FILE_COLUMN_FILE_NAME,fname);
        db_contents.put(USR_FILE_COLUMN_FILE_LOC,floc);
        db_contents.put(USR_FILE_COLUMN_FILE_UP_DATE,up_date);
        current_db.insert(TABLE_NAME,null,db_contents);
    }
    public int isInsertionPossible(String file_name)
    {
        Cursor cursor = null;
        SQLiteDatabase db= this.getReadableDatabase();
        try
        {
            cursor= db.rawQuery("SELECT count(*) from "+TABLE_NAME+" WHERE "+USR_FILE_COLUMN_FILE_NAME+"= ?",new String[] {file_name});
            if(cursor.moveToFirst())
            {
                return cursor.getInt(0);
            }
            return 0;
        }finally {
            if(cursor != null)
                cursor.close();
            if(db != null)
                db.close();
        }
    }
    public List<VrajUserFile> getAllfiles(String uname)
    {
        List<VrajUserFile> vrajUserFileList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from "+TABLE_NAME+" where "+ USR_FILE_COLUMN_USER_NAME+" = ?",new String[] {uname});
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false)
        {
            vrajUserFileList.add(
                    new VrajUserFile
                            (cursor.getString(cursor.getColumnIndex(USR_FILE_COLUMN_USER_NAME)),
                                    cursor.getString(cursor.getColumnIndex(USR_FILE_COLUMN_FILE_NAME)),
                                            cursor.getString(cursor.getColumnIndex(USR_FILE_COLUMN_FILE_LOC)),
                                                    cursor.getString(cursor.getColumnIndex(USR_FILE_COLUMN_FILE_UP_DATE))
                            )
            );
            cursor.moveToNext();
        }

        //This is an example
        Log.i("Database progress",vrajUserFileList.get(0).toString());
        return vrajUserFileList;
    }


}
