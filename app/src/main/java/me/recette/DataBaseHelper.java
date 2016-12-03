package me.recette;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Me on 21/10/2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    //The Android's default system path the App.
    private static String DB_PATH = "/data/data/me.recette/databases/";

    private static String DB_NAME = "test";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    /**
     * Creates a empty test on the system and rewrites it with your own test.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - Database already exists could be needed in future

        }else{

            //By calling this method and empty test will be created into the default system path
            //of your application so we are gonna be able to overwrite that test with our test.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                //throw new Error("Error copying test");

            }

        }
        this.close();
    }

    /**
     * Check if the test already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    public boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //test does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null;
    }

    /**
     * Copies your test from your local assets-folder to the just created empty test in the
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

    public void openDataBase() throws SQLException {

        //Open the test
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    public ArrayList<FullRecipe> getAllRecipes(){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "select _id, name, ingredients, preparation, time, cost, difficulty, image, author, aimer from recipes";
        ArrayList recipes = new ArrayList();
        final Cursor cursor;
        try{
            cursor = db.rawQuery(selectQuery, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        while(!cursor.isAfterLast()) {
                            FullRecipe recipe = new FullRecipe(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                                    cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getString(7), cursor.getString(8), cursor.getInt(9) != 0);
                            recipes.add(recipe);
                            cursor.moveToNext();
                        }
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        catch (SQLiteException e){
            return recipes;
        }
        db.close();
        return recipes;
    }

    public boolean updateRecipe(FullRecipe fullRecipe){
        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteStatement stmt = db.compileStatement("UPDATE recipes set name = ?, ingredients = ?, preparation = ?, time = ?, cost = ?, difficulty = ?," +
                " image = ?, author = ?, AIMER = ? where _id = ?");
        if(fullRecipe.getName()!=null) stmt.bindString(1, fullRecipe.getName());
        if(fullRecipe.getIngredients()!=null) stmt.bindString(2, fullRecipe.getIngredients());
        if(fullRecipe.getPreparation()!=null) stmt.bindString(3, fullRecipe.getPreparation());
        if(fullRecipe.getTime()!=0) stmt.bindString(4, String.valueOf(fullRecipe.getTime()));
        if(fullRecipe.getCost()!=0) stmt.bindString(5, String.valueOf(fullRecipe.getCost()));
        if(fullRecipe.getDifficulty()!=0) stmt.bindString(6, String.valueOf(fullRecipe.getDifficulty()));
        if(fullRecipe.getImage()!=null) stmt.bindString(7, fullRecipe.getImage());
        if(fullRecipe.getAuthor()!=null) stmt.bindString(8, fullRecipe.getAuthor());
        stmt.bindString(9, String.valueOf(fullRecipe.getAimer()? 1 : 0));
        stmt.bindString(10, String.valueOf(fullRecipe.getId()));
        stmt.execute();

        db.close();
        return true;
    }

    //Insert new recipe in DB
    public boolean insertRecipe(FullRecipe fullRecipe){
        SQLiteDatabase db = this.getWritableDatabase();


        SQLiteStatement stmt = db.compileStatement("INSERT INTO recipes (name, ingredients, preparation, time, cost, difficulty, image, author) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?)");

        if(fullRecipe.getName()!=null) stmt.bindString(1, fullRecipe.getName());
        if(fullRecipe.getIngredients()!=null) stmt.bindString(2, fullRecipe.getIngredients());
        if(fullRecipe.getPreparation()!=null) stmt.bindString(3, fullRecipe.getPreparation());
        if(fullRecipe.getTime()!=0) stmt.bindString(4, String.valueOf(fullRecipe.getTime()));
        if(fullRecipe.getCost()!=0) stmt.bindString(5, String.valueOf(fullRecipe.getCost()));
        if(fullRecipe.getDifficulty()!=0) stmt.bindString(6, String.valueOf(fullRecipe.getDifficulty()));
        if(fullRecipe.getImage()!=null) stmt.bindString(7, fullRecipe.getImage());
        if(fullRecipe.getAuthor()!=null) stmt.bindString(8, fullRecipe.getAuthor());

        stmt.execute();

        db.close();
        return true;
    }

    //Delete recipe from DB by ID
    public boolean deleteRecipe(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        /*String query = "INSERT INTO recipes (name, ingredients, preparation, time, cost, difficulty, image, author)" +
                " VALUES ("+fullRecipe.getName()+","+fullRecipe.getIngredients()+","+fullRecipe.getPreparation()+","
                +fullRecipe.getTime()+","+fullRecipe.getCost()+","+fullRecipe.getDifficulty()+","
                +fullRecipe.getImage()+","+fullRecipe.getAuthor()+","+") where _id = '"+fullRecipe.getId()+"'";
        db.execSQL(query);*/

        SQLiteStatement stmt = db.compileStatement("delete from recipes where _id=?");

        if (id !=0) stmt.bindString(1, String.valueOf(id));

        stmt.execute();

        db.close();
        return true;
    }

    //Retrieve recipe by ID
    public FullRecipe getRecipeById(String id){

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "select _id, name, ingredients, preparation, time, cost, difficulty, image, author, aimer from recipes where _id = '"+id+"'";
        FullRecipe fullRecipe = null;
        final Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    fullRecipe = new FullRecipe(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                            cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getString(7), cursor.getString(8), cursor.getInt(9) != 0);
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        }
        db.close();
        return fullRecipe;
    }

    public boolean setRecipeLiked(String id, boolean liked){


        if(id == null || Integer.parseInt(id) <= 0) return false;
        else{
            if(getRecipeById(id) == null) return false;
            else{
                SQLiteDatabase db = this.getWritableDatabase();
                SQLiteStatement stmt = db.compileStatement("UPDATE recipes set AIMER = ? where _id = ?");
                stmt.bindString(1, String.valueOf(liked ? 1 : 0));
                stmt.bindString(2, id);
                stmt.execute();
                db.close();
                return true;
            }
        }
    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}