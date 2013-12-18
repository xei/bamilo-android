//package pt.rocket.framework.database;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import pt.rocket.framework.objects.Section;
//import android.content.ContentValues;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import de.akquinet.android.androlog.Log;
//
///**
// * @author ivanschuetz
// */
//public class SectionsTablesHelper {
//	
//	private static final String TAG = SectionsTablesHelper.class.getSimpleName();
//	
//	public static final String TABLE = "section";
//	
//	public static interface Columns {
//		String ID = "id";
//		String NAME = "name";
//		String MD5 = "md5";
//		String URL = "url";
//	}
//    
//    public static final String CREATE = 
//    		"CREATE TABLE " + TABLE + " (" + 
//    				Columns.ID +			" INTEGER PRIMARY KEY, " + 
//    				Columns.NAME +			" TEXT UNIQUE, " + 
//    				Columns.MD5 +			" TEXT, " + 
//    				Columns.URL +			" TEXT" + 
//    				")";
//    
//    public static interface Projection {
//    	int ID = 0;
//    	int NAME = 1;
//    	int MD5 = 2;
//    	int URL = 3;
//    }
//    
//    /**
//     * Save sections to database
//     * this replaces existent entry on unique conflict (the row will get a new id)
//     * 
//     * @param shopBys
//     */
//    public static void saveSections(List<Section> sections) {
//    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
//
//    	try {
//    		db.beginTransaction();
//    		
//    		for (Section section : sections) {
//    			saveSection(db, section);
//    		}
//	    	db.setTransactionSuccessful();
//
//    	} catch (SQLException e) {
//    		Log.e(e.getMessage());
//    		e.printStackTrace();
//    		
//    	}
//    	finally {
//    		db.endTransaction();
//    	}
//    }
//    
//    /**
//     * Save a section to database
//     * this replaces existent entry on unique conflict (the row will get a new id)
//     * 
//     * @param db writable database instance
//     * @param shopBy
//     */
//	public static void saveSection(SQLiteDatabase db, Section section) {
//    	ContentValues contentValues = new ContentValues();
//    	contentValues.put(Columns.NAME, section.getName());
//    	contentValues.put(Columns.MD5, section.getMd5());
//    	contentValues.put(Columns.URL, section.getUrl());
//
//    	db.insertWithOnConflict(TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
//	}
//	
//	public static List<Section> getSections() {
//		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
//		
//		Cursor cursor = db.query(TABLE, new String[] {
//				Columns.ID,
//				Columns.NAME,
//				Columns.MD5,
//				Columns.URL
//		}, 
//		null, null, null, null, null);
//		
//		List<Section> sections = new ArrayList<Section>();
//    	
//		while (cursor.moveToNext()) {
//			String name = cursor.getString(Projection.NAME);
//			String md5 = cursor.getString(Projection.MD5);
//			String url = cursor.getString(Projection.URL);
//			
//			sections.add(new Section(name, md5, url));
//		}
//		cursor.close();
//		
//		return sections;
//	}
//}