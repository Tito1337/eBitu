package be.webtito.ebitu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Momed on 23/04/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static String DATABASE_PATH = "/data/data/be.webtito.ebitu/databases/";
    private static final String DATABASE_NAME = "eBitu.DB";
    private SQLiteDatabase myDatabase;
    private final Context myContext;

    public static final String TABLE_NAME = "eBitu_Chants";
//    public static final String COL_1 = "ID";
//    public static final String COL_2 = "Title";
//    public static final String COL_3 = "Lyric";
//    public static final String COL_4 = "Date_Selected";
//    public static final String COL_5 = "Faved";


/*Constructeur*/
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.myContext = context;
        //SQLiteDatabase db = this.getWritableDatabase();
    }

/*Creation d'une nouvelle DB et Copie des données àpd la Db préinstallée dans les assets*/
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //Ne rien faire
        }else{

            //A l'appel de cette méthode, la Db sera créée
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    //Vérifier si la Db existe pour éviter qu'elle soit recréée à chaque fois qu'on ouvre l'appli
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //La Db n'existe pas encore.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    //Copier la Db du dossier Assets vers Celle qui vient d'être créée
    private void copyDataBase() throws IOException{

        //On ouvre la db des chants dans l'input stream
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

        // Chemin de la nouvelle Db vide
        String outFileName = DATABASE_PATH + DATABASE_NAME;

        //On ouvre la Db vide dans l'output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //On transfère les bytes de l'inputfile à l'outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //On flush puis on ferme les streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //ouvrir la db
        String myPath = DATABASE_PATH + DATABASE_NAME;
        myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDatabase != null)
            myDatabase.close();

        super.close();

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
       // db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Title TEXT,Lyric TEXT,Date_Selected DATETIME,Faved INTEGER) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String Title, String Lyric, String Date_Selected, String Faved){
       /* SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,Title);
        contentValues.put(COL_3,Lyric);
        contentValues.put(COL_4,Date_Selected);
        contentValues.put(COL_5,Faved);

        */
        return true;
    }

    public Cursor getTitlesList() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select Title from "+ TABLE_NAME,null);
        return res;
    }
}
