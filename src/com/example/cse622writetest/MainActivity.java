package com.example.cse622writetest;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private TextView tv;
	private EditText et;
	private static final String fileName = "myfile";
	private static final String key = "key";
	private static final String value = "value";
	int bytesPerMb = 1024;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.textView1);
		et = (EditText) findViewById(R.id.editText1);
		tv.setText("Backup Text App");
		et.setText("Size in Mb");
		
	}
	
	public void clearText(View v) {
		et.setText("");
	}
	
	public void filesWriteTest(View v) {
		
		FileOutputStream outputStream;
		tv.setText("");
		int size = Integer.parseInt(et.getText().toString());
		
		try {
				
			byte[] b = new byte[size * bytesPerMb];
			Random random = new Random();
			random.nextBytes(b);
			
			long tick = System.currentTimeMillis();
			
		    outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
		    outputStream.write(b);
		    outputStream.close();
		    
		    long tock = System.currentTimeMillis();
		    
		    tv.append("Wrote: " + size + " Mb in " + (tock-tick) + " ms. ");
		    Log.i("benchmark", "FILE WRITE: " + size + " Mb took " + (tock-tick) + " ms");
			
		} catch (Exception e) {
			tv.setText("Unable to write file: " + e.getMessage());
		}
		
	}
	
	public void filesReadTest(View v) {
		
		FileInputStream inputStream;
		tv.setText("");
		int size = Integer.parseInt(et.getText().toString());
		
		try {
				
			byte[] fileData = new byte[size * bytesPerMb];
			
			long tick = System.currentTimeMillis();
		
		    inputStream = openFileInput(fileName);
		    DataInputStream dis = new DataInputStream(inputStream);
		    dis.readFully(fileData);
		    inputStream.close();
		    
		    long tock = System.currentTimeMillis();
		    
	    	tv.append("Read: " + size + " Mb in " + (tock-tick) + " ms. ");
		    Log.i("benchmark", "FILE READ: " + size + " Mb took " + (tock-tick) + " ms");
		    
		} catch (Exception e) {
			tv.setText("Unable to read file: " + e.getMessage());
		}
		
	}
	
	public void sharedPrefsWriteTest(View v) {
		
		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(key, value);
		editor.commit();
		
		tv.setText("Set '" + key + "' to '" + value + "'");
		
	}
	
	public void sharedPrefsReadTest(View v) {
		
		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		String value = sharedPref.getString(key, null);
		
		if (value == null) tv.setText("Value for '" + key + "' not set");
		else tv.setText("Value for '" + key + "' set to '" + value + "'");
		
	}
	
	public void databaseWriteTest(View v) {
	    
	    FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this);
	    SQLiteDatabase db = mDbHelper.getWritableDatabase();
	    ContentValues values = new ContentValues();
	    
	    int id = 1;
	    String name = "name1";
	    
	    values.put("id", 1);
	    values.put("name", "name1");
	    db.insert("foo", "null", values);
	    tv.setText("Wrote id '" + id + "' with name '" + name + "' to database file");   
		
	}
	
	public void databaseReadTest(View v) {
		
		FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		String[] projection = { "id", "name" };
		
		Cursor cursor = db.query( "foo", projection, null, null, null, null, null);
		
		cursor.moveToFirst();
		int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
		String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
		
		tv.setText("Read id '" + id + "' with name '" + name + "' from database file");
		
	}
	
	// write to database
    class FeedReaderDbHelper extends SQLiteOpenHelper {
	    
	    public static final int DATABASE_VERSION = 1;
	    public static final String DATABASE_NAME = "mydatabase.db";
	    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE foo (id INTEGER, name TEXT)";
	    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS foo";

	    public FeedReaderDbHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	    public void onCreate(SQLiteDatabase db) {
	        db.execSQL(SQL_CREATE_ENTRIES);
	    }
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        db.execSQL(SQL_DELETE_ENTRIES);
	        onCreate(db);
	    }
	    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        onUpgrade(db, oldVersion, newVersion);
	    }
	}
	
}
