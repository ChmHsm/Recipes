package me.recette;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.IOException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

import static me.recette.R.color.white;


public class MainListActivity extends ActionBarActivity {

    private GridView gridView;
    private Toolbar toolbar;
    private ArrayList<FullRecipe> recipes;
    private RecepiesAdapter recipesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        setTitle(R.string.main_list_activity_title);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        gridView = (GridView) findViewById(R.id.gridView);



        recipes = retrieveDBInstance().getAllRecipes();


        recipesAdapter = new RecepiesAdapter(this, recipes, this);
        gridView.setAdapter(recipesAdapter);

        /*FullRecipe tmpRecipe = retrieveDBInstance().getRecipeById("1");
        tmpRecipe.setName("New Recipe 4");
        retrieveDBInstance().insertRecipe(tmpRecipe);
        retrieveDBInstance().deleteRecipe(25);
        retrieveDBInstance().deleteRecipe(26);
        retrieveDBInstance().deleteRecipe(27);
        updateList();
        FullRecipe tmpRecipe = retrieveDBInstance().getRecipeById("1");
        tmpRecipe.setAimer(true);
        tmpRecipe.setName("Brochettes de kefta");
        retrieveDBInstance().updateRecipe(tmpRecipe);
        updateList();*/



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main_list, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        /*int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_bar", null, null);
        View searchPlate = findViewById(searchPlateId);
        Log.d("Search plate id", String.valueOf(searchPlateId));
        if (searchPlate!=null) {
            Log.d("Search plate", "not null");
            searchPlate.setBackgroundColor(Color.DKGRAY);
            int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView searchText = (TextView) searchPlate.findViewById(searchTextId);
            if (searchText != null) {
                Log.d("Search text", "not null");
                searchText.setTextColor(Color.WHITE);
                searchText.setHintTextColor(Color.WHITE);
            }
        }*/
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Chercher...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                recipesAdapter.getFilter().filter(newText);
                return false;
            }
        });
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_search) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                if(data.getBooleanExtra("result", false)){
                    updateList();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void triggerNotification(PendingIntent pendingIntent, String title, String textContent){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.app_logo)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.app_logo))
                        .setContentTitle(title)
                        .setContentText(textContent)
                        .setContentIntent(pendingIntent)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());//Required on Gingerbread and below
    }

    public void updateList(){
        recipes = retrieveDBInstance().getAllRecipes();
        recipesAdapter = new RecepiesAdapter(this, recipes, this);
        gridView.setAdapter(recipesAdapter);
        recipesAdapter.notifyDataSetChanged();
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


class RecepiesAdapter extends BaseAdapter implements Filterable
{
    ArrayList<FullRecipe> recipes;
    Context context;
    ImageLoader imageLoader;
    DisplayImageOptions displayImageOptions;
    private ArrayList<FullRecipe> mStringFilterList;
    private ValueFilter valueFilter;
    private Activity activity;

    public RecepiesAdapter(Context context, ArrayList<FullRecipe> recipes, Activity activity){

        this.context = context;
        this.recipes = recipes;
        this.mStringFilterList = recipes;
        this.activity = activity;
        /*String[] recipesNames = context.getResources().getStringArray(R.array.recipes_names);
        String[] recipesImagesNames = context.getResources().getStringArray(R.array.recipes_images_names);
        recipes = new ArrayList<Recipe>();
        for (int i =0 ; i < recipesNames.length ; i++){
            Recipe recipe = new Recipe(context.getResources().getIdentifier(recipesImagesNames[i], "drawable", context.getPackageName()), recipesNames[i]);
            recipes.add(recipe);
            Log.i("Recipe name: ", recipesImagesNames[i]);
        }*/

        ImageLoaderConfiguration imageLoaderConfiguration
                = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(imageLoaderConfiguration);

        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.on_loading)
                .showImageForEmptyUri(R.drawable.on_fail)
                .showImageOnFail(R.drawable.on_fail)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }

    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public Object getItem(int position) {
        return recipes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if(valueFilter==null) {

            valueFilter=new ValueFilter();
        }

        return valueFilter;
    }

    private class ValueFilter extends Filter {

        //Invoked in a worker thread to filter the data according to the constraint.
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results=new FilterResults();
            if(constraint!=null && constraint.length()>0){
                ArrayList<FullRecipe> filteredRecipes = new ArrayList<>();
                for(int i=0;i<mStringFilterList.size();i++){
                    if((mStringFilterList.get(i).getName().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {
                        FullRecipe fullRecipe = new FullRecipe(mStringFilterList.get(i));
                        fullRecipe.setName(mStringFilterList.get(i).getName());
                        fullRecipe.setId(mStringFilterList.get(i).getId());
                        filteredRecipes.add(fullRecipe);
                    }
                }
                results.count=filteredRecipes.size();
                results.values=filteredRecipes;

            }else{
                results.count=mStringFilterList.size();
                results.values=mStringFilterList;
            }
            return results;
        }


        //Invoked in the UI thread to publish the filtering results in the user interface.
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            recipes=(ArrayList<FullRecipe>) results.values;
            notifyDataSetChanged();
        }
    }



    class ViewHolder{

        ImageView recipeImageView;
        TextView recipeTextView;
        TextView recipeCostTextView;
        TextView recipeDifficultyTextView;
        TextView recipeTimeTextView;
        public ViewHolder(View v){
            recipeImageView = (ImageView) v.findViewById(R.id.recipeImageView);
            recipeTextView = (TextView) v.findViewById(R.id.recipeTextView);
            recipeTimeTextView = (TextView) v.findViewById(R.id.recipeTimeTextView);
            recipeCostTextView = (TextView) v.findViewById(R.id.recipeCostTextView);
            recipeDifficultyTextView = (TextView) v.findViewById(R.id.recipeDifficultyTextView);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //View oneRecipe = convertView;
        final ViewHolder holder;

        if (convertView == null){

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.one_recipe, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        else{
            holder = (ViewHolder) convertView.getTag();
        }
        /*
        Drawable tmpImage = context.getResources().getDrawable(recipes.get(position).imageRecipe);
        holder.recipeImageView.setImageDrawable(resize(tmpImage));
        //Log.i("Holder position: ", String.valueOf(recipes.get(position).imageRecipe));
        holder.recipeTextView.setText(recipes.get(position).nameRecipe);
        context.getResources().getIdentifier(recipes.get(position).getName())
        */
        imageLoader.displayImage("drawable://" + context.getResources().getIdentifier(recipes.get(position).getImage(), "drawable", context.getPackageName()), holder.recipeImageView, displayImageOptions);
        holder.recipeTextView.setText(recipes.get(position).getName()/* + " id: " + String.valueOf(recipes.get(position).getId()) + " "+recipes.get(position).getAimer()*/);
        //Log.d("Crash value", String.valueOf(recipes.get(position).getDifficulty()));
        if(recipes.get(position).getDifficulty()!=0) holder.recipeDifficultyTextView.setText(String.valueOf(recipes.get(position).getDifficulty())+"/5");
        if(recipes.get(position).getCost()!=0) holder.recipeCostTextView.setText(String.valueOf(recipes.get(position).getCost())+" Dh");
        if(recipes.get(position).getTime()!=0) holder.recipeTimeTextView.setText(String.valueOf(recipes.get(position).getTime())+" min.");


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Item clicked", recipes.get(position).getName());
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("recipeId", recipes.get(position).getId());
                intent.putExtra("recipeAimer", recipes.get(position).getAimer());
                intent.putExtra("recipeName", recipes.get(position).getName());
                intent.putExtra("recipeIngredients", recipes.get(position).getIngredients());
                intent.putExtra("recipePreparation", recipes.get(position).getPreparation());
                intent.putExtra("recipeTime", recipes.get(position).getTime());
                intent.putExtra("recipeDifficulty", recipes.get(position).getDifficulty());
                intent.putExtra("recipeCost", recipes.get(position).getCost());
                intent.putExtra("recipeImage", recipes.get(position).getImage());
                activity.startActivityForResult(intent, 1);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }
        });


        return convertView;
    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 200, 180, false);
        return new BitmapDrawable(context.getResources(), bitmapResized);
    }
}




