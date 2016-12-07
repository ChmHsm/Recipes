package me.recette;


import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;


import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.io.IOException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import me.recette.ButtonAnimation.LikeButtonView;


//This is the first Activity containing the GridView
// TODO ActionBarActivity is deprecated, to be fixed
public class MainListActivity extends ActionBarActivity {

    private GridView gridView;
    private Toolbar toolbar;
    private ArrayList<FullRecipe> recipes;
    private static RecipesAdapter recipesAdapter;
    private LikeButtonView recipeLikeButtonView;
    private LikeButtonView recipeDifficultyButtonView;
    private LikeButtonView recipeCostButtonView;
    private LikeButtonView recipeTimeButtonView;

    public static char likeFilter;
    public static char difficultyFilter;
    public static char timeFilter;
    public static char costFilter;
    private static String textForFiltering;
    private static String toastHintContent;
    private static Context activityContext;
    private static int ADD_RECIPE_ACTIVITY = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        setTitle(R.string.main_list_activity_title);

        /*if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.LOLLIPOP){
            final LayoutInflater factory = getLayoutInflater();
            final View textEntryView = factory.inflate(R.layout.appbar_shadow_view, null);
            View shadow = textEntryView.findViewById(R.id.shadow);
            shadow.setVisibility(View.VISIBLE);
        }*/

        gridView = (GridView) findViewById(R.id.gridView);

        cleanImageCache();

        recipes = retrieveDBInstance().getAllRecipes();

        recipesAdapter = new RecipesAdapter(this, recipes, this);
        gridView.setAdapter(recipesAdapter);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(gridView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainListActivity.this, NewRecipeActivity.class);
                startActivityForResult(intent, ADD_RECIPE_ACTIVITY);
                overridePendingTransition(R.anim.bottom_up, R.anim.hold);
            }
        });


        recipeLikeButtonView = (LikeButtonView) findViewById(R.id.recipeLikeButtonView);
        recipeCostButtonView = (LikeButtonView) findViewById(R.id.recipeCostButtonView);
        recipeDifficultyButtonView = (LikeButtonView) findViewById(R.id.recipeDifficultyButtonView);
        recipeTimeButtonView = (LikeButtonView) findViewById(R.id.recipeTimeButtonView);

        recipeLikeButtonView.setLayoutName("view_like_button_main_list_activity");
        recipeCostButtonView.setLayoutName("view_cost_button");
        recipeTimeButtonView.setLayoutName("view_time_button");
        recipeDifficultyButtonView.setLayoutName("view_difficulty_button");
        recipeLikeButtonView.init();
        recipeCostButtonView.init();
        recipeDifficultyButtonView.init();
        recipeTimeButtonView.init();

        likeFilter = '0';
        costFilter = '0';
        timeFilter = '0';
        difficultyFilter = '0';
        textForFiltering = "";

        activityContext = MainListActivity.this;

        //TODO make elevation effect for items for lollipop end prelollipop version

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main_list, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        // TODO Animate the SearchView to make it like Google's Play Music's search bar
        //Tried to animate the SearchView to make it like the Play Music's search bar, but the search plate was always null
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
        searchView.setQueryHint(getResources().getString(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                textForFiltering = newText;
                performFiltering();
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

        //Menu still empty
        //int id = item.getItemId();

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
                    performFiltering();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //So far, there's always an OK Result from Activity
            }
        }

        else if(requestCode == ADD_RECIPE_ACTIVITY){
            if(resultCode == Activity.RESULT_OK){
                if(data.getBooleanExtra("result", false)){
                    updateList();
                    performFiltering();
                }
            }
        }
    }


    //Made for notification handling, should probably be a separate class for calls outside this Activity
    // TODO Make class for Notifications management instead of this method
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

    //TODO animate gridView when list updates, this method is to be called in updatelist()
    private void animateGridView(){
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        gridView.setAnimation(anim);
        anim.start();
    }

    public static void showToastAfterFilter(){

        if(likeFilter == '1' || difficultyFilter == '1' || timeFilter == '1' || costFilter == '1') {
            toastHintContent = activityContext.getResources().getString(R.string.toast_filter_hint);
            if (likeFilter == '1') {
                toastHintContent += " "+activityContext.getResources().getString(R.string.favorite_name)+",";
            }
            if (difficultyFilter == '1') {
                toastHintContent += " "+activityContext.getResources().getString(R.string.difficulty_name)+",";
            }
            if (timeFilter == '1') {
                toastHintContent += " "+activityContext.getResources().getString(R.string.time_name)+",";
            }
            if (costFilter == '1') {
                toastHintContent += " "+activityContext.getResources().getString(R.string.cost_name)+",";
            }
            toastHintContent = toastHintContent.substring(0, toastHintContent.length()-1)+".";
            Toast.makeText(activityContext, toastHintContent, Toast.LENGTH_SHORT).show();
        }
    }

    // Retrieves a new DB instance and updates the recipes object containing ALL the recipes and updates the Views
    public void updateList(){
        recipes = retrieveDBInstance().getAllRecipes();
        recipesAdapter = new RecipesAdapter(this, recipes, this);
        gridView.setAdapter(recipesAdapter);
        recipesAdapter.notifyDataSetChanged();
        //animateGridView();
    }

    public static void performFiltering(){
        recipesAdapter.getFilter().filter(textForFiltering + likeFilter + costFilter + timeFilter + difficultyFilter);
        recipesAdapter.notifyDataSetChanged();
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

    private void cleanImageCache() {
        ArrayList<FullRecipe> recipes = retrieveDBInstance().getAllRecipes();
        ArrayList<String> usedImagesPaths = new ArrayList<>();

        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getImage().contains("local")) {
                int index = recipes.get(i).getImage().lastIndexOf(':');
                usedImagesPaths.add(recipes.get(i).getImage().substring(index + 1, recipes.get(i).getImage().length() - 1));
            }
        }

        String path = Environment.getExternalStorageDirectory().toString() + "/DCIM/RecipesApp/";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        if (directory.listFiles() != null) {
            File[] files = directory.listFiles();
            Log.d("Files", "Size: " + files.length);

            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getPath());
            }
            for (int i = 0; i < files.length; i++) {
                if (!usedImagesPaths.contains(files[i].getAbsolutePath())) {
                    files[i].delete();
                }
            }
        }
    }

}

//Adapter for the GridView.
//For images caching and management, Universal Image Loader API was used.
class RecipesAdapter extends BaseAdapter implements Filterable
{
    ArrayList<FullRecipe> recipes;
    Context context;
    ImageLoader imageLoader;
    DisplayImageOptions displayImageOptions;
    private ArrayList<FullRecipe> mStringFilterList;
    private ValueFilter valueFilter;
    private Activity activity;

    private static int RECIPE_DETAIL_ACTIVITY = 1;

    public RecipesAdapter(Context context, ArrayList<FullRecipe> recipes, Activity activity){

        this.context = context;
        this.recipes = recipes;
        this.mStringFilterList = recipes;
        this.activity = activity;


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

    //Adapter Filtering by recipe's name
    private class ValueFilter extends Filter {

        //Invoked in a worker thread to filter the data according to the constraint.
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results=new FilterResults();
            int length = constraint.length();

            final boolean filterLike = (String.valueOf(constraint.subSequence(length - 4, length - 3)).equals("1"));
            Log.d("Filter sequence", String.valueOf(constraint));
            final boolean filterCost = (String.valueOf(constraint.subSequence(length - 3, length - 2)).equals("1"));
            final boolean filterTime = (String.valueOf(constraint.subSequence(length - 2, length - 1)).equals("1"));
            final boolean filterDifficulty = (String.valueOf(constraint.subSequence(length - 1, length)).equals("1"));

            String sequenceToFilterOn = "";

            if(constraint.length()>=4){
                Log.d("Filter sequence", String.valueOf(constraint)+" not null && length >= 4");
                if(constraint.length() > 4) {
                    Log.d("Filter sequence", String.valueOf(constraint)+" not null && length > 4");
                    sequenceToFilterOn = constraint.subSequence(0, length - 4).toString();
                }
                ArrayList<FullRecipe> filteredRecipes = new ArrayList<>();
                for(int i=0;i<mStringFilterList.size();i++){
                    if(filterLike) {

                        if ((mStringFilterList.get(i).getName().toUpperCase()).contains(sequenceToFilterOn.toUpperCase())
                                && mStringFilterList.get(i).getAimer()) {
                            Log.d("Filtering", "one item matching");
                            FullRecipe fullRecipe = new FullRecipe(mStringFilterList.get(i));
                            //fullRecipe.setName(mStringFilterList.get(i).getName());
                            //fullRecipe.setId(mStringFilterList.get(i).getId());
                            filteredRecipes.add(fullRecipe);
                        }

                        if(filterCost || filterTime || filterDifficulty){
                            Collections.sort(filteredRecipes, new Comparator<FullRecipe>() {
                                @Override
                                public int compare(FullRecipe lhs, FullRecipe rhs) {
                                    if(filterDifficulty){
                                        if(lhs.getDifficulty() < rhs.getDifficulty()) return -1;
                                        else if(lhs.getDifficulty() > rhs.getDifficulty()) return 1;
                                        return 0;
                                    }
                                    else if(filterCost){
                                        if(lhs.getCost() < rhs.getCost()) return -1;
                                        else if(lhs.getCost() > rhs.getCost()) return 1;
                                        return 0;
                                    }
                                    else if(filterTime){
                                        if(lhs.getTime() < rhs.getTime()) return -1;
                                        else if(lhs.getTime() > rhs.getTime()) return 1;
                                        return 0;
                                    }
                                    else if(filterDifficulty && filterTime){
                                        if(lhs.getDifficulty() < rhs.getDifficulty() && lhs.getTime() < rhs.getTime()) return -1;
                                        else if(lhs.getDifficulty() > rhs.getDifficulty() && lhs.getTime() > rhs.getTime()) return 1;
                                        else if(lhs.getDifficulty() < rhs.getDifficulty() && lhs.getTime() > rhs.getTime()) return -1;
                                        else if(lhs.getDifficulty() > rhs.getDifficulty() && lhs.getTime() < rhs.getTime()) return 1;
                                        return 0;
                                    }
                                    else if(filterDifficulty && filterCost){
                                        if(lhs.getDifficulty() < rhs.getDifficulty() && lhs.getCost() < rhs.getCost()) return -1;
                                        else if(lhs.getDifficulty() > rhs.getDifficulty() && lhs.getCost() > rhs.getCost()) return 1;
                                        else if(lhs.getDifficulty() < rhs.getDifficulty() && lhs.getCost() > rhs.getCost()) return -1;
                                        else if(lhs.getDifficulty() > rhs.getDifficulty() && lhs.getCost() < rhs.getCost()) return 1;
                                        return 0;
                                    }
                                    else if(filterTime && filterCost){
                                        if(lhs.getTime() < rhs.getTime() && lhs.getCost() < rhs.getCost()) return -1;
                                        else if(lhs.getTime() > rhs.getTime() && lhs.getCost() > rhs.getCost()) return 1;
                                        else if(lhs.getTime() < rhs.getTime() && lhs.getCost() > rhs.getCost()) return -1;
                                        else if(lhs.getTime() > rhs.getTime() && lhs.getCost() < rhs.getCost()) return 1;
                                        return 0;
                                    }
                                    else if(filterDifficulty && filterTime && filterCost){
                                        if(lhs.getDifficulty() < rhs.getDifficulty() && lhs.getTime() < rhs.getTime() && lhs.getCost() < rhs.getCost()) return -1;
                                        else if(lhs.getDifficulty() < rhs.getDifficulty() && lhs.getTime() < rhs.getTime() && lhs.getCost() > rhs.getCost()) return -1;
                                        else if(lhs.getDifficulty() > rhs.getDifficulty() && lhs.getTime() > rhs.getTime() && lhs.getCost() > rhs.getCost()) return 1;
                                        else if(lhs.getDifficulty() > rhs.getDifficulty() && lhs.getTime() > rhs.getTime() && lhs.getCost() > rhs.getCost()) return -1;
                                        return 0;
                                    }
                                    return 0;
                                }
                            });
                        }
                    }

                    else{
                        if ((mStringFilterList.get(i).getName().toUpperCase()).contains(sequenceToFilterOn.toUpperCase())) {
                            Log.d("Filtering", "one item matching");
                            FullRecipe fullRecipe = new FullRecipe(mStringFilterList.get(i));
                            //fullRecipe.setName(mStringFilterList.get(i).getName());
                            //fullRecipe.setId(mStringFilterList.get(i).getId());
                            filteredRecipes.add(fullRecipe);
                        }

                        if(filterCost || filterTime || filterDifficulty){
                            Collections.sort(filteredRecipes, new Comparator<FullRecipe>() {
                                @Override
                                public int compare(FullRecipe lhs, FullRecipe rhs) {
                                    if (filterDifficulty) {
                                        if (lhs.getDifficulty() < rhs.getDifficulty()) return -1;
                                        else if (lhs.getDifficulty() > rhs.getDifficulty())
                                            return 1;
                                        return 0;
                                    } else if (filterCost) {
                                        if (lhs.getCost() < rhs.getCost()) return -1;
                                        else if (lhs.getCost() > rhs.getCost()) return 1;
                                        return 0;
                                    } else if (filterTime) {
                                        if (lhs.getTime() < rhs.getTime()) return -1;
                                        else if (lhs.getTime() > rhs.getTime()) return 1;
                                        return 0;
                                    } else if (filterDifficulty && filterTime) {
                                        if (lhs.getDifficulty() < rhs.getDifficulty() && lhs.getTime() < rhs.getTime())
                                            return -1;
                                        else if (lhs.getDifficulty() > rhs.getDifficulty() && lhs.getTime() > rhs.getTime())
                                            return 1;
                                        else if (lhs.getDifficulty() < rhs.getDifficulty() && lhs.getTime() > rhs.getTime())
                                            return -1;
                                        else if (lhs.getDifficulty() > rhs.getDifficulty() && lhs.getTime() < rhs.getTime())
                                            return 1;
                                        return 0;
                                    } else if (filterDifficulty && filterCost) {
                                        if (lhs.getDifficulty() < rhs.getDifficulty() && lhs.getCost() < rhs.getCost())
                                            return -1;
                                        else if (lhs.getDifficulty() > rhs.getDifficulty() && lhs.getCost() > rhs.getCost())
                                            return 1;
                                        else if (lhs.getDifficulty() < rhs.getDifficulty() && lhs.getCost() > rhs.getCost())
                                            return -1;
                                        else if (lhs.getDifficulty() > rhs.getDifficulty() && lhs.getCost() < rhs.getCost())
                                            return 1;
                                        return 0;
                                    } else if (filterTime && filterCost) {
                                        if (lhs.getTime() < rhs.getTime() && lhs.getCost() < rhs.getCost())
                                            return -1;
                                        else if (lhs.getTime() > rhs.getTime() && lhs.getCost() > rhs.getCost())
                                            return 1;
                                        else if (lhs.getTime() < rhs.getTime() && lhs.getCost() > rhs.getCost())
                                            return -1;
                                        else if (lhs.getTime() > rhs.getTime() && lhs.getCost() < rhs.getCost())
                                            return 1;
                                        return 0;
                                    } else if (filterDifficulty && filterTime && filterCost) {
                                        if (lhs.getDifficulty() < rhs.getDifficulty() && lhs.getTime() < rhs.getTime() && lhs.getCost() < rhs.getCost())
                                            return -1;
                                        else if (lhs.getDifficulty() < rhs.getDifficulty() && lhs.getTime() < rhs.getTime() && lhs.getCost() > rhs.getCost())
                                            return -1;
                                        else if (lhs.getDifficulty() > rhs.getDifficulty() && lhs.getTime() > rhs.getTime() && lhs.getCost() > rhs.getCost())
                                            return 1;
                                        else if (lhs.getDifficulty() > rhs.getDifficulty() && lhs.getTime() > rhs.getTime() && lhs.getCost() > rhs.getCost())
                                            return -1;
                                        return 0;
                                    }
                                    return 0;
                                }
                            });
                        }
                    }
                }
                results.count=filteredRecipes.size();
                results.values=filteredRecipes;
                Log.d("Filtered Recipes","Filtered Recipes are as follows: ");
                for(int i=0 ; i < filteredRecipes.size() ; i++){
                    Log.d(filteredRecipes.get(i).getName(), "Difficulty: "+filteredRecipes.get(i).getDifficulty()+" Time: "+filteredRecipes.get(i).getTime()+" Cost: "+filteredRecipes.get(i).getCost());
                }

            }else{
                results.count=mStringFilterList.size();
                results.values=mStringFilterList;
            }

            return results;
        }


        //Invoked in the UI thread to publish the filtering results in the user interface.
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            recipes=(ArrayList<FullRecipe>) results.values;
            notifyDataSetChanged();
        }
    }


    //One item view holder
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
        if(!recipes.get(position).getImage().contains("local")) {
            imageLoader.displayImage("drawable://" + context.getResources().getIdentifier(recipes.get(position).getImage(), "drawable", context.getPackageName()), holder.recipeImageView, displayImageOptions);
        }
        else if(recipes.get(position).getImage().contains("local")){
            int index = recipes.get(position).getImage().lastIndexOf(':');
            imageLoader.displayImage("file:///"+recipes.get(position).getImage().substring(index+1,recipes.get(position).getImage().length()-1), holder.recipeImageView, displayImageOptions);
        }
        holder.recipeTextView.setText(recipes.get(position).getName()/* + " id: " + String.valueOf(recipes.get(position).getId()) + " "+recipes.get(position).getAimer()*/);
        //Log.d("Crash value", String.valueOf(recipes.get(position).getDifficulty()));
        /*if(recipes.get(position).getDifficulty()!=0) */holder.recipeDifficultyTextView.setText(recipes.get(position).getDifficulty()!=1000 ? String.valueOf(recipes.get(position).getDifficulty())+"/5" : "N/A");
        /*if(recipes.get(position).getCost()!=0) */holder.recipeCostTextView.setText(recipes.get(position).getCost()!=1000 ? String.valueOf(recipes.get(position).getCost())+" Dh" : "N/A");
        /*if(recipes.get(position).getTime()!=0) */holder.recipeTimeTextView.setText(recipes.get(position).getTime()!=1000 ? String.valueOf(recipes.get(position).getTime())+" min." : "N/A");

        //The data is passed to the next Activity through extras, thought it was better than passing the id
        //alone then retrieving the recipe from DB since we already have all the data in this activity.
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Item clicked", recipes.get(position).getName());
                Intent intent = new Intent(context, OneRecipeActivity.class);
                intent.putExtra("recipeId", recipes.get(position).getId());
                intent.putExtra("recipeAimer", recipes.get(position).getAimer());
                intent.putExtra("recipeName", recipes.get(position).getName());
                intent.putExtra("recipeIngredients", recipes.get(position).getIngredients());
                intent.putExtra("recipePreparation", recipes.get(position).getPreparation());
                intent.putExtra("recipeTime", recipes.get(position).getTime());
                intent.putExtra("recipeDifficulty", recipes.get(position).getDifficulty());
                intent.putExtra("recipeCost", recipes.get(position).getCost());
                intent.putExtra("recipeImage", recipes.get(position).getImage());
                activity.startActivityForResult(intent, RECIPE_DETAIL_ACTIVITY);
                //Slide animation
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }
        });


        return convertView;
    }
}




