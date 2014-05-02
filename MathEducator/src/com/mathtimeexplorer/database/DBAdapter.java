package com.mathtimeexplorer.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.mathtimeexplorer.main.User;
import com.mathtimeexplorer.utils.Constants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.*;
import android.util.Log;

public class DBAdapter extends SQLiteOpenHelper{

		//The Android's default system path of your application database.
		private static String DB_PATH = "/data/data/com.example.matheducator/databases/";
		private static String DB_NAME = "appdata";
		private SQLiteDatabase myDataBase;
		private final Context myContext;

		/**
		 * Constructor
		 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
		 * @param context
		 */
		public DBAdapter(Context context) {
			super(context, DB_NAME, null, 1);
			this.myContext = context;
		}	
		
		/**
		 * Creates a empty database on the system and rewrites it with your own database.
		 * */
		public void createDataBase() throws IOException{
			boolean dbExist = checkDataBase();
			if(dbExist){
				//do nothing - database already exist
			}else{
				
				//By calling this method and empty database will be created into the default system path
				//of your application so we are gonna be able to overwrite that database with our database.
				this.getReadableDatabase();
				try {
					copyDataBase();
				} catch (IOException e) {
					Log.i(Constants.LOG_DBADAPTER, e.toString());
				}
			}
		}

		/**
		 * Check if the database already exist to avoid re-copying the file each time you open the application.
		 * @return true if it exists, false if it doesn't
		 */
		private boolean checkDataBase(){

			SQLiteDatabase checkDB = null;
			try{
				String myPath = DB_PATH + DB_NAME;
				checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
			}catch(SQLiteException e){
				Log.i(Constants.LOG_DBADAPTER, e.toString());
			}
			if(checkDB != null){
				checkDB.close();
			}
			return checkDB != null ? true : false;
		}

		/**
		 * Copies your database from your local assets-folder to the just created empty database in the
		 * system folder, from where it can be accessed and handled.
		 * This is done by transfering bytestream.
		 * */
		private void copyDataBase() throws IOException{
			//Open your local db as the input stream
			InputStream myInput = myContext.getAssets().open(DB_NAME);

			// Path to the just created empty db
			String outFileName = DB_PATH + DB_NAME;

			//Open the empty db as the output stream
			OutputStream myOutput = new FileOutputStream(outFileName);

			//transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer))>0){
				myOutput.write(buffer, 0, length);
			}

			//Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();
		}

		public void openDataBase() throws SQLException{
			//Open the database
			String myPath = DB_PATH + DB_NAME;
			myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		}

		@Override
		public synchronized void close() {
			if(myDataBase != null)
				myDataBase.close();
			super.close();
		}

		@Override
		public void onCreate(SQLiteDatabase db) {}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

		// Add your public helper methods to access and get content from the database.
		// You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
		// to you to create adapters for your views.
		public Cursor getQuestions() {		 
			Cursor cursor = getReadableDatabase().query("Question",		 
					new String[] { "_id", "question", "a","b","c","d","answer","explanation","mark",
					"image_path","quiz_id","sub_topic_id"},		 
					null, null, null, null, null);		 
			return cursor;
		}
		
		// Insert user details into User table
		public void addUser(User user) {
			myDataBase = getWritableDatabase();
			
			// Save data into content values
			ContentValues values = new ContentValues();
			values.put(Constants.LOGIN_USER_ID, user.getApp_user_id());
			values.put(Constants.LOGIN_FIRST_NAME, user.getFirst_name());
			values.put(Constants.LOGIN_LAST_NAME, user.getLast_name());
			values.put(Constants.LOGIN_GENDER, user.getGender());
			values.put(Constants.LOGIN_CLASS_ID, user.getClass_id());
			values.put(Constants.LOGIN_SCHOOL_ID, user.getSchool_id());
			values.put(Constants.LOGIN_EDULEVEL, user.getEduLevel());
			
			// Insert the contents into the database
			myDataBase.insert(Constants.LOGIN_USER, null, values);
			myDataBase.close();
		}
		
		// Checks whether any user exists in the database
		public User isUserExists() {
			User user = null;
			myDataBase = getReadableDatabase();
			String query = "SELECT * FROM " + Constants.LOGIN_USER;
			Cursor cursor = myDataBase.rawQuery(query, null);
			cursor.moveToFirst();
			
			// If row count > 0, means a user exists in the database
			if (cursor.getCount() > 0) {
				user = new User();
				user.setApp_user_id(cursor.getInt(0));
				user.setFirst_name(cursor.getString(1));
				user.setLast_name(cursor.getString(2));
				user.setGender(cursor.getString(3));
				user.setClass_id(cursor.getInt(4));
				user.setSchool_id(cursor.getInt(5));
				user.setEduLevel(cursor.getInt(6));
			} 
			cursor.close();
			myDataBase.close();
			return user;
		}
		
		// Delete user data when logout
		public void deleteUser() {
			myDataBase = getWritableDatabase();
			
			// Delete everything from User table
			String query = "DELETE FROM " + Constants.LOGIN_USER;
			myDataBase.execSQL(query);
			myDataBase.close();
		}
}
