package me.recette;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

import at.markushi.ui.CircleButton;


public class NewRecipeActivity extends ActionBarActivity {

    private Toolbar toolbar;
    CircleButton recipeImageCircularButton;
    public static int SELECT_IMAGE;
    private ImageView recipeImageView;


    private EditText recipeNameEditText;
    private Spinner difficultySpinner;
    private EditText costEditText;
    private EditText preparationEditText;
    private EditText ingredientsEditText;
    private EditText instructionsEditText;
    private String imageURL = null;
    private CircleButton recipeConfirmationCircularButton;
    private boolean recipeAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.new_recipe_activity_layout);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        setTitle(R.string.new_recipe_name);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        difficultySpinner = (Spinner) findViewById(R.id.difficultySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.difficulty_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);

        recipeImageCircularButton = (CircleButton) findViewById(R.id.recipeImageCircularButton);
        recipeImageCircularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
            }
        });

        recipeImageView = (ImageView) findViewById(R.id.recipeImageView);

        recipeNameEditText = (EditText) findViewById(R.id.recipeNameEditText);
        difficultySpinner = (Spinner) findViewById(R.id.difficultySpinner);
        costEditText = (EditText) findViewById(R.id.costEditText);
        preparationEditText = (EditText) findViewById(R.id.preparationEditText);
        ingredientsEditText = (EditText) findViewById(R.id.ingredientsEditText);
        instructionsEditText = (EditText) findViewById(R.id.instructionsEditText);
        recipeConfirmationCircularButton = (CircleButton) findViewById(R.id.recipeConfirmationCircularButton);

        recipeConfirmationCircularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("New recipe attempt", recipeNameEditText.getText().toString()+" "+ difficultySpinner.getSelectedItem().toString()+" "+
                        costEditText.getText().toString()+" "+
                        preparationEditText.getText().toString()+" "+
                        ingredientsEditText.getText().toString()+" "+instructionsEditText.getText().toString()+" ImageURL"+imageURL);
                if(!recipeNameEditText.getText().toString().equals("") && !String.valueOf(difficultySpinner.getSelectedItem()).equals(getResources().getStringArray(R.array.difficulty_array)[0]) &&
                        !costEditText.getText().toString().equals("") && !preparationEditText.getText().toString().equals("") &&
                        !ingredientsEditText.getText().toString().equals("") && !instructionsEditText.getText().toString().equals("") &&
                        imageURL != null){


                    recipeAdded = retrieveDBInstance().insertRecipe(new FullRecipe(0, recipeNameEditText.getText().toString(), ingredientsEditText.getText().toString(),
                            instructionsEditText.getText().toString(), Integer.parseInt(preparationEditText.getText().toString()),
                            Integer.parseInt(costEditText.getText().toString()), Integer.parseInt(difficultySpinner.getSelectedItem().toString()),
                            "{local:"+Environment.getExternalStorageDirectory().toString() + "/DCIM/RecipesApp/"+imageURL+"}", null, false));
                    if(recipeAdded) Toast.makeText(NewRecipeActivity.this, R.string.recipe_added_success, Toast.LENGTH_LONG).show();
                    else Toast.makeText(NewRecipeActivity.this, R.string.recipe_added_failure, Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(NewRecipeActivity.this, R.string.fields_mandatory_hint, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:

                //Log.d("Back pressed", "from menu");
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        // For good practice, this will be called either automatically on 2.0 or later, or from onOptionsItemSelected the code above on earlier versions.
        Intent returnIntent = new Intent();
        if(recipeAdded) {

            returnIntent.putExtra("result", true);
        }

        else{
            returnIntent.putExtra("result", false);
        }
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(R.anim.hold, R.anim.bottom_down);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        recipeImageView.setVisibility(View.VISIBLE);
                        float ratio = (float) bitmap.getWidth() / bitmap.getHeight();
                        Log.d("Height", String.valueOf(ratio));
                        recipeImageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, (int) (500 * ratio), 500, false));
                        imageURL = saveImage(Bitmap.createScaledBitmap(bitmap, (int) (500 * ratio), 500, false));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(NewRecipeActivity.this, "Image selected", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(NewRecipeActivity.this, R.string.couldnt_retrieve_image, Toast.LENGTH_SHORT).show();
                //recipeImageView.setVisibility(View.GONE);
            }
        }
    }

    public String saveImage(Bitmap image) {
        String storedImageName = null;
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/DCIM/RecipesApp");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        storedImageName = n + ".jpg";
        File file = new File(myDir, storedImageName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
            //Toast.makeText(NewRecipeActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
            storedImageName = null;
        }

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        getApplicationContext().sendBroadcast(mediaScanIntent);
        return storedImageName;
    }

    //Retrieves DB instance
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
