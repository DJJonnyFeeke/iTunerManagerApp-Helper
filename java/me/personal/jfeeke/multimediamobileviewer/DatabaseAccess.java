package me.personal.jfeeke.multimediamobileviewer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.javahelps.externalsqliteimporter.ExternalSQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class DatabaseAccess {
    private ExternalSQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    /**
     * Private constructor to avoid object creation from outside classes.
     *
     * @param context         the Context
     * @param sourceDirectory optional external directory
     * * @return the DatabaseAccess
     */
    private DatabaseAccess(Context context, String sourceDirectory) {
        if (sourceDirectory == null) {
            this.openHelper = new DatabaseOpenHelper(context);
        } else {
            this.openHelper = new DatabaseOpenHelper(context, sourceDirectory);
        }
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context         the Context
     * @param sourceDirectory optional external directory
     * @return the instance of DatabaseAccess
     */
    static DatabaseAccess getInstance(MainActivity context, String sourceDirectory) {
        if (instance == null) {
            instance = new DatabaseAccess(context, sourceDirectory);
        }
        return instance;
    }

    static DatabaseAccess getMappingInstance(BusyActivity context, String sourceDirectory) {
        if (instance == null) {
            instance = new DatabaseAccess(context, sourceDirectory);
        }
        return instance;
    }
    /**
     * Open the database connection.
     */
    void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    void close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
     * Read all data from the database.
     *
     * @return the List of data
     */
    ArrayList<List<String>> getData(String searchText) {
        ArrayList<List<String>> arrayList = new ArrayList<>();
        List<String> stringList;// = new ArrayList<>();
        String sqLiteStatement = "SELECT * FROM iTunesLibrary";
        if(!Objects.equals(searchText, ""))
        {
            //fields to retrieve
            //0,1,3,8,12,13,16
            Cursor fieldCursor = database.rawQuery(sqLiteStatement, null);
            StringBuilder fieldsString = new StringBuilder(" (");
            for (int x = 0;x < fieldCursor.getColumnCount();x++)
            {
                if(x == 1 || x == 2 || x == 3 || x == 4 || x == 8 || x == 16 || x == 17) {
                    if(x==17)
                    {
                        fieldsString.append(fieldCursor.getColumnName(x)).append(" LIKE '%").append(searchText).append("%')");
                    }
                    else {
                        fieldsString.append(fieldCursor.getColumnName(x)).append(" LIKE '%").append(searchText).append("%'").append(") OR (");
                    }
                }
            }
            fieldCursor.close();
            sqLiteStatement = "SELECT * FROM iTunesLibrary WHERE " + fieldsString;
        }
        Cursor cursor = database.rawQuery(sqLiteStatement, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            stringList = new ArrayList<>();
            for (int i = 0;i < cursor.getColumnCount();i++) {
                if(cursor.getString(i) == null)
                {
                    stringList.add("NoData");
                }
                else {
                    stringList.add(cursor.getString(i));
                }
            }
            arrayList.add(stringList);
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
    }

    ArrayList<List<String>> getDataRow(int rowID) {
        ArrayList<List<String>> arrayList = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM iTunesLibrary WHERE id =" + rowID, null);
        for (int x = 0;x < cursor.getColumnCount();x++)
        {
            stringList.add(cursor.getColumnName(x));
        }
        arrayList.add(stringList);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            stringList = new ArrayList<>();
            for (int i = 0;i < cursor.getColumnCount();i++) {
                if(cursor.getString(i) == null)
                {
                    stringList.add("NoData");
                }
                else {
                    stringList.add(cursor.getString(i));
                }
            }
            arrayList.add(stringList);
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
    }

    ArrayList<List<String>> getDistinctData() {
        ArrayList<List<String>> arrayList = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT DISTINCT Artist FROM iTunesLibrary", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            stringList = new ArrayList<>();
            for (int i = 0;i < cursor.getColumnCount();i++) {
                if(cursor.getString(i) == null)
                {
                    stringList.add("NoData");
                }
                else {
                    stringList.add(cursor.getString(i));
                }
            }
            arrayList.add(stringList);
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
    }
}
