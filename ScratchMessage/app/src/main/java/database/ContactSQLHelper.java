package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import model.Contact;

public class ContactSQLHelper extends SQLiteOpenHelper {

    private final static String TABLENAME_CONTACT = "contacts";

    private SQLiteDatabase rdb;
    private SQLiteDatabase wdb;

    public ContactSQLHelper(Context context) {
        super(context, TABLENAME_CONTACT, null, 1);

        rdb = getReadableDatabase();
        wdb = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLENAME_CONTACT + "(id text primary key, avaUri text, userName text, nickName text, online integer)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            String query = "DROP TABLE IF EXISTS " + TABLENAME_CONTACT;
            db.execSQL(query);
            onCreate(db);
        }
    }


    public void insert(Contact contact) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", contact.getId());
        contentValues.put("avaUri", contact.getAvaUri());
        contentValues.put("userName", contact.getUserName());
        contentValues.put("nickName", contact.getNickName());
        contentValues.put("online", contact.isOnline());

        wdb.replace(TABLENAME_CONTACT, null, contentValues);
    }

    public ArrayList<Contact> getAll() {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor cursor = rdb.query(false, TABLENAME_CONTACT, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String avaUri = cursor.getString(cursor.getColumnIndex("avaUri"));
            String userName = cursor.getString(cursor.getColumnIndex("userName"));
            String nickName = cursor.getString(cursor.getColumnIndex("nickName"));
            int onlineAsInt = cursor.getInt(cursor.getColumnIndex("online"));
            boolean online = onlineAsInt == 1;

            contacts.add(new Contact(id, avaUri, userName, nickName, online));
        }
        cursor.close();
        return contacts;
    }

    public void deleteTable() {
        String query = "DELETE FROM " + TABLENAME_CONTACT;
        wdb.execSQL(query);
    }

    public boolean isExists(String checkUserName) {
        Cursor cursor = rdb.query(false, TABLENAME_CONTACT, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String userName = cursor.getString(cursor.getColumnIndex("userName"));
            if (checkUserName.compareTo(userName) == 0)
                return true;
        }
        cursor.close();
        return false;
    }
}
