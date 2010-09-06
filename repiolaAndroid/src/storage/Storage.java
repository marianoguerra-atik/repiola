/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 *
 * @author Mateo
 */
public class Storage extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "repiola_db";
    private static final String SCRIPTS_TABLE_NAME = "repiola_scripts";
    private static final String SCRIPTS_NAME_COLUMN = "SCRIPT_NAME";
    private static final String SCRIPTS_CONTENT_COLUMN = "SCRIPT_CONTENT";
    private static final String SCRIPTS_TABLE_CREATE =
                "CREATE TABLE " + SCRIPTS_TABLE_NAME +
                " ( ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SCRIPTS_NAME_COLUMN + " TEXT, " +
                SCRIPTS_CONTENT_COLUMN + " TEXT);";
    private SQLiteDatabase db;
    private static Storage instance;
    
    private Storage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCRIPTS_TABLE_CREATE);
    }
    
    @Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// Don't do nothing, I don't care.
    }
	
	public String load(String name)
    {
		Cursor c = db.query(SCRIPTS_TABLE_NAME, 
				new String[] {"SCRIPT_CONTENT"},
				"SCRIPT_NAME = ?",
				new String[] {name},
				null, null, null);
		
		if(c.getCount() != 1) {
            return null;
        } else {
        	c.moveToFirst();
        	return c.getString(0);
        }
    }

    public boolean save(String name, String text)
    {
    	Integer id = this.getIfExists(name);
    	long result;
    	
        if(id == -1) {
        	ContentValues values = new ContentValues();
        	values.put(SCRIPTS_NAME_COLUMN, name);
        	values.put(SCRIPTS_CONTENT_COLUMN, text);
        	result = db.insert(SCRIPTS_TABLE_NAME, null, values);
        } else {
        	ContentValues values = new ContentValues();
        	values.put(SCRIPTS_CONTENT_COLUMN, text);
        	result = db.update(SCRIPTS_TABLE_NAME, values, "ID = ?", new String[] {id.toString()});
        }

        if (result != -1) {
        	System.out.println("stored record " + result);
            return true;
        } else {
        	System.err.println("Could not set record");
            return false;
        }
    }

    private int getIfExists(String name)
    {
    	Cursor c = db.query(SCRIPTS_TABLE_NAME, 
				new String[] {"ID"},
				"SCRIPT_NAME = ?",
				new String[] {name},
				null, null, null);
		
		if(c.getCount() != 1) {
            return -1;
        } else {
        	c.moveToFirst();
        	return c.getInt(0);
        }
    }
    
    public static synchronized Storage getInstance(Context context) {
    	if(instance == null) {
    		instance = new Storage(context);
    	}
    	return instance;
    }
}
