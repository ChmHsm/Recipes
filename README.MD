Typical Android app about recipes (Content is in French for the moment) containing two main activities:
- A GridView Activity showing all the items (recipes) available.
- A per recipe Activity showing details for every recipe.

Data is stored locally in an SQLite database which you can download from app/assets (db is called "test"). The db is stored there because I needed the app to be shipped with the App (this is very arguable because then the app would have redundant data).
The databse contains one table called "recipes", composed of 10 attributes:
name, ingredients, preparation, time, cost, difficulty, image, author, aimer and _id. (aimer is french for like)

The targeted versions are 16 (JellyBean) to 22 (Kitkat).

What needs to be done in the app is simple:
- An add Recipe functionality (to the local DB of course), a good way to do that is with a FloatingActionButton in the list activity.
- A filtering functionality for the list activity, not by recipe name, rather by "liked", difficulty level (difficulty is on a 1 to 5 scale), time (to cook) and cost.
- Strings-fr and strings-en, string-ar.

The like button's twitter like animation is based on forgermcs's here: http://frogermcs.github.io/twitters-like-animation-in-android-alternative/, only very minor changes were made to it.

Please crticise everything you can about this app, I'll apreciate it very much.
