package com.karim.ater.myexpenses.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.karim.ater.myexpenses.AppController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Created by Ater on 8/1/2018.
 */

public class
DatabaseConnector extends SQLiteOpenHelper {
    private static final int version = 1;
    private static final String TAG = DatabaseConnector.class.getSimpleName();
    private static String DB_PATH = "/data/data/com.karim.ater.myexpenses/databases/";
    private static final String DbName = "MyExpenses.db";
    Context context;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String account, profile;

    public DatabaseConnector(Context context) {
        super(context, DbName, null, version);
        this.context = context;
        getProfileAndAccount();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

//            createDataBase();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private static boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DbName;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //database does't exist yet.
            Log.d(TAG, "createDataBase: " + "database doesnt exist");
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    public void createDataBase() {
        boolean dbExist = checkDataBase();
        Log.d(TAG, "createDataBase: " + dbExist);
        if (dbExist) {
            //do nothing - database already exist
            Log.d(TAG, "Final database is ready");
        } else {
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
//            this.close();
            try {
                // copy the predefined database
                copyDataBase();

                SQLiteDatabase db = this.getWritableDatabase();
                //creating the Operations & Favorites tables
                db.execSQL("create table IF NOT EXISTS Operations(" +
                        "OperationId INTEGER primary key AUTOINCREMENT UNIQUE,Profile TEXT, Account TEXT," +
                        "ItemId TEXT,Direction TEXT, MainCategory TEXT," +
                        " CategoryName TEXT, CategoryType TEXT, " +
                        "PeriodIdentifier TEXT, ExpenseName TEXT,ExpenseNote TEXT, Cost REAL, Date TEXT )");
                db.execSQL("create table IF NOT EXISTS Favorites(ItemID INTEGER primary key,ItemName TEXT)");
                db.execSQL("create table IF NOT EXISTS Scheduler (ScheduleID INTEGER primary key,CategoryId TEXT," +
                        "ScheduleType TEXT,StartDate TEXT)");
//     Todo: update Ids

                db.close();

                ;
                Log.d(TAG, "Final database is ready");
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    // copying database process
    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DbName);
        // Path to the just created empty db
        String outFileName = DB_PATH + DbName;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
        Log.d(TAG, "Copy process is completed");
    }

    private void getProfileAndAccount() {
        account = AppController.getCurrentAccount();
        profile = AppController.getCurrentProfile();
    }

    public ArrayList<CategoryData> getCategoriesData() {
        ArrayList<CategoryData> categoriesData = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String categoriesDataQuery = "Select Id,MainCategory,CategoryName,CategoryType,Cost,CategoryLimiter,cnt,cst,ItemIcon from \n" +
                "(Select * from Categorizer where CategoryType like '%Random%') as firstSet\n" +
                "left join\n" +
                "(Select CategoryName,count(CategoryName) as cnt,sum(Cost) as cst from Operations group by CategoryName) as secondSet\n" +
                "using (CategoryName)";
        Cursor categoriesCursor = sqLiteDatabase.rawQuery(categoriesDataQuery, null);
        categoriesCursor.moveToFirst();

        //Todo: repeat getColumnIndex
        while (!categoriesCursor.isAfterLast()) {
            CategoryData categoryData = new CategoryData();
            categoryData.setCategoryId(categoriesCursor.getString(categoriesCursor.getColumnIndex("Id")));
            categoryData.setMainCategory(categoriesCursor.getString(categoriesCursor.getColumnIndex("MainCategory")));
            categoryData.setCategoryName(categoriesCursor.getString(categoriesCursor.getColumnIndex("CategoryName")));
            categoryData.setCategoryType(categoriesCursor.getString(categoriesCursor.getColumnIndex("CategoryType")));
            categoryData.setCost(categoriesCursor.getFloat(categoriesCursor.getColumnIndex("Cost")));
            categoryData.setCategoryLimiter(categoriesCursor.getFloat(categoriesCursor.getColumnIndex("CategoryLimiter")));
            categoryData.setCount(categoriesCursor.getInt(categoriesCursor.getColumnIndex("cnt")));
            categoryData.setTotalCost(categoriesCursor.getFloat(categoriesCursor.getColumnIndex("cst")));
            categoryData.setIcon(categoriesCursor.getString(categoriesCursor.getColumnIndex("ItemIcon")));
            categoriesData.add(categoryData);
            categoriesCursor.moveToNext();
        }
        categoriesCursor.close();

        String expensesDataQuery = "Select Id,MainCategory,CategoryName,CategoryType,PeriodIdentifier,ItemName,Cost," +
                "ItemLimiter,cnt,cst,ItemIcon from (Select * from Categorizer where CategoryType not like '%Random%') as firstSet "
                + "left join\n" +
                "(Select ExpenseName,count(ExpenseName) as cnt,sum(Cost) as cst from Operations group by ExpenseName) as secondSet "
                + "on (firstSet.ItemName=secondSet.ExpenseName)";
        Cursor expensesCursor = sqLiteDatabase.rawQuery(expensesDataQuery, null);
        expensesCursor.moveToFirst();
        while (!expensesCursor.isAfterLast()) {
            CategoryData categoryData = new CategoryData();
            categoryData.setCategoryId(expensesCursor.getString(expensesCursor.getColumnIndex("Id")));
            categoryData.setMainCategory(expensesCursor.getString(expensesCursor.getColumnIndex("MainCategory")));
            categoryData.setCategoryName(expensesCursor.getString(expensesCursor.getColumnIndex("CategoryName")));
            categoryData.setCategoryType(expensesCursor.getString(expensesCursor.getColumnIndex("CategoryType")));
            categoryData.setPeriodIdentifier(expensesCursor.getString(expensesCursor.getColumnIndex("PeriodIdentifier")));
            categoryData.setExpenseName(expensesCursor.getString(expensesCursor.getColumnIndex("ItemName")));
            categoryData.setCost(expensesCursor.getFloat(expensesCursor.getColumnIndex("Cost")));
            categoryData.setItemLimiter(expensesCursor.getFloat(expensesCursor.getColumnIndex("ItemLimiter")));
            categoryData.setCount(expensesCursor.getInt(expensesCursor.getColumnIndex("cnt")));
            categoryData.setTotalCost(expensesCursor.getFloat(expensesCursor.getColumnIndex("cst")));
            categoryData.setIcon(expensesCursor.getString(expensesCursor.getColumnIndex("ItemIcon")));
            categoriesData.add(categoryData);
            expensesCursor.moveToNext();
        }
        expensesCursor.close();
        sqLiteDatabase.close();
        return categoriesData;
    }

    public ArrayList<CategoryData> getCategoriesData(String searchText) {
        ArrayList<CategoryData> categoriesData = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String categoriesDataQuery = "Select Id,MainCategory,CategoryName,CategoryType,Cost,CategoryLimiter,cnt,cst,ItemIcon,ScheduleType from " +
                "(Select * from Categorizer where CategoryName like '%" + searchText + "%' and CategoryType like '%Random%') as firstSet " +
                " left join\n" +
                "(Select CategoryName,count(CategoryName) as cnt,sum(Cost) as cst from Operations group by CategoryName) as secondSet " +
                " using (CategoryName)" +
                " left join " +
                "(select CategoryId,ScheduleType from Scheduler) as thirdSet " +
                "on firstSet.ItemName=thirdSet.CategoryId";
        Cursor categoriesCursor = sqLiteDatabase.rawQuery(categoriesDataQuery, null);
        categoriesCursor.moveToFirst();
        while (!categoriesCursor.isAfterLast()) {
            CategoryData categoryData = new CategoryData();
            categoryData.setCategoryId(categoriesCursor.getString(categoriesCursor.getColumnIndex("Id")));
            categoryData.setMainCategory(categoriesCursor.getString(categoriesCursor.getColumnIndex("MainCategory")));
            categoryData.setCategoryName(categoriesCursor.getString(categoriesCursor.getColumnIndex("CategoryName")));
            categoryData.setCategoryType(categoriesCursor.getString(categoriesCursor.getColumnIndex("CategoryType")));
            categoryData.setCost(categoriesCursor.getFloat(categoriesCursor.getColumnIndex("Cost")));
            categoryData.setCategoryLimiter(categoriesCursor.getFloat(categoriesCursor.getColumnIndex("CategoryLimiter")));
            categoryData.setCount(categoriesCursor.getInt(categoriesCursor.getColumnIndex("cnt")));
            categoryData.setTotalCost(categoriesCursor.getFloat(categoriesCursor.getColumnIndex("cst")));
            categoryData.setIcon(categoriesCursor.getString(categoriesCursor.getColumnIndex("ItemIcon")));
            categoryData.setScheduleType(categoriesCursor.getString(categoriesCursor.getColumnIndex("ScheduleType")));
            categoriesData.add(categoryData);
            categoriesCursor.moveToNext();
        }
        categoriesCursor.close();

        String expensesDataQuery = "Select Id,MainCategory,CategoryName,CategoryType,PeriodIdentifier,ItemName,Cost,ItemLimiter,cnt,cst,ItemIcon,ScheduleType from \n" +
                "(Select * from Categorizer where ItemName like '%" + searchText + "%' and CategoryType not like '%Random%') as firstSet " +
                "left join" +
                "(Select ExpenseName,count(ExpenseName) as cnt,sum(Cost) as cst from Operations group by ExpenseName) as secondSet " +
                "on (firstSet.ItemName=secondSet.ExpenseName)" +
                " left join " +
                "(select CategoryId,ScheduleType from Scheduler) as thirdSet " +
                "on firstSet.ItemName=thirdSet.CategoryId";
        Cursor expensesCursor = sqLiteDatabase.rawQuery(expensesDataQuery, null);
        expensesCursor.moveToFirst();
        while (!expensesCursor.isAfterLast()) {
            CategoryData categoryData = new CategoryData();
            categoryData.setCategoryId(expensesCursor.getString(expensesCursor.getColumnIndex("Id")));
            categoryData.setMainCategory(expensesCursor.getString(expensesCursor.getColumnIndex("MainCategory")));
            categoryData.setCategoryName(expensesCursor.getString(expensesCursor.getColumnIndex("CategoryName")));
            categoryData.setCategoryType(expensesCursor.getString(expensesCursor.getColumnIndex("CategoryType")));
            categoryData.setPeriodIdentifier(expensesCursor.getString(expensesCursor.getColumnIndex("PeriodIdentifier")));
            categoryData.setExpenseName(expensesCursor.getString(expensesCursor.getColumnIndex("ItemName")));
            categoryData.setCost(expensesCursor.getFloat(expensesCursor.getColumnIndex("Cost")));
            categoryData.setItemLimiter(expensesCursor.getFloat(expensesCursor.getColumnIndex("ItemLimiter")));
            categoryData.setCount(expensesCursor.getInt(expensesCursor.getColumnIndex("Count")));
            categoryData.setTotalCost(expensesCursor.getFloat(expensesCursor.getColumnIndex("cst")));
            categoryData.setIcon(expensesCursor.getString(expensesCursor.getColumnIndex("ItemIcon")));
            categoryData.setScheduleType(expensesCursor.getString(expensesCursor.getColumnIndex("ScheduleType")));
            categoriesData.add(categoryData);
            expensesCursor.moveToNext();
        }
        expensesCursor.close();
        sqLiteDatabase.close();
        return categoriesData;
    }


    // get Category Names,CategoryLimiters,totalCosts for Categories
    public ArrayList<CategoryItem> getCategories(String formattedDate) {
        ArrayList<CategoryItem> categoryItems = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String categoriesQuery = "Select MainCategory,firstSet.CategoryName,CategoryLimiter,C from \n" +
                "(select MainCategory,CategoryName,CategoryLimiter from 'Categorizer' where Direction like 'Out' " +
                "and CategoryName!='' and CategoryLimiter not like '0.0' and CategoryLimiter != '' Group by CategoryName) as firstSet " +
                "left join\n" +
                "(select CategoryName,sum(Cost) as C FROM Operations where Direction like 'Out' and Profile ='" + profile + "' and Account ='" + account + "' " +
                "And date like '%" + formattedDate + "%' group by CategoryName) as secondSet " +
                "on firstSet.CategoryName=secondSet.CategoryName";
        Cursor cursor = sqLiteDatabase.rawQuery(categoriesQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CategoryItem categoryItem = new CategoryItem();
            categoryItem.setMainCategory(cursor.getString(0));
            categoryItem.setCategoryName(cursor.getString(1));
            categoryItem.setCategoryLimiter(cursor.getFloat(2));
            categoryItem.setCost(cursor.getFloat(3));
            categoryItems.add(categoryItem);
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return categoryItems;
    }

    // get all CategoryNames
    public ArrayList<String> getCategories() {
        ArrayList<String> categories = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String categoriesQuery = "Select CategoryName from Categorizer";
        Cursor cursor = sqLiteDatabase.rawQuery(categoriesQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            categories.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return categories;
    }

    // get Expenses Names,ItemLimiters,totalCosts for Expenses
    public ArrayList<CategoryItem> getExpensesNames(String formattedDate) {
        ArrayList<CategoryItem> categoryItems = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String expensesQuery = "Select MainCategory,CategoryName,ItemName,ItemLimiter,C from \n" +
                "(select MainCategory,CategoryName,ItemName,ItemLimiter from 'Categorizer' where Direction like 'Out' " +
                "and ItemName!='' and ItemLimiter!='' and ItemLimiter not like '0.0' Group by ItemName) as firstSet " +
                "left join" +
                "(select ExpenseName,sum(Cost) as C FROM Operations where Direction like 'Out' and Profile ='" + profile + "' and Account ='" + account + "' " +
                "And date like '%" + formattedDate + "%' group by ExpenseName) as secondSet " +
                "on firstSet.ItemName=secondSet.ExpenseName";
        Cursor cursor = sqLiteDatabase.rawQuery(expensesQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CategoryItem categoryItem = new CategoryItem();
            categoryItem.setMainCategory(cursor.getString(0));
            categoryItem.setCategoryName(cursor.getString(1));
            categoryItem.setExpenseName(cursor.getString(2));
            categoryItem.setItemLimiter(cursor.getFloat(3));
            categoryItem.setCost(cursor.getFloat(4));
            categoryItems.add(categoryItem);
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return categoryItems;
    }

    // get Fixed category Names
    public CategoriesClassification getFixedCategories() {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String periodItemsQuery = "select MainCategory,CategoryName from 'Categorizer' where CategoryType LIKE 'Fixed'" +
                " Group by CategoryName";
        Cursor cursor = sqLiteDatabase.rawQuery(periodItemsQuery, null);
        cursor.moveToFirst();
        int count = cursor.getCount();
        String[] mainCategories = new String[count];
        String[] categoryNames = new String[count];
        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            mainCategories[i] = cursor.getString(0);
            categoryNames[i] = cursor.getString(1);
            cursor.moveToNext();
            i++;
        }
        cursor.close();
        sqLiteDatabase.close();
        return new CategoriesClassification(mainCategories, categoryNames);
    }

    // get Subcategories of fixed category
    public ArrayList<CategoryItem> getFixedSubCategories(String category) {
        ArrayList<CategoryItem> fixedList = new ArrayList<CategoryItem>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String fixedItemsQuery = "select * from 'Categorizer' where CategoryType Like 'Fixed' And CategoryName Like '" + category + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(fixedItemsQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CategoryItem fixedCategoryItem = new CategoryItem();
            fixedCategoryItem.setCategoryId(cursor.getString(0));
            fixedCategoryItem.setDirection(cursor.getString(1));
            fixedCategoryItem.setMainCategory(cursor.getString(2));
            fixedCategoryItem.setCategoryName(cursor.getString(3));
            fixedCategoryItem.setCategoryType(cursor.getString(4));
            fixedCategoryItem.setExpenseName(cursor.getString(6));
            fixedCategoryItem.setCost(cursor.getFloat(7));
            fixedCategoryItem.setFavorite(cursor.getString(8).equals("true"));
            fixedCategoryItem.setItemLimiter(cursor.getFloat(10));
            fixedCategoryItem.setIcon(cursor.getString(11));
            fixedList.add(fixedCategoryItem);
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return fixedList;
    }

    // search Subcategories of fixed category
    public ArrayList<CategoryItem> searchFixedSubCategories(String category, String searchText) {
        ArrayList<CategoryItem> fixedList = new ArrayList<CategoryItem>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String fixedItemsQuery = "select * from 'Categorizer' where CategoryType Like 'Fixed' And CategoryName Like '"
                + category + "' And ItemName like '%" + searchText + "%'";
        Cursor cursor = sqLiteDatabase.rawQuery(fixedItemsQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CategoryItem fixedCategoryItem = new CategoryItem();
            fixedCategoryItem.setCategoryId(cursor.getString(0));
            fixedCategoryItem.setDirection(cursor.getString(1));
            fixedCategoryItem.setMainCategory(cursor.getString(2));
            fixedCategoryItem.setCategoryName(cursor.getString(3));
            fixedCategoryItem.setCategoryType(cursor.getString(4));
            fixedCategoryItem.setExpenseName(cursor.getString(6));
            fixedCategoryItem.setCost(cursor.getFloat(7));
            fixedCategoryItem.setFavorite(cursor.getString(8).equals("true"));
            fixedCategoryItem.setItemLimiter(cursor.getFloat(10));
            fixedCategoryItem.setIcon(cursor.getString(11));
            fixedList.add(fixedCategoryItem);
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return fixedList;
    }

    // get Periodic category Names
//    public ArrayList<CategoryItem> getPeriodicList() {
////        ArrayList<CategoryItem> periodicList = new ArrayList<CategoryItem>();
////        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
////        String periodItemsQuery = "select * from 'Categorizer' where CategoryType LIKE 'Periodic'";
////        Cursor cursor = sqLiteDatabase.rawQuery(periodItemsQuery, null);
////        cursor.moveToFirst();
////        while (!cursor.isAfterLast()) {
////            periodicList.add(new CategoryItem(cursor.getString(0), cursor.getString(1),
////                    cursor.getString(2), cursor.getString(3), cursor.getString(4),
////                    cursor.getString(5), cursor.getFloat(6), cursor.getString(7).equals("true")));
////            cursor.moveToNext();
////        }
////        cursor.close();
////        sqLiteDatabase.close();
////        return periodicList;
//    }

    // get subcategories for specific periodic identifier
    public ArrayList<CategoryItem> getPeriodicSubCategories(String periodIdentifier) {
        ArrayList<CategoryItem> periodicList = new ArrayList<CategoryItem>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String periodicItemsQuery = "select * from 'Categorizer' where PeriodIdentifier Like '" + periodIdentifier + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(periodicItemsQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CategoryItem periodicCategoryItem = new CategoryItem();
            periodicCategoryItem.setCategoryId(cursor.getString(0));
            periodicCategoryItem.setDirection(cursor.getString(1));
            periodicCategoryItem.setMainCategory(cursor.getString(2));
            periodicCategoryItem.setCategoryName(cursor.getString(3));
            periodicCategoryItem.setCategoryType(cursor.getString(4));
            periodicCategoryItem.setExpenseName(cursor.getString(6));
            periodicCategoryItem.setCost(cursor.getFloat(7));
            periodicCategoryItem.setFavorite(cursor.getString(8).equals("true"));
            periodicCategoryItem.setItemLimiter(cursor.getFloat(10));
            periodicCategoryItem.setIcon(cursor.getString(11));
            periodicCategoryItem.setPeriodIdentifier(periodIdentifier);
            periodicList.add(periodicCategoryItem);
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return periodicList;
    }

    // search subcategories for specific periodic identifier
    public ArrayList<CategoryItem> searchPeriodicSubCategories(String periodIdentifier, String searchText) {
        ArrayList<CategoryItem> periodicList = new ArrayList<CategoryItem>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String periodicItemsQuery = "select * from 'Categorizer' where PeriodIdentifier Like '" + periodIdentifier +
                "' And ItemName like '%" + searchText + "%'";
        Cursor cursor = sqLiteDatabase.rawQuery(periodicItemsQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CategoryItem periodicCategoryItem = new CategoryItem();
            periodicCategoryItem.setCategoryId(cursor.getString(0));
            periodicCategoryItem.setDirection(cursor.getString(1));
            periodicCategoryItem.setMainCategory(cursor.getString(2));
            periodicCategoryItem.setCategoryName(cursor.getString(3));
            periodicCategoryItem.setCategoryType(cursor.getString(4));
            periodicCategoryItem.setExpenseName(cursor.getString(6));
            periodicCategoryItem.setCost(cursor.getFloat(7));
            periodicCategoryItem.setFavorite(cursor.getString(8).equals("true"));
            periodicCategoryItem.setItemLimiter(cursor.getFloat(10));
            periodicCategoryItem.setIcon(cursor.getString(11));
            periodicList.add(periodicCategoryItem);
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return periodicList;
    }

    // get subcategories for specific random category
    public ArrayList<CategoryItem> getRandomListByMainCategory(String category) {
        ArrayList<CategoryItem> catList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String RandomItemsQuery;
        RandomItemsQuery = "select * from 'Categorizer' where CategoryType LIKE 'Random' " +
                "And MainCategory Like '" + category + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(RandomItemsQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CategoryItem randomCategoryItem = new CategoryItem();
            randomCategoryItem.setCategoryId(cursor.getString(0));
            randomCategoryItem.setDirection(cursor.getString(1));
            randomCategoryItem.setMainCategory(cursor.getString(2));
            randomCategoryItem.setCategoryName(cursor.getString(3));
            randomCategoryItem.setCategoryType(cursor.getString(4));
            randomCategoryItem.setExpenseName(cursor.getString(6));
            randomCategoryItem.setCost(cursor.getFloat(7));
            randomCategoryItem.setFavorite(cursor.getString(8).equals("true"));
            randomCategoryItem.setCategoryLimiter(cursor.getFloat(9));
            randomCategoryItem.setIcon(cursor.getString(11));
            catList.add(randomCategoryItem);

//            this.flowDirection = flowDirection;
//            this.mainCategory = mainCategory;
//            this.categoryName = categoryName;
//            this.categoryType = categoryType;
//            this.periodIdentifier = periodIdentifier;
//            this.expenseName = expenseName;
//            this.cost = cost;
//            this.favorite = favorite;

//            catList.add(new CategoryItem(cursor.getString(0), cursor.getString(1),
//                    cursor.getString(2), cursor.getString(3), cursor.getString(4),
//                    cursor.getString(5), cursor.getFloat(6), cursor.getString(7).equals("true")),
//                    cursor.getFloat(8));
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return catList;
    }

    // search subcategories for specific random category
    public ArrayList<CategoryItem> searchRandomListByMainCategory(String mainCategory, String searchText) {
        ArrayList<CategoryItem> catList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String RandomItemsQuery;
        RandomItemsQuery = "select * from 'Categorizer' where CategoryType LIKE 'Random' " +
                "And MainCategory Like '" + mainCategory + "' And CategoryName like '%" + searchText + "%' ";
        Cursor cursor = sqLiteDatabase.rawQuery(RandomItemsQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CategoryItem randomCategoryItem = new CategoryItem();
            randomCategoryItem.setCategoryId(cursor.getString(0));
            randomCategoryItem.setDirection(cursor.getString(1));
            randomCategoryItem.setMainCategory(cursor.getString(2));
            randomCategoryItem.setCategoryName(cursor.getString(3));
            randomCategoryItem.setCategoryType(cursor.getString(4));
            randomCategoryItem.setExpenseName(cursor.getString(6));
            randomCategoryItem.setCost(cursor.getFloat(7));
            randomCategoryItem.setFavorite(cursor.getString(8).equals("true"));
            randomCategoryItem.setCategoryLimiter(cursor.getFloat(9));
            randomCategoryItem.setIcon(cursor.getString(11));
            catList.add(randomCategoryItem);
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return catList;
    }

    // get favorites for each category type
    public ArrayList<CategoryItem> getFavoritesByCategoryType(String categoryType) {
        ArrayList<CategoryItem> favList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String favoritesList;
        favoritesList = "select * from 'Categorizer' where CategoryType LIKE '" + categoryType + "' And " +
                "Favorite Like 'true'";
        Cursor cursor = sqLiteDatabase.rawQuery(favoritesList, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CategoryItem favoriteCategoryItem = new CategoryItem();
            favoriteCategoryItem.setCategoryId(cursor.getString(0));
            favoriteCategoryItem.setDirection(cursor.getString(1));
            favoriteCategoryItem.setMainCategory(cursor.getString(2));
            favoriteCategoryItem.setCategoryName(cursor.getString(3));
            favoriteCategoryItem.setCategoryType(cursor.getString(4));
            favoriteCategoryItem.setPeriodIdentifier(cursor.getString(5));
            favoriteCategoryItem.setExpenseName(cursor.getString(6));
            favoriteCategoryItem.setCost(cursor.getFloat(7));
            favoriteCategoryItem.setFavorite(cursor.getString(8).equals("true"));
            favoriteCategoryItem.setCategoryLimiter(cursor.getFloat(9));
            favoriteCategoryItem.setItemLimiter(cursor.getFloat(10));
            favoriteCategoryItem.setIcon(cursor.getString(11));
            favList.add(favoriteCategoryItem);
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return favList;
    }

    // search favorites for each category type
    public ArrayList<CategoryItem> searchFavoritesByCategoryType(String categoryType, String searchText) {
        ArrayList<CategoryItem> favList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String favoritesList;
        favoritesList = "select * from 'Categorizer' where CategoryType LIKE '" + categoryType + "' And " +
                "Favorite Like 'true' And (CategoryName like '%" + searchText + "%' or ItemName like '%" + searchText + "%')";
        Cursor cursor = sqLiteDatabase.rawQuery(favoritesList, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CategoryItem favoriteCategoryItem = new CategoryItem();
            favoriteCategoryItem.setCategoryId(cursor.getString(0));
            favoriteCategoryItem.setDirection(cursor.getString(1));
            favoriteCategoryItem.setMainCategory(cursor.getString(2));
            favoriteCategoryItem.setCategoryName(cursor.getString(3));
            favoriteCategoryItem.setCategoryType(cursor.getString(4));
            favoriteCategoryItem.setPeriodIdentifier(cursor.getString(5));
            favoriteCategoryItem.setExpenseName(cursor.getString(6));
            favoriteCategoryItem.setCost(cursor.getFloat(7));
            favoriteCategoryItem.setFavorite(cursor.getString(8).equals("true"));
            favoriteCategoryItem.setCategoryLimiter(cursor.getFloat(9));
            favoriteCategoryItem.setItemLimiter(cursor.getFloat(10));
            favoriteCategoryItem.setIcon(cursor.getString(11));
            favList.add(favoriteCategoryItem);
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return favList;
    }


    // add periodic/fixed expense
    void addExpense(CategoryItem categoryItem) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Calendar todayDate = Calendar.getInstance();
        String formattedDate = dateFormat.format(todayDate.getTime());
        getProfileAndAccount();

//        db.execSQL("create table IF NOT EXISTS Operations(" +
//                "OperationId INTEGER primary key AUTOINCREMENT UNIQUE,Profile TEXT, Account TEXT," +
//                "ItemId TEXT,Direction TEXT, MainCategory TEXT," +
//                " CategoryName TEXT, CategoryType TEXT, " +
//                "PeriodIdentifier TEXT, ExpenseName TEXT,ExpenseNote TEXT, Cost REAL, Date TEXT )");

        ContentValues expenseCv = new ContentValues();
        expenseCv.put("Profile", profile);
        expenseCv.put("Account", account);
        expenseCv.put("ItemId", categoryItem.getCategoryId());
        expenseCv.put("Direction", "Out");
        expenseCv.put("MainCategory", categoryItem.getMainCategory());
        expenseCv.put("CategoryName", categoryItem.getCategoryName());
        expenseCv.put("CategoryType", categoryItem.getCategoryType());
        expenseCv.put("PeriodIdentifier", categoryItem.getPeriodIdentifier());
        expenseCv.put("ExpenseName", categoryItem.getExpenseName());
        expenseCv.put("Cost", categoryItem.getCost());
        expenseCv.put("Date", formattedDate);
//        String expenseQuery = "Insert into 'Operations' (Profile,Account,Direction,MainCategory,CategoryName,CategoryType," +
//                "PeriodIdentifier,ExpenseName,ExpenseNote,Cost,Date) " +
//                "Values('" + profile + "','" + account + "','Out','" + categoryItem.getMainCategory() +
//                "','" + categoryItem.getCategoryName() + "','" + categoryItem.getCategoryType() + "','" +
//                categoryItem.getPeriodIdentifier() + "','" + categoryItem.getExpenseName() + "','','" +
//                categoryItem.getCost() + "','" + formattedDate + "')";
        sqLiteDatabase.insert("Operations", null, expenseCv);
        sqLiteDatabase.close();
    }

    // add random expense
    public void addExpense(Transaction transaction) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        getProfileAndAccount();
//        String expenseQuery = "Insert into 'Operations' (Profile,Account,Direction,MainCategory,CategoryName,CategoryType," +
//                "PeriodIdentifier,ExpenseName,ExpenseNote,Cost,Date)" + " Values ('" + profile + "','" + account + "','Out','"
//                + mainCategory + "','" + categoryName + "','Random',' ','" + expenseName + "','" + notes + "','" + cost
//                + "','" + formattedDate + "')";

        ContentValues expenseCv = new ContentValues();
        expenseCv.put("Profile", profile);
        expenseCv.put("Account", account);
        expenseCv.put("ItemId", transaction.getCategoryId());
        expenseCv.put("Direction", "Out");
        expenseCv.put("MainCategory", transaction.getMainCategory());
        expenseCv.put("CategoryName", transaction.getCategoryName());
        expenseCv.put("CategoryType", transaction.getCategoryType());
        expenseCv.put("ExpenseName", transaction.getExpenseName());
        expenseCv.put("Cost", transaction.getCost());
        expenseCv.put("Date", transaction.getTransactionDate());
        sqLiteDatabase.insert("Operations", null, expenseCv);

        sqLiteDatabase.close();
    }

    // add periodic/fixed expense with an old date
    public void addExpense(CategoryItem categoryItem, String oldDate, String note) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        getProfileAndAccount();
        String expenseQuery = "Insert into 'Operations' (Profile,Account,Direction,MainCategory,CategoryName,CategoryType,PeriodIdentifier,ExpenseName,ExpenseNote,Cost,Date) " +
                "Values('" + profile + "','" + account + "','Out','" + categoryItem.getMainCategory() + "','" +
                categoryItem.getCategoryName() + "','" + categoryItem.getCategoryType() + "','" +
                categoryItem.getPeriodIdentifier() + "','" + categoryItem.getExpenseName() + "','" + note + "','" + categoryItem.getCost() + "','" + oldDate + "')";
        sqLiteDatabase.execSQL(expenseQuery);
        sqLiteDatabase.close();
    }

    // add income
    public void addIncome(String categoryName, String categoryType, String identifier, float value, String note, String formattedDate) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
//        Calendar todayDate = Calendar.getInstance();
//        String formattedDate = dateFormat.format(todayDate.getTime());
        String incomeQuery = "Insert into 'Operations' (Profile,Account,Direction,MainCategory,CategoryName,CategoryType," +
                "PeriodIdentifier,Cost,ExpenseName,ExpenseNote,Date)" + " Values ('" + profile + "','" + account + "','In','Income','"
                + categoryName + "','" + categoryType + "','" + identifier + "','" + value + "','" + categoryName + "','" +
                note + "','" + formattedDate + "')";
        sqLiteDatabase.execSQL(incomeQuery);
    }

    // Update name/cost of category or expense
    public boolean updateItem(CategoryItem categoryItem, String newName, String oldName) {
        boolean duplicate, sameItem;
        sameItem = oldName.equals(newName);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        if (categoryItem.getCategoryType().equals("Random")) {
            duplicate = isRandomCategoryDuplicate(categoryItem.getMainCategory(), newName);
            if (!duplicate || sameItem) {
                sqLiteDatabase.execSQL("UPDATE 'Categorizer' set CategoryName = '"
                        + newName + "',CategoryLimiter = '" + categoryItem.getCategoryLimiter() +
                        "' where CategoryName Like '" + oldName + "' And CategoryType like 'Random'");
                return false;
            }
        } else {
            if (categoryItem.getCategoryType().equals("Periodic"))
                duplicate = isCategoryDuplicate(categoryItem.getCategoryType(), categoryItem.getPeriodIdentifier(), newName);
            else
                duplicate = isCategoryDuplicate(categoryItem.getCategoryType(), categoryItem.getCategoryName(), newName);
            if (!duplicate || sameItem) {
                sqLiteDatabase.execSQL("UPDATE 'Categorizer' set Cost = '" + categoryItem.getCost() + "',ItemName = '"
                        + newName + "',ItemLimiter = '" + categoryItem.getItemLimiter() +
                        "' where ItemName Like '" + oldName + "' And CategoryType like '" + categoryItem.getCategoryType()
                        + "'");
                return false;
            }

        }
        sqLiteDatabase.close();
        return true;
    }

    // Delete Category or Expense from Categories list
    public void deleteExpense(CategoryItem categoryItem) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("Delete FROM 'Categorizer' where CategoryName like '" + categoryItem.getCategoryName() + "' And CategoryType like '" +
                categoryItem.getCategoryType() + "' And PeriodIdentifier like '" + categoryItem.getPeriodIdentifier() +
                "' And ItemName like '" + categoryItem.getExpenseName() + "'");
        sqLiteDatabase.close();
    }

    public void deleteTransaction() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("Select max(ItemID) from 'Operations'", null);
        cursor.moveToFirst();
        int maxId = cursor.getInt(0);
        sqLiteDatabase.delete("Operations", "ItemID = '" + maxId + "'", null);

        sqLiteDatabase.close();
    }

    public void deleteTransaction(int transactionId) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        sqLiteDatabase.delete("Operations", "ItemID = '" + transactionId + "'", null);
        sqLiteDatabase.close();
    }

    void addToFavorites(CategoryItem item) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query;
        if (item.getCategoryType().equals("Random")) {
            query = "UPDATE 'Categorizer' SET Favorite = 'true' where CategoryName like '" + item.getCategoryName() +
                    "' And CategoryType like 'Random'";
        } else {
            query = "UPDATE 'Categorizer' SET Favorite = 'true' where ItemName like '" + item.getExpenseName() +
                    "' And CategoryType like '" + item.getCategoryType() + "'";
        }
        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();

    }

    void removeFromFavorites(CategoryItem item) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query;
        if (item.getCategoryType().equals("Random")) {
            query = "UPDATE 'Categorizer' SET Favorite = '' where CategoryName like '" + item.getCategoryName() + "' And CategoryType like 'Random'";
        } else {
            query = "UPDATE 'Categorizer' SET Favorite = '' where ItemName like '" + item.getExpenseName() + "'";
        }
        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    public void addCategoryExpense(CategoryItem categoryItem) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues initCv = new ContentValues();
        initCv.put("Direction", "Out");
        initCv.put("MainCategory", categoryItem.getMainCategory());
        initCv.put("CategoryName", categoryItem.getCategoryName());
        initCv.put("ItemName", categoryItem.getExpenseName());
        initCv.put("Cost", categoryItem.getCost());
        initCv.put("CategoryType", categoryItem.getCategoryType());
        initCv.put("PeriodIdentifier", categoryItem.getPeriodIdentifier());
        initCv.put("Favorite", String.valueOf(categoryItem.isFavorite()));
        initCv.put("CategoryLimiter", categoryItem.getCategoryLimiter());
        initCv.put("ItemLimiter", categoryItem.getCategoryLimiter());
        initCv.put("ItemIcon", categoryItem.getIcon());
        sqLiteDatabase.insert("Categorizer", null, initCv);
        sqLiteDatabase.close();
    }

    public boolean addRandomCategory(CategoryItem categoryItem) {
        String mainCategory = categoryItem.getMainCategory();
        String categoryName = categoryItem.getCategoryName();
        boolean duplicate = isRandomCategoryDuplicate(mainCategory, categoryName);
        if (!duplicate) {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues initCv = new ContentValues();
            initCv.put("Direction", "Out");
            initCv.put("MainCategory", mainCategory);
            initCv.put("CategoryName", categoryName);
            initCv.put("CategoryType", "Random");
            initCv.put("ItemName", "");
            initCv.put("Cost", "");
            initCv.put("PeriodIdentifier", "");
            initCv.put("Favorite", "");
            initCv.put("CategoryLimiter", "");
            initCv.put("ItemLimiter", "");
            initCv.put("ItemIcon", categoryItem.getIcon());
            sqLiteDatabase.insert("Categorizer", null, initCv);
        }
        return duplicate;
    }

    private boolean isRandomCategoryDuplicate(String mainCategory, String categoryName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String searchQuery = "Select * FROM 'Categorizer' where CategoryType like 'Random' and MainCategory like '%"
                + mainCategory + "%' And CategoryName = '" + categoryName + "'";
        Cursor searchCursor = sqLiteDatabase.rawQuery(searchQuery, null);
        return (searchCursor.getCount() > 0);
    }

    public boolean addNewPeriodicItem(CategoryItem categoryItem) {
        String expenseName = categoryItem.getExpenseName();
        String periodicIdentifier = categoryItem.getPeriodIdentifier();
        boolean duplicate = isCategoryDuplicate("Periodic", periodicIdentifier, expenseName);
        if (!duplicate) {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues initCv = new ContentValues();
            initCv.put("Direction", "Out");
            initCv.put("CategoryType", "Periodic");
            initCv.put("Favorite", "");
            initCv.put("CategoryLimiter", "");
            initCv.put("ItemLimiter", "");
            initCv.put("PeriodIdentifier", periodicIdentifier);
            initCv.put("ItemIcon", categoryItem.getIcon());
            initCv.put("MainCategory", categoryItem.getMainCategory());
            initCv.put("CategoryName", categoryItem.getCategoryName());
            initCv.put("ItemName", categoryItem.getExpenseName());
            initCv.put("Cost", categoryItem.getCost());
            sqLiteDatabase.insert("Categorizer", null, initCv);
        }
        return duplicate;
    }

    public boolean addNewFixedItem(CategoryItem categoryItem) {
        String expenseName = categoryItem.getExpenseName();
        String categoryName = categoryItem.getCategoryName();
        boolean duplicate = isCategoryDuplicate("Fixed", categoryName, expenseName);
        if (!duplicate) {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues initCv = new ContentValues();
            initCv.put("Direction", "Out");
            initCv.put("CategoryType", "Fixed");
            initCv.put("PeriodIdentifier", "");
            initCv.put("Favorite", "");
            initCv.put("CategoryLimiter", "");
            initCv.put("ItemLimiter", "");
            initCv.put("ItemIcon", categoryItem.getIcon());
            initCv.put("MainCategory", categoryItem.getMainCategory());
            initCv.put("CategoryName", categoryItem.getCategoryName());
            initCv.put("ItemName", categoryItem.getExpenseName());
            initCv.put("Cost", categoryItem.getCost());
            sqLiteDatabase.insert("Categorizer", null, initCv);
        }
        return duplicate;
    }


    public void reorder(ArrayList<CategoryItem> categoryItems) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (CategoryItem categoryItem : categoryItems) {
            switch (categoryItem.getCategoryType()) {
                case "Random":
                    deleteExpense(categoryItem);
                    addRandomCategory(categoryItem);
                    break;
                case "Periodic":
                    deleteExpense(categoryItem);
                    addNewPeriodicItem(categoryItem);
                    break;
                case "Fixed":
                    deleteExpense(categoryItem);
                    addNewFixedItem(categoryItem);
                    break;
            }
        }
    }

    private boolean isCategoryDuplicate(String categoryType, String categoryOrIdentifier, String expenseName) {
        String searchQuery = null;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        switch (categoryType) {
            case "Fixed":
                searchQuery = "Select * FROM 'Categorizer' where CategoryName = '"
                        + categoryOrIdentifier + "' And ItemName = '" + expenseName + "'";
                break;
            case "Periodic":
                searchQuery = "Select * FROM 'Categorizer' where PeriodIdentifier = '"
                        + categoryOrIdentifier + "' And ItemName = '" + expenseName + "'";
                break;
        }
        Cursor searchCursor = sqLiteDatabase.rawQuery(searchQuery, null);

        return (searchCursor.getCount() > 0);
    }


    public ArrayList<Transaction> getOperations(String duration, String searchText) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query;

        if (searchText == null || searchText.equals("")) {
            if (duration.contains("23:59:59")) {
                query = "Select Account,ItemId,MainCategory,firstSet.CategoryName,ExpenseName,firstSet.CategoryType,Cost,Date,ItemIcon from (\n" +
                        "Select * From Operations where Profile ='" + profile + "' and Account ='" + account + "' and Date Between '" + duration +
                        "' ORDER BY `Date` Desc ) as firstSet " +
                        "left join  " +
                        "( Select Id,ItemIcon from Categorizer ) as secondSet " +
                        "on firstSet.ItemId=secondSet.Id";
            } else {
                query = "Select Account,ItemId,MainCategory,firstSet.CategoryName,ExpenseName,firstSet.CategoryType,Cost,Date,ItemIcon from " +
                        "(Select * From Operations where Profile ='" + profile + "' and Account ='" + account + "' and Date like '%"
                        + duration + "%' ORDER BY `Date` Desc) as firstSet " +
                        "left join " +
                        "(Select Id,ItemIcon from Categorizer) as secondSet " +
                        "on firstSet.ItemId=secondSet.Id";
            }
        } else {
            if (duration.contains("23:59:59")) {

                query = "Select Account,ItemId,MainCategory,firstSet.CategoryName,ExpenseName,firstSet.CategoryType,Cost,Date,ItemIcon" +
                        " from " +
                        "(Select * From Operations where Profile ='" + profile + "' and Account ='" + account + "' and ExpenseName like '%" + searchText + "%'" +
                        "and Date Between '" + duration + "' ORDER BY `Date` Desc) as firstSet " +
                        "left join " +
                        "(Select Id,ItemIcon from Categorizer) as secondSet " +
                        "on firstSet.ItemId=secondSet.Id";
            } else {


                query = "Select Account,ItemId,MainCategory,firstSet.CategoryName,ExpenseName,firstSet.CategoryType,Cost,Date,ItemIcon" +
                        " from " +
                        "(Select * from Operations where Profile ='" + profile + "' and Account ='" + account + "' and ExpenseName like '%" + searchText + "%' " +
                        "and Date like '%" + duration + "%' ORDER BY `Date` Desc) as firstSet " +
                        "left join " +
                        "(Select Id,ItemIcon from Categorizer) as secondSet " +
                        "on firstSet.ItemId=secondSet.Id";
            }
        }

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(cursor.getInt(1));
            transaction.setMainCategory(cursor.getString(2));
            transaction.setCategoryName(cursor.getString(3));
            transaction.setExpenseName(cursor.getString(4));
            transaction.setCategoryType(cursor.getString(5));
            transaction.setCost(cursor.getFloat(6));
            transaction.setTransactionDate(cursor.getString(7));
            transaction.setIcon(cursor.getString(8));
            transactions.add(transaction);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return transactions;
    }

    public ArrayList<Transaction> getOperations(String categoryLevel, String category, String duration, String searchText) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query;

        if (searchText == null || searchText.equals("")) {
            if (duration.contains("23:59:59")) {
                query = "Select Account,ItemId,MainCategory,firstSet.CategoryName,ExpenseName,firstSet.CategoryType,Cost,Date,ItemIcon from (\n" +
                        "Select * From Operations where Profile ='" + profile + "' and Account ='" + account + "' and " + categoryLevel +
                        "='" + category + "' and Date Between '" + duration +
                        "' ORDER BY `Date` Desc ) as firstSet " +
                        "left join  " +
                        "( Select Id,ItemIcon from Categorizer ) as secondSet " +
                        "on firstSet.ItemId=secondSet.Id";
            } else {
                query = "Select Account,ItemId,MainCategory,firstSet.CategoryName,ExpenseName,firstSet.CategoryType,Cost,Date,ItemIcon from " +
                        "(Select * From Operations where Profile ='" + profile + "' and Account ='" + account + "' and " + categoryLevel +
                        "='" + category + "' and Date like '%"
                        + duration + "%' ORDER BY `Date` Desc) as firstSet " +
                        "left join " +
                        "(Select Id,ItemIcon from Categorizer) as secondSet " +
                        "on firstSet.ItemId=secondSet.Id";
            }
        } else {
            if (duration.contains("23:59:59")) {


                query = "Select Account,ItemId,MainCategory,firstSet.CategoryName,ExpenseName,firstSet.CategoryType,Cost,Date,ItemIcon" +
                        " from " +
                        "(Select * From Operations where Profile ='" + profile + "' and Account ='" + account + "' and " + categoryLevel +
                        "='" + category + "' and ExpenseName like '%" + searchText + "%'" + " and Date Between '" + duration + "' ORDER BY `Date` Desc) as firstSet " +
                        "left join " +
                        "(Select Id,ItemIcon from Categorizer) as secondSet " +
                        "on firstSet.ItemId=secondSet.Id";
            } else {


                query = "Select Account,ItemId,MainCategory,firstSet.CategoryName,ExpenseName,firstSet.CategoryType,Cost,Date,ItemIcon" +
                        " from " +
                        "(Select * from Operations where Profile ='" + profile + "' and Account ='" + account + "' and " + categoryLevel +
                        "='" + category + "' and ExpenseName like '%" + searchText + "%'" + " and Date like '%" + duration + "%' ORDER BY `Date` Desc) as firstSet " +
                        "left join " +
                        "(Select Id,ItemIcon from Categorizer) as secondSet " +
                        "on firstSet.ItemId=secondSet.Id";
            }
        }
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(cursor.getInt(1));
            transaction.setMainCategory(cursor.getString(2));
            transaction.setCategoryName(cursor.getString(3));
            transaction.setExpenseName(cursor.getString(4));
            transaction.setCategoryType(cursor.getString(5));
            transaction.setCost(cursor.getFloat(6));
            transaction.setTransactionDate(cursor.getString(7));
            transaction.setIcon(cursor.getString(8));
            transactions.add(transaction);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return transactions;
    }

    public ArrayList<Entry> totalCostByDay(ArrayList<String> dateValues, MyCalendar.CALENDAR_MODE calendarMode) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Entry> lineEntries = new ArrayList<>();
        int size = dateValues.size();
        String intervalTotalQuery;
        if (calendarMode == MyCalendar.CALENDAR_MODE.WEEK) {
            for (int i = 0; i < size; i++) {
                intervalTotalQuery = "select sum(Cost) FROM Operations where Direction like 'Out' and Profile ='" + profile +
                        "' and Account ='" + account + "' And Date between " + dateValues.get(i);
                Cursor cursor = db.rawQuery(intervalTotalQuery, null);
                cursor.moveToFirst();
                lineEntries.add(new Entry(i, cursor.getFloat(0)));
            }

        } else {

            for (int i = 0; i < size; i++) {
                intervalTotalQuery = "select sum(Cost) FROM Operations where Direction like 'Out' and Profile ='" + profile +
                        "' and Account ='" + account + "' And Date like '%" + dateValues.get(i) + "%'";
                Cursor cursor = db.rawQuery(intervalTotalQuery, null);
                cursor.moveToFirst();
                lineEntries.add(new Entry(i, cursor.getFloat(0)));
            }
        }
        return lineEntries;
    }

    public ArrayList<BarEntry> totalCostByDay(ArrayList<String> dateValues, MyCalendar.CALENDAR_MODE calendar_mode, String flow) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        int size = dateValues.size();
        String intervalTotalQuery;
        if (calendar_mode == MyCalendar.CALENDAR_MODE.WEEK) {
            for (int i = 0; i < size; i++) {
                intervalTotalQuery = "select sum(Cost) FROM Operations where Direction like '" + flow + "' and Profile ='" + profile +
                        "' and Account ='" + account + "' And Date between " + dateValues.get(i);
                Cursor cursor = db.rawQuery(intervalTotalQuery, null);
                cursor.moveToFirst();
                barEntries.add(new BarEntry(i, cursor.getFloat(0)));
            }

        } else {

            for (int i = 0; i < size; i++) {
                intervalTotalQuery = "select sum(Cost) FROM Operations where Direction like '" + flow + "' and Profile ='" + profile +
                        "' and Account ='" + account + "' And Date like '%" + dateValues.get(i) + "%'";
                Cursor cursor = db.rawQuery(intervalTotalQuery, null);
                cursor.moveToFirst();
                barEntries.add(new BarEntry(i, cursor.getFloat(0)));
            }
        }
        return barEntries;

    }

    public ArrayList<PieEntry> totalCostBy(String formattedDate, MyCalendar.CALENDAR_MODE calendarMode) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<PieEntry> entries = new ArrayList<>();
        String costQuery;
        if (calendarMode == MyCalendar.CALENDAR_MODE.WEEK) {
            costQuery = "select MainCategory, sum(Cost) FROM Operations where Direction like 'Out' and Profile ='" +
                    profile + "' and Account ='" + account + "' and Date between '" + formattedDate + "' group by MainCategory ";
        } else {
            costQuery = "select MainCategory, sum(Cost) FROM Operations where Direction like 'Out' and Profile ='" +
                    profile + "' and Account ='" + account + "' and Date like '%" + formattedDate + "%' group by MainCategory ";
        }
        Cursor cursor = db.rawQuery(costQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            entries.add(new PieEntry(cursor.getFloat(1), cursor.getString(0)));
            cursor.moveToNext();
        }
        return entries;
    }

    public LinkedHashMap totalCostMapBy(String categoryLevel, String formattedDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        LinkedHashMap map = new LinkedHashMap();
        String costQuery;
        if (formattedDate.contains("And")) {
            costQuery = "select " + categoryLevel + ", sum(Cost) FROM Operations where Direction like 'Out' and Profile ='" + profile +
                    "' and Account ='" + account + "' And date Between '" + formattedDate
                    + "' group by " + categoryLevel + " ORDER BY sum(Cost) DESC";
        } else {
            costQuery = "select " + categoryLevel + ", sum(Cost) FROM Operations where Direction like 'Out' and Profile ='" + profile +
                    "' and Account ='" + account + "' And date like '%" + formattedDate + "%' group by " + categoryLevel +
                    " ORDER BY sum(Cost) DESC";
        }
        Cursor cursor = db.rawQuery(costQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            map.put(cursor.getString(0), cursor.getFloat(1));
            cursor.moveToNext();
        }
        return map;
    }

    public float getTotalCost(String duration) {
        SQLiteDatabase db = this.getReadableDatabase();
        String costQuery;
        if (duration.contains("And"))
            costQuery = "select sum(Cost) FROM Operations where Direction Like 'Out' and Profile ='" + profile +
                    "' and Account ='" + account + "' and Date Between '" + duration + "'";
        else
            costQuery = "select sum(Cost) FROM Operations where Direction Like 'Out' and Profile ='" + profile +
                    "' and Account ='" + account + "' and Date like '%" + duration + "%'";
        Cursor cursor = db.rawQuery(costQuery, null);
        cursor.moveToFirst();
        float cost = cursor.getFloat(0);
        cursor.close();
        return cost;
    }

    public float getMonthlyIncome(String duration) {

        SQLiteDatabase db = this.getReadableDatabase();
        String costQuery;
        costQuery = "select sum(Cost) FROM Operations where Direction Like 'In' and Profile ='" + profile +
                "' and Account ='" + account + "' and Date like '%" + duration + "%'";
        Cursor cursor = db.rawQuery(costQuery, null);
        cursor.moveToFirst();
        float cost = cursor.getFloat(0);
        cursor.close();
        return cost;
    }

    public int getNumberOfMonths() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("Select Date from Operations Order by date ASC limit 1", null);
        String x = cursor.getString(0);
        Calendar startCalendar = Calendar.getInstance();
        Date date = null;
        try {
            date = dateFormat.parse(x);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        startCalendar.setTime(date);

        Calendar endCalendar = Calendar.getInstance();
        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

        return diffMonth + diffYear;
    }


    public ArrayList<CategoryItem> getRandomCategories() {
        ArrayList<CategoryItem> randomCategoryItems = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("Select Id,MainCategory, CategoryName,ItemIcon from Categorizer where Direction like 'Out'" +
                " and CategoryType like 'Random' group by CategoryName", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CategoryItem randomCategoryItem = new CategoryItem();
            randomCategoryItem.setCategoryId(cursor.getString(cursor.getColumnIndex("Id")));
            randomCategoryItem.setMainCategory(cursor.getString(cursor.getColumnIndex("MainCategory")));
            randomCategoryItem.setCategoryName(cursor.getString(cursor.getColumnIndex("CategoryName")));
            randomCategoryItem.setIcon(cursor.getString(cursor.getColumnIndex("ItemIcon")));
            randomCategoryItem.setDirection("Out");
            randomCategoryItem.setCategoryType("Random");
            randomCategoryItems.add(randomCategoryItem);
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return randomCategoryItems;

    }

    public void updateTransaction(Transaction updatedTransaction) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("UPDATE 'Operations' set Cost = '" + updatedTransaction.getCost() + "'," +
                "ExpenseName = '" + updatedTransaction.getExpenseName() + "', MainCategory = '" +
                updatedTransaction.getMainCategory() + "',CategoryName ='" + updatedTransaction.getCategoryName() +
                "',Date ='" + updatedTransaction.getTransactionDate() +
                "' where ItemID Like '" + updatedTransaction.getTransactionId() + "'");
        sqLiteDatabase.close();
    }

    public void restoreTransaction(Transaction transaction) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String restoreTransactionQuery = "Insert into 'Operations' (Profile,Account,ItemId,Direction,MainCategory,CategoryName,CategoryType," +
                "PeriodIdentifier,ExpenseName,ExpenseNote,Cost,Date) " +
                "Values('" + profile + "','" + account + "','" + transaction.getTransactionId() + "','Out','" +
                transaction.getMainCategory() + "','" + transaction.getCategoryName() +
                "','" + transaction.getCategoryType() + "','" + transaction.getPeriodIdentifier() + "','" +
                transaction.getExpenseName() + "','','" + transaction.getCost() + "','" + transaction.getTransactionDate() + "')";
        sqLiteDatabase.execSQL(restoreTransactionQuery);
        sqLiteDatabase.close();
    }

    public String[] getCategoriesByMainCategory(String mainCategory) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("Select CategoryType, CategoryName from Categorizer where Direction" +
                " like 'Out' and MainCategory ='" + mainCategory + "' GROUP BY CategoryName", null);
        ArrayList<CategoryItem> categoryItems = new ArrayList<>();
        cursor.moveToFirst();
        int i = 0;
        String[] categories = new String[cursor.getCount()];
        while (!cursor.isAfterLast()) {
            categories[i] = cursor.getString(1);
            i++;
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return categories;
    }

    public boolean activeToday() {
        boolean active;
        DateFormat notifDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String todayDate = notifDateFormat.format(calendar.getTime());
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String todayCheck = "Select * FROM Operations where Date like '%" + todayDate + "%'";
        Cursor cursor = sqLiteDatabase.rawQuery(todayCheck, null);
        active = cursor.getCount() > 0;
        cursor.close();
        sqLiteDatabase.close();
        return active;
    }


//    public String extractIconName(){
//        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
//        Cursor cursor = sqLiteDatabase.rawQuery("Select CategoryType, CategoryName from Categorizer where Direction" +
//                " like 'Out' and MainCategory ='" + mainCategory + "' GROUP BY CategoryName", null);
//        ArrayList<CategoryItem> categoryItems = new ArrayList<>();
//        cursor.moveToFirst();
//        int i = 0;
//        String[] add_category_main = new String[cursor.getCount()];
//        while (!cursor.isAfterLast()) {
//            add_category_main[i] = cursor.getString(1);
//            i++;
//            cursor.moveToNext();
//        }
//        cursor.close();
//        sqLiteDatabase.close();
//        return add_category_main;
//    }

    //Todo: Delete CategClass class
    public class CategoriesClassification {
        String[] mainCategory, categoryName, expenseName;

        CategoriesClassification(String[] mainCategory, String[] categoryName) {
            this.mainCategory = mainCategory;
            this.categoryName = categoryName;
        }

        CategoriesClassification(String[] mainCategory, String[] categoryName, String[] expenseName) {
            this(mainCategory, categoryName);
            this.expenseName = expenseName;
        }

        public String[] getMainCategory() {
            return mainCategory;
        }

        public String[] getCategoryName() {
            return categoryName;
        }

        public String[] getExpenseName() {
            return expenseName;
        }
    }
}
