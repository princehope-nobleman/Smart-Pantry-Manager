**#Smart Pantry Manager**

The smart pantry manager is an android app that tracks what is in the pantry and shows what recipes a user can cook with said ingredients.
Cooking of a recipe subtracts the required amounts from the pantry.

**Application Version and Package**:
Package: `com.example.smartpantrymanager`  
Version: 1.0 (`versionCode` 1)

**Application Features**
Pantry Inventory: Utilises CRUDE models to allow the addition, edting and deletion of ingredients with names, quanity and units along the denominations:`pcs`, `g`, `kg`, `ml`, `L`, `cups`, `tbsp`, `tsp`, `oz`, `lbs`
Suggested recipes: The application is able to view or display recipes that can be made only when the ingredients in the pantry are suffiecient to make said recipe
All recipes: Allows the user to browse the possible recipes they can make even when the ingredients are unavailbale
Cook: confirms the deductions of recipe ingredients from the matching pantry items. An item is removed when its quanitity is zero or less.
Settings: Switches the display unit-system label between the matric or imperial systems, along with restoring default recipes or clearing the panrty.
Recipe detail: Each recipe in the application shjows the caetpgry along with the prp time, instructions and staus of the ingredients in the pantry

**Default home screen:**
The home screen shows how many recipes are currently ready to make and an empty state when the pantry has no ingredients

**Application Built in recipes:**

| Recipe | Category | Prep |
| --- | --- | --- |
| Pancakes | Breakfast | 15 min |
| Omelette | Breakfast | 10 min |
| Scrambled Eggs | Breakfast | 5 min |
| Grilled Cheese Sandwich | Lunch | 10 min |
| Fried Rice | Lunch | 15 min |
| Tomato Pasta | Dinner | 20 min |
| Garlic Bread | Snack | 12 min |
| Fruit Smoothie | Beverage | 5 min |

**Application Tech stack usage:**
-Java
-Android Gradle Plugin 9.2.1
-'minSdk' 24, 'compileSdk' / 'targetSdk' 36
- Java 11
- AndroidX AppCompat, Material Components, ConstraintLayout
- SQLite via `SQLiteOpenHelper` (`smart_pantry.db`)
- SharedPreferences for the unit-system setting (`PREFS_SMART_PANTRY`)

**Application requirements:**
-Android Studio with Android SDK 36
-JDK 11 or newer
-An Emulator or device running the Android 7.0 (API 24) or higher. Makes it compatitble with 99.8% of devices

**Building and Runnning the application**
To run this application, clone the repo then open the project in Android Studio and tun the 'app' configuration

'''bash
git clone https://github.com/princehope-nobleman/Smart-Pantry-Manager.git
cd Smart-Pantry-Manager
```
Or run the debug APK from the command line. On Windows:
```bat
gradlew.bat assembleDebug
```
**Application Project STructure:**
```text
app/src/main/java/com/example/smartpantrymanager/
  MainActivity.java                 Pantry list and navigation
  AddEditIngredientActivity.java    Create or update an ingredient
  SuggestedRecipesActivity.java     Suggested and all-recipes tabs
  RecipeDetailActivity.java         Instructions and cook action
  SettingsActivity.java             Unit label, reset, and clear
  DatabaseHelper.java               SQLite access and recipe matching
  Ingredient.java                   Pantry item
  Recipe.java                       Recipe
  RecipeIngredient.java             Required ingredient on a recipe
  IngredientAdapter.java
  RecipeAdapter.java
  RecipeIngredientAdapter.java
```
**Application Data**
The application utilises the 'DatabaseHelper' to store three tables:
pantry_ingredients: name, quantity, unit
recipes: title, instructions, category, prep time
recipe_ingredients: recipe id, name, quantity, unit (cascade delete)


