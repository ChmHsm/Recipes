package me.recette;

import android.util.Log;

/**
 * Created by Me on 21/10/2016.
 */
public class FullRecipe {

    private int id;
    private String name;
    private String ingredients;
    private String preparation;
    private int time;
    private int cost;
    private int difficulty;
    private String image;
    private String author;
    private boolean aimer;

    public FullRecipe( int id, String name, String ingredients, String preparation, int time, int cost, int difficulty, String image, String author, boolean aimer) {
        this.name = name;
        this.ingredients = ingredients;
        this.preparation = preparation;
        this.time = (time!=0 ? time : 1000);
        this.cost = (cost!=0 ? cost : 1000);
        this.difficulty = (difficulty!=0 ? difficulty : 1000);
        this.image = image;
        this.author = author;
        this.id = id;
        this.aimer = aimer;
    }

    public FullRecipe(FullRecipe fullRecipe){
        this.name = fullRecipe.getName();
        this.ingredients = fullRecipe.getIngredients();
        this.preparation = fullRecipe.getPreparation();
        this.time = (fullRecipe.getTime()!=0 ? fullRecipe.getTime() : 1000);
        this.cost = (fullRecipe.getCost()!=0 ? fullRecipe.getCost() : 1000);
        this.difficulty = (fullRecipe.getDifficulty()!=0 ? fullRecipe.getDifficulty() : 1000);
        this.image = fullRecipe.getImage();
        this.author = fullRecipe.getAuthor();
        this.id = fullRecipe.getId();
        this.aimer = fullRecipe.getAimer();
    }

    public boolean getAimer() {
        return aimer;
    }

    public int getId() {
        return id;
    }

    public void setAimer(boolean aimer) {
        this.aimer = aimer;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getPreparation() {
        return preparation;
    }

    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    /*public boolean equals(FullRecipe fullRecipe) {
        Log.d("Full recipe id", fullRecipe.getId());
        if (fullRecipe.getId().equals(this.getId()) &&
                fullRecipe.getName().equals(this.getName()) &&
                fullRecipe.getIngredients().equals(this.getIngredients()) &&
                fullRecipe.getImage().equals(this.getImage()) &&
                fullRecipe.getAuthor().equals(this.getAuthor()) &&
                fullRecipe.getDifficulty()==this.getDifficulty() &&
                fullRecipe.getCost()==this.getCost() &&
                fullRecipe.getTime()==this.getTime() &&
                fullRecipe.getPreparation().equals(this.getPreparation()))
            return true;
        else return false;
    }*/
}
