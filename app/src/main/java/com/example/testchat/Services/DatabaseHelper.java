package com.example.testchat.Services;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.testchat.Models.User;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "aieuxScope";

    // Table Names
    private static final String TABLE_USER = "user";

    // Common column names
    private static final String KEY_ID = "id";
    // private static final String KEY_CREATED_AT = "created_at";

    // NOTES Table - column names
    private static final String COLUMN_NAME_EMAIL = "email";

    private static final String COLUMN_NAME_PASSWORD= "password";
    //emergency table





    // Table Create Statements
    // Todo table create statement
    private static final String CREATE_TABLE_USER = "CREATE TABLE "
            + TABLE_USER + "(" + KEY_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME_EMAIL
            + " TEXT," + COLUMN_NAME_PASSWORD + " TEXT)";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
           ContactContract.TABLE_NAME + "("+
           ContactContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +","+
            ContactContract.COLUMN_NAME + " TEXT NOT NULL" +","+
            ContactContract.COLUMN_CONTACT + " TEXT NOT NULL" + ");";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_USER);

        // creation emergency contact
        db.execSQL(SQL_CREATE_TABLE);

        db.execSQL("create Table UserInfo(poids TEXT, prises TEXT, periode TEXT,hauteur TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        //emergency contact
        db.execSQL("DROP TABLE IF EXISTS " +ContactContract.TABLE_NAME);
        // create new tables
        onCreate(db);
    }

    public boolean insertuserdata(String poids, String prises, String periode,String hauteur){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("poids", poids);
        contentValues.put("prises", prises);
        contentValues.put("periode", periode);
        contentValues.put("hauteur", hauteur);
        long result = DB.insert("UserInfo", null, contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getInfo(){

        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery("SELECT * FROM UserInfo ", null);
    }

    public void addUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_EMAIL, user.getEmail());
        values.put(COLUMN_NAME_PASSWORD, user.getPassword());
        long newRowId = db.insert(TABLE_USER, null, values);
    }
    @SuppressLint("Range")
    public User getUser() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_USER +";";
        Cursor c = db.rawQuery(selectQuery, null);
        try{
            User user = null;
            if (c != null) {
                c.moveToFirst();
                user = new User(c.getString(c.getColumnIndex(COLUMN_NAME_EMAIL)),
                        c.getString(c.getColumnIndex(COLUMN_NAME_PASSWORD)));
            }
            return user;
        }catch (Exception e){
            return null;

        }


    }



    public  long addNewContact(String contact,String number){
        SQLiteDatabase sql = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ContactContract.COLUMN_CONTACT,number);
        cv.put(ContactContract.COLUMN_NAME,contact);
        return sql.insert(ContactContract.TABLE_NAME,null,cv);
    }

    public void ct(String contactremoveConta){
        SQLiteDatabase sql = this.getWritableDatabase();
        sql.delete(ContactContract.TABLE_NAME, "contact"+"=?",new String[]{contactremoveConta});
    }




    public void deleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_USER);
    }
}

