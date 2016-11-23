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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import at.markushi.ui.CircleButton;


public class NewRecipeActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private Spinner difficultySpinner;
    CircleButton recipeImageCircularButton;
    public static int SELECT_IMAGE;
    private ImageView recipeImageView;
    private String imageURL;

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
        /*if( if user added actually a recipe ) {

            returnIntent.putExtra("result", true);
        }

        else{
            returnIntent.putExtra("result", false);
        }*/
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
                        //TODO If form is valid then add the new recipe to the DB
                        //if(imageURL != null) ;
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
        if (myDir.mkdirs()) {
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
        }
        return storedImageName;
    }
}
