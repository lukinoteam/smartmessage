package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import model.Message;

public class ChatLogSQLHelper extends SQLiteOpenHelper {

    private static final String TABLENAME_CHATLOG = "chatlog";

    private SQLiteDatabase rdb;
    private SQLiteDatabase wdb;

    public ChatLogSQLHelper(Context context) {
        super(context, TABLENAME_CHATLOG, null, 1);

        rdb = getReadableDatabase();
        wdb = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLENAME_CHATLOG + "(id text primary key, time integer, content text, status int, friend text, type int)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion){
            String query = "DROP TABLE IF EXISTS " + TABLENAME_CHATLOG;
            db.execSQL(query);
            onCreate(db);
        }
    }

    public void insert(Message message){
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", message.getId());
        contentValues.put("time", message.getTime());
        contentValues.put("content", message.getContent());
        contentValues.put("status", message.getStatus());
        contentValues.put("friend", message.getFriend());
        contentValues.put("type", message.getType());

        wdb.insert(TABLENAME_CHATLOG, null, contentValues);
    }

    public void deleteTable(){
        String query = "DELETE FROM " + TABLENAME_CHATLOG;
        wdb.execSQL(query);
    }

    public ArrayList<Message> getAll(){
        ArrayList<Message> messages = new ArrayList<>();
        Cursor cursor = rdb.query(false, TABLENAME_CHATLOG, null,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex("id"));
            long time = cursor.getInt(cursor.getColumnIndex("time"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            String friend = cursor.getString(cursor.getColumnIndex("friend"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));

            messages.add(new Message(id, content, time, status, friend, type));

        }
        cursor.close();
        return  messages;
    }

    public ArrayList<Message> getAllByFriend(String friendUsername){
        ArrayList<Message> messages = new ArrayList<>();
        Cursor cursor = rdb.query(false, TABLENAME_CHATLOG, null,null,null,null,null,null,null);
        while (cursor.moveToNext()){

            String friend = cursor.getString(cursor.getColumnIndex("friend"));
            if (friend.compareTo(friendUsername) == 0){
                String id = cursor.getString(cursor.getColumnIndex("id"));
                long time = cursor.getInt(cursor.getColumnIndex("time"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                int status = cursor.getInt(cursor.getColumnIndex("status"));
                int type = cursor.getInt(cursor.getColumnIndex("type"));

                messages.add(new Message(id, content, time, status, friend, type));
            }
        }
        cursor.close();
        return  messages;
    }

    public boolean isExists(String checkId){
        Cursor cursor = rdb.query(false, TABLENAME_CHATLOG, null,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex("id"));
            if (checkId.compareTo(id) == 0)
                return true;
        }
        cursor.close();
        return  false;
    }
}
