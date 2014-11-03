package pt.rocket.framework.database;

import java.util.ArrayList;
import java.util.List;

import pt.rocket.framework.database.DarwinDatabaseHelper.TableType;
import pt.rocket.framework.objects.Section;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import de.akquinet.android.androlog.Log;

/**
 * @author ivanschuetz
 * @modified sergiopereira
 */
public class SectionsTablesHelper extends BaseTable {
	
	protected static final String TAG = SectionsTablesHelper.class.getSimpleName();
	
	public static final String TABLE_NAME = "section";
	
	public static interface Columns {
		String ID = "id";
		String NAME = "name";
		String MD5 = "md5";
		String URL = "url";
	}
    
    public static interface Projection {
    	int ID = 0;
    	int NAME = 1;
    	int MD5 = 2;
    	int URL = 3;
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.framework.database.BaseTable#getUpgradeType()
     */
    @Override
    public TableType getUpgradeType() {
        return TableType.FREEZE;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.framework.database.BaseTable#getName()
     */
    @Override
    public String getName() {        
        return TABLE_NAME;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.framework.database.BaseTable#create(java.lang.String)
     */
    @Override
    public String create(String table) {
        return "CREATE TABLE " + table + " (" + 
                Columns.ID +            " INTEGER PRIMARY KEY, " + 
                Columns.NAME +          " TEXT UNIQUE, " + 
                Columns.MD5 +           " TEXT, " + 
                Columns.URL +           " TEXT" +
                ")";
    }
    
    /*
     * ################## CRUD ##################  
     */
    
    /**
     * Save sections to database
     * this replaces existent entry on unique conflict (the row will get a new id)
     * 
     * @param shopBys
     */
    public static void saveSections(List<Section> sections) {
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();

    	try {
    		db.beginTransaction();
    		
    		for (Section section : sections) {
    			saveSection(db, section);
    		}
	    	db.setTransactionSuccessful();

    	} catch (SQLException e) {
    		Log.e(e.getMessage());
    		e.printStackTrace();
    		
    	}
    	finally {
    		db.endTransaction();
    	}
    }
    
    /**
     * Save a section to database
     * this replaces existent entry on unique conflict (the row will get a new id)
     * 
     * @param db writable database instance
     * @param shopBy
     */
	public static void saveSection(SQLiteDatabase db, Section section) {
    	ContentValues contentValues = new ContentValues();
    	contentValues.put(Columns.NAME, section.getName());
    	contentValues.put(Columns.MD5, section.getMd5());
    	contentValues.put(Columns.URL, section.getUrl());

    	db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
	}
	
	public static List<Section> getSections() {
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_NAME, new String[] {
				Columns.ID,
				Columns.NAME,
				Columns.MD5,
				Columns.URL
		}, 
		null, null, null, null, null);
		
		List<Section> sections = new ArrayList<Section>();
    	
		while (cursor.moveToNext()) {
			String name = cursor.getString(Projection.NAME);
			String md5 = cursor.getString(Projection.MD5);
			String url = cursor.getString(Projection.URL);
			
			sections.add(new Section(name, md5, url));
		}
		cursor.close();
		
		return sections;
	}
    
}