![alt tag](https://cloud.githubusercontent.com/assets/12183061/20238519/67b3116c-a8e5-11e6-84b2-95b69f572dbe.png)
![alt tag](https://cloud.githubusercontent.com/assets/12183061/20238518/64e9075c-a8e5-11e6-94c1-b42bbe86ce02.jpg)

Typical Android app about recipes (Content is in French for the moment) containing two main activities:
- A GridView Activity showing all the items (recipes) available.
- A per recipe Activity showing details for every recipe.

Data is stored locally in an SQLite database which you can download from app/src/main/assets/ (db is called "test"). The db is stored there because I needed initial data to be shipped with the App (this is very arguable because then the app would have redundant data).
The database contains one table called "recipes", composed of 10 attributes:
name, ingredients, preparation, time, cost, difficulty, image, author, aimer and _id. (aimer is french for like)

The targeted versions are 16 (JellyBean) to 22 (Kitkat).

What needs to be done in the app is simple (by order of priority):
- A filtering functionality for the list activity, not by recipe name, rather by "liked", difficulty level (difficulty is on a 1 to 5 scale), time (to cook) and cost.
- An add Recipe functionality (to the local DB of course), a good way to do that is with a FloatingActionButton in the list activity.
- Strings-fr, strings-en and string-ar.
- Maybe UI enhancement.

The like button's twitter like animation is based on forgermcs's here: http://frogermcs.github.io/twitters-like-animation-in-android-alternative/, only very minor changes were made to it.

Please crticise everything you can about this app, I'll apreciate it very much.

P.S: I'll be updating the app_debug.apk in app/ for apk download.
