
<b>Layouts:</b><br>
![alt tag](https://cloud.githubusercontent.com/assets/12183061/20642806/320ba5ec-b411-11e6-9048-76b91b2acb58.png)
![alt tag](https://cloud.githubusercontent.com/assets/12183061/20642810/364dbc12-b411-11e6-9716-4fa6dc15a255.png)
![alt tag](https://cloud.githubusercontent.com/assets/12183061/20775431/cf6a3e22-b752-11e6-8365-f540c1db053b.png)



<b>Animations:</b><br>
![alt tag](https://cloud.githubusercontent.com/assets/12183061/20367855/3a510fe6-ac49-11e6-86e5-b7841abe3e29.gif)
![alt tag](https://cloud.githubusercontent.com/assets/12183061/20367849/31709d56-ac49-11e6-97e9-f723284a75fc.gif)


Typical Android app about recipes (Content is in French for the moment) containing 3 activities:
- MainListActivity: A GridView Activity showing all the items (recipes) available.
- MainActivity: A per recipe Activity showing details for every recipe.
- NewRecipeActivity: Activity for adding a new recipe.

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

<b>P.S:</b> I'll be updating the app_debug.apk in app/ regularly for apk download.
