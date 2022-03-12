package vn.edu.huflit.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    Context context;
    private static final String dbName = "Notes";

    private static final String tblNote = "notes";
    private static final String colID = "id";
    private static final String colTitle = "title";
    private static final String colDes = "description";
    private static final String colDead = "deadline";


    public Database(@Nullable Context context) {
        super(context, dbName, null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE "+ tblNote+
                " ("+ colID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                colTitle + " TEXT, "+
                colDes + " TEXT, " +
                colDead + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+tblNote);
        onCreate(db);
    }

    void addNote(String title, String description, String deadline){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(colTitle, title);
        cv.put(colDes, description);
        cv.put(colDead, deadline);
        long result = db.insert(tblNote, null, cv);
        if(result == -1){
            Toast.makeText(context, "Thêm không thành công", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllData(){
        String query = "SELECT * FROM "+ tblNote;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    void updateNote(String title, String description, String id, String deadline){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(colTitle, title);
        cv.put(colDes, description);
        cv.put(colDead, deadline);
        long result = db.update(tblNote, cv, "id=?", new String[]{id});
        if(result == -1){
            Toast.makeText(context, "Cập nhật không thành công", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteNote(String id){
        SQLiteDatabase database = this.getWritableDatabase();
        String where="ID=?";
        database.delete(dbName, where, new String[]{id}) ;
        Toast.makeText(this.context.getApplicationContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
    }
}
