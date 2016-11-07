package me.recette;

import android.app.Activity;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;


public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private TextView recipeTextView;
    private TextView recipeDifficultyTextView;
    private TextView recipeTimeTextView;
    private TextView recipeCostTextView;
    private TextView recipeIngredientsTextView;
    private TextView recipePreparationTextView;
    private LinearLayout recipeImageView;
    private LikeButtonView recipeLikeButtonView;
    public static boolean likeValue;
    private boolean originalLikeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if(getIntent().getStringExtra("recipeName")!=null) setTitle(getIntent().getStringExtra("recipeName"));
        else {setTitle(R.string.main_activity_title);}
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        likeValue = getIntent().getBooleanExtra("recipeAimer", false);
        originalLikeValue = getIntent().getBooleanExtra("recipeAimer", false);

        recipeTextView = (TextView) findViewById(R.id.recipeTextView);
        recipeDifficultyTextView = (TextView) findViewById(R.id.recipeDifficultyTextView);
        recipeTimeTextView = (TextView) findViewById(R.id.recipeTimeTextView);
        recipeCostTextView = (TextView) findViewById(R.id.recipeCostTextView);
        recipeIngredientsTextView = (TextView) findViewById(R.id.textIngredients);
        recipePreparationTextView = (TextView) findViewById(R.id.textPreparation);
        recipeImageView = (LinearLayout) findViewById(R.id.recipeImageView);
        recipeLikeButtonView = (LikeButtonView) findViewById(R.id.recipeLikeButtonView);

        if(getIntent().getStringExtra("recipeName")!=null) recipeTextView.setText(getIntent().getStringExtra("recipeName"));
        if(getIntent().getStringExtra("recipeIngredients")!=null) recipeIngredientsTextView.setText(getIntent().getStringExtra("recipeIngredients"));
        if(getIntent().getStringExtra("recipePreparation")!=null) recipePreparationTextView.setText(getIntent().getStringExtra("recipePreparation"));
        if(getIntent().getIntExtra("recipeCost", 0) != 0) recipeCostTextView.setText(String.valueOf(getIntent().getIntExtra("recipeCost", 0))+" Dh");
        if(getIntent().getIntExtra("recipeTime", 0) != 0) recipeTimeTextView.setText(String.valueOf(getIntent().getIntExtra("recipeTime", 0))+" min.");
        if(getIntent().getIntExtra("recipeDifficulty", 0) != 0) recipeDifficultyTextView.setText(String.valueOf(getIntent().getIntExtra("recipeDifficulty", 0))+"/5");
        if(getIntent().getStringExtra("recipeImage") != null) recipeImageView.setBackground(getResources().getDrawable(getResources().getIdentifier(getIntent().getStringExtra("recipeImage"), "drawable", getPackageName())));
        //Log.d("Resource name", String.valueOf(getResources().getIdentifier(getIntent().getStringExtra("recipeImage"), "drawable", getPackageName())));
        if(getIntent().getBooleanExtra("recipeAimer", false)) recipeLikeButtonView.setClicked(true);
        Log.d("Liked boolean",String.valueOf(getIntent().getBooleanExtra("recipeAimer", false)));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                Log.d("Back pressed", "from menu");
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onBackPressed() {
        // This will be called either automatically for you on 2.0
        // or later, or by the code above on earlier versions of the
        // platform.
        Intent returnIntent = new Intent();
        if(originalLikeValue != likeValue) {
            FullRecipe tmpRecipe = retrieveDBInstance().getRecipeById(String.valueOf(getIntent().getIntExtra("recipeId", 0)));
            tmpRecipe.setAimer(likeValue);
            retrieveDBInstance().updateRecipe(tmpRecipe);
            Log.d("Back pressed", "recipe " + tmpRecipe.getName() + " like value changed to " + tmpRecipe.getAimer());
            returnIntent.putExtra("result", true);
        }
        else{
            returnIntent.putExtra("result", false);
        }
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(R.anim.hold, R.anim.slide_out_left);
        return;
    }

    public DataBaseHelper retrieveDBInstance(){

        DataBaseHelper myDbHelper = new DataBaseHelper(this);

        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create test");
        }

        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            try {
                throw sqle;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return myDbHelper;
    }
}
