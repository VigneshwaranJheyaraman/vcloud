package app.cloud.vrajinc.v_cloud.DataClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class VrajUserDBHelpher extends SQLiteOpenHelper {
    public static final String DB_NAME = "VrajUsers.db";
    public static final String TABLE_NAME = "vrajuser";
    public static final String USERNAME_COL = "user_name";
    public static final String USERF_NAME_COL = "user_first_name";
    public static final String USERL_NAME_COL = "user_last_name";
    public static final String USER_EMAIL_COL = "user_email";
    public static final String USER_FACE_COL = "user_face";
    public static final String USER_ABOUT_ME_COL = "user_about_me";
    public static final String USER_WHO_AM_I_COL = "user_who_am_i";
    public static final String USER_AUTH_TOK_COL = "user_auth_token";
    public VrajUserDBHelpher(Context context)
    {
        super(context,DB_NAME,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ TABLE_NAME+
                "( id integer primary key,"+USERNAME_COL+" text not null,"+USERF_NAME_COL+" text not null,"+USERL_NAME_COL+" text not null,"+
                USER_EMAIL_COL+" text not null,"+USER_FACE_COL+" text not null,"+USER_ABOUT_ME_COL+" text not null,"+
                USER_WHO_AM_I_COL+" text not null,"+USER_AUTH_TOK_COL+" text not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP table "+TABLE_NAME);
        onCreate(db);
    }

    public void insertUser(String uname,String fname,String lname, String email, String my_face, String abt_me, String who_am_i, String auth_token)
    {
        SQLiteDatabase current_db = this.getWritableDatabase();
        ContentValues user_contents = new ContentValues();
        user_contents.put(USERNAME_COL,uname);
        user_contents.put(USERF_NAME_COL,fname);
        user_contents.put(USERL_NAME_COL,lname);
        user_contents.put(USER_EMAIL_COL,email);
        user_contents.put(USER_FACE_COL,my_face);
        user_contents.put(USER_ABOUT_ME_COL,abt_me);
        user_contents.put(USER_WHO_AM_I_COL,who_am_i);
        user_contents.put(USER_AUTH_TOK_COL,auth_token);
        current_db.insert(TABLE_NAME,null,user_contents);
    }
    public int doesUserExist(String username)
    {
        Cursor cursor = null;
        SQLiteDatabase db= this.getReadableDatabase();
        try
        {
            cursor= db.rawQuery("SELECT count(*) from "+TABLE_NAME+" WHERE "+USERNAME_COL+"= ?",new String[] {username});
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
    public VrajUser getUser(String auth_token)
    {
        VrajUser current_user;
        SQLiteDatabase user_db = this.getReadableDatabase();
        Cursor cursor = user_db.rawQuery("select * from "+TABLE_NAME+" where "+USER_AUTH_TOK_COL+"=?",new String[] {auth_token});
        cursor.moveToFirst();
        current_user = new VrajUser(
                cursor.getString(cursor.getColumnIndex(USERNAME_COL)),
                cursor.getString(cursor.getColumnIndex(USERF_NAME_COL)),
                cursor.getString(cursor.getColumnIndex(USERL_NAME_COL)),
                cursor.getString(cursor.getColumnIndex(USER_EMAIL_COL)),
                cursor.getString(cursor.getColumnIndex(USER_FACE_COL)),
                cursor.getString(cursor.getColumnIndex(USER_ABOUT_ME_COL)),
                cursor.getString(cursor.getColumnIndex(USER_WHO_AM_I_COL)),
                cursor.getString(cursor.getColumnIndex(USER_AUTH_TOK_COL))
        );

        //this is an example
        Log.i("user_db progress",current_user.toString());
        return current_user;
    }
}
