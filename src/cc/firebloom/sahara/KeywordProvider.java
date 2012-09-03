/**
 * 
 */
package cc.firebloom.sahara;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * @author snow.hellsing@gmail.com
 *
 */
public class KeywordProvider extends ContentProvider {
	
	private static final String TAG = "KeywordProvider";
	
	/**
     * The database that the provider uses as its underlying data store
     */
    private static final String DATABASE_NAME = "keyword.db";

    /**
     * The database version
     */
    private static final int DATABASE_VERSION = 1;
    
    /**
     * A projection map used to select columns from the database
     */
    private static HashMap<String, String> sKeywordsProjectionMap;
    
    /**
     * Standard projection for the interesting columns of a normal keyword.
     */
    private static final String[] READ_KEYWORD_PROJECTION = new String[] {
            SaharaContract.Keywords._ID,               // Projection position 0, the keyword's id
            SaharaContract.Keywords.COLUMN_NAME_TEXT,  // Projection position 1, the keyword's text          
    };
    private static final int READ_KEYWORD_TEXT_INDEX = 1;
    
    /*
     * Constants used by the Uri matcher to choose an action based on the pattern
     * of the incoming URI
     */
    // The incoming URI matches the Keywords URI pattern
    private static final int KEYWORDS = 1;

    // The incoming URI matches the Keyword ID URI pattern
    private static final int KEYWORD_ID = 2;
	
    /**
     * A UriMatcher instance
     */
    private static final UriMatcher sUriMatcher;

    // Handle to a new DatabaseHelper.
    private DatabaseHelper mOpenHelper;
    
    static class DatabaseHelper extends SQLiteOpenHelper {
    	
    	DatabaseHelper(Context context) {

            // calls the super constructor, requesting the default cursor factory.
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
    	
    	/**
        *
        * Creates the underlying database with table name and column names taken from the
        * NotePad class.
        */
       @Override
       public void onCreate(SQLiteDatabase db) {
           db.execSQL("CREATE TABLE " + SaharaContract.Keywords.TABLE_NAME + " ("
                   + SaharaContract.Keywords._ID + " INTEGER PRIMARY KEY,"
                   + SaharaContract.Keywords.COLUMN_NAME_TEXT + " TEXT,"
                   + SaharaContract.Keywords.COLUMN_NAME_CREATE_DATE + " INTEGER,"
                   + SaharaContract.Keywords.COLUMN_NAME_MODIFICATION_DATE + " INTEGER"
                   + ");");
       }
       
       /**
       *
       * Demonstrates that the provider must consider what happens when the
       * underlying datastore is changed. In this sample, the database is upgraded the database
       * by destroying the existing data.
       * A real application should upgrade the database in place.
       */
      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

          // Logs that the database is being upgraded
          Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                  + newVersion + ", which will destroy all old data");

          // Kills the table and existing data
          db.execSQL("DROP TABLE IF EXISTS notes");

          // Recreates the database with a new version
          onCreate(db);
      }
    }
	
    /**
     * A block that instantiates and sets static objects
     */
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(SaharaContract.AUTHORITY, SaharaContract.Keywords.TABLE_NAME, KEYWORDS);
		sUriMatcher.addURI(SaharaContract.AUTHORITY, SaharaContract.Keywords.TABLE_NAME + "/#", KEYWORD_ID);
		
		sKeywordsProjectionMap.put(
				SaharaContract.Keywords._ID, 
				SaharaContract.Keywords._ID);
		
		sKeywordsProjectionMap.put(
				SaharaContract.Keywords.COLUMN_NAME_TEXT, 
				SaharaContract.Keywords.COLUMN_NAME_TEXT);
		
		sKeywordsProjectionMap.put(
				SaharaContract.Keywords.COLUMN_NAME_CREATE_DATE, 
				SaharaContract.Keywords.COLUMN_NAME_CREATE_DATE);
		
		sKeywordsProjectionMap.put(
				SaharaContract.Keywords.COLUMN_NAME_MODIFICATION_DATE, 
				SaharaContract.Keywords.COLUMN_NAME_MODIFICATION_DATE);
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
