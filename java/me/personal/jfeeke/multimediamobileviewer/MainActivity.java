package me.personal.jfeeke.multimediamobileviewer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifTextView;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    TextView copyRightNotice, textViewHeader,textViewUniqueID,textViewName,textViewType,textViewDataCount;
    EditText editName,editType;
    AutoCompleteTextView editUniqueIDDropDown;
    Button btnClearCache,btnSearch,btnLoadData,btnAdd,btnDelete,btnDeleteAll,btnModify,btnView,btnViewAll,btnShowInfo,btnHelp,btnMap;
    ListView listView;
    SQLiteDatabase db;
    ProgressBar progressBar;
    String searchText = "";
    GifTextView loader,wait;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private final boolean fromExternalSource = true;

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.override_title);

        //Initialization
        copyRightNotice=findViewById(R.id.copyRightNotice);
        textViewHeader=findViewById(R.id.textViewHeader);
        textViewUniqueID=findViewById(R.id.textViewUniqueID);
        textViewName=findViewById(R.id.textViewName);
        textViewType=findViewById(R.id.textViewType);
        textViewDataCount=findViewById(R.id.textViewDataCount);
        editUniqueIDDropDown=findViewById(R.id.editUniqueIDDropDown);
        editName=findViewById(R.id.editName);
        editType=findViewById(R.id.editType);
        btnAdd=findViewById(R.id.btnAdd);
        btnDelete=findViewById(R.id.btnDelete);
        btnClearCache=findViewById(R.id.btnClearCache);
        btnDeleteAll=findViewById(R.id.btnDeleteAll);
        btnModify=findViewById(R.id.btnModify);
        btnView=findViewById(R.id.btnView);
        btnViewAll=findViewById(R.id.btnViewAll);
        btnSearch=findViewById(R.id.btnSearch);
        btnShowInfo=findViewById(R.id.btnShowInfo);
        btnLoadData=findViewById(R.id.btnLoadData);
        progressBar = findViewById(R.id.progressbar);

        //Listeners
        btnAdd.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnClearCache.setOnClickListener(this);
        btnDeleteAll.setOnClickListener(this);
        btnModify.setOnClickListener(this);
        btnView.setOnClickListener(this);
        btnViewAll.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnShowInfo.setOnClickListener(this);
        btnLoadData.setOnClickListener(this);

        db=openOrCreateDatabase("MultiMediaDB", Context.MODE_PRIVATE, null);
        db.execSQL(getString(R.string.createLocalDBString));

        setDropDownValues();

        final ListView listview=findViewById(R.id.listView);
        listview.setOnItemClickListener((parent, view, position, id) -> showDataInPopup(position));

        showMessage("Multi-Media Manager Mobile QUICK Tips", "There are FOUR Sections of this App:\n1) Notes:\n  - Buttons: Add, Delete, Delete ALL,\n    Modify, View, View ALL\n" +
                "  - Editable Text: Enter Unique-ID, Enter Note,\n    Enter Category and their Text Entries\n2) Data:\n  - Buttons: Load Data\n  - Display: The List View\n" +
                "3) Tools:\n  - Search Icon: Search the 'LOAD DATA' Database\n  - Delete Icon: Clear Cache and App Data\n    (p.s. Does not Delete the 'LOAD DATA' Database)\n" +
                "  - For More Detailed Information use the Info Button     (Top Right of Screen)\n" +
                "  - About Button: Developer Information\n" +
                "4) Map:\n  - Browse a Map\n  - Discover the Location(Place of Birth)\n   of the Artists in your Database\n" +
                "   p.s. This is based on the Accuracy of your Music"
        );

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //activity_helper function
        btnHelp = findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener(this);

        //activity_map function
        btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(this);
    }
    public void onClick(View view)
    {
        if(view==btnMap)
        {
            Thread mapThread = new Thread() {

                @Override
                public void run() {
                    try {
                        super.run();
                        //sleep(3000);  //Delay of 3 seconds
                    } catch (Exception e) {
                        //Error occurred
                    } finally {

                        Intent i = new Intent(MainActivity.this,
                                BusyActivity.class);
                        startActivity(i);
                        //finish();
                    }
                }
            };
            mapThread.start();
        }
        if(view==btnHelp)
        {
            Thread welcomeThread = new Thread() {

                @Override
                public void run() {
                    try {
                        super.run();
                        //sleep(3000);  //Delay of 3 seconds
                    } catch (Exception e) {
                        //Error occurred
                    } finally {

                        Intent i = new Intent(MainActivity.this,
                                HelperActivity.class);
                        startActivity(i);
                        //finish();
                    }
                }
            };
            welcomeThread.start();
        }
        if(view==btnClearCache)
        {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        deleteCache();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you would like to Clear App Data and Cache?\n" +
                    "This only needs to be performed when updating your Main Database")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
        }
        if(view==btnSearch)
        {
            // get search_popup.xml view
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            @SuppressLint("InflateParams") View searchPopup = layoutInflater.inflate(R.layout.search_popup,null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set prompts.xml to alert dialog builder
            alertDialogBuilder.setView(searchPopup);

            final EditText userInput = searchPopup
                    .findViewById(R.id.searchInput);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Search",
                            (dialog, id) -> {
                                // get user input and set it to result
                                searchText = userInput.getText().toString();
                                //do a database search

                                new Thread(() -> progressBar.post(this::getData)).start();
                                displayProgress(true);
                            })
                    .setNegativeButton("Cancel",
                            (dialog, id) -> dialog.cancel());

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
        if(view==btnLoadData)
        {
            //clear the search text so we do not tell the data loader to search
            searchText="";
            this.progressBar = findViewById(R.id.progressbar);
            new Thread(() -> progressBar.post(this::getData)).start();
            displayProgress(true);
        }
        if(view==btnAdd)
        {
            if(editUniqueIDDropDown.getText().toString().trim().length()==0||
                    editName.getText().toString().trim().length()==0||
                    editType.getText().toString().trim().length()==0)
            {
                showMessage("Information", "Please enter all values");
                return;
            }
            db.execSQL("INSERT INTO mmdatabase VALUES('"+editUniqueIDDropDown.getText()+"','"+editName.getText()+
                    "','"+editType.getText()+"');");
            showMessage("Success", "Record Added");
            clearText();
            setDropDownValues();
        }
        if(view==btnDelete)
        {
            if(editUniqueIDDropDown.getText().toString().trim().length()==0)
            {
                showMessage("Information", "Please enter Unique-ID");
                return;
            }
            Cursor c=db.rawQuery("SELECT * FROM mmdatabase WHERE uniqueid='"+editUniqueIDDropDown.getText()+"'", null);
            if(c.moveToFirst())
            {
                db.execSQL("DELETE FROM mmdatabase WHERE uniqueid='"+editUniqueIDDropDown.getText()+"'");
                showMessage("Success", "Record Deleted");
            }
            else
            {
                showMessage("Information", "Invalid Unique-ID");
            }
            clearText();
            c.close();
            setDropDownValues();
        }
        if(view==btnDeleteAll)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Confirmation:");
            builder.setMessage("Are you Sure you would like to Delete All the Notes?");
            builder.setPositiveButton("Confirm",
                    (dialog, which) -> {
                        Cursor c=db.rawQuery(getString(R.string.SelectAllDataForNotes), null);
                        if(c.getCount()!=0)
                        {
                            while(c.moveToNext()) {
                                db.execSQL("DELETE FROM mmdatabase WHERE uniqueid='" + c.getString(0) + "'");
                            }
                        }
                        else
                        {
                            showMessage("Information", "No Notes have been Found!");
                        }
                        clearText();
                        c.close();
                    });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> showMessage("Information", "No Notes have been Deleted!"));

            AlertDialog dialog = builder.create();
            dialog.show();
            setDropDownValues();
        }
        if(view==btnModify)
        {
            if(editUniqueIDDropDown.getText().toString().trim().length()==0)
            {
                showMessage("Information", "Please enter a Unique-ID");
                return;
            }
            Cursor c=db.rawQuery("SELECT * FROM mmdatabase WHERE uniqueid='"+editUniqueIDDropDown.getText()+"'", null);
            if(c.moveToFirst())
            {
                db.execSQL("UPDATE mmdatabase SET name='"+editName.getText()+"',type='"+editType.getText()+
                        "' WHERE uniqueid='"+editUniqueIDDropDown.getText()+"'");
                showMessage("Success", "Record Modified");
            }
            else
            {
                showMessage("Information", "Invalid Unique-ID");
            }
            clearText();
            c.close();
            setDropDownValues();
        }
        if(view==btnView)
        {
            if(editUniqueIDDropDown.getText().toString().trim().length()==0)
            {
                showMessage("Information", "Please enter a Unique-ID");
                return;
            }
            Cursor c=db.rawQuery("SELECT * FROM mmdatabase WHERE uniqueid='"+editUniqueIDDropDown.getText()+"'", null);
            if(c.moveToFirst())
            {
                editName.setText(c.getString(1));
                editType.setText(c.getString(2));
            }
            else
            {
                showMessage("Information", "Invalid Unique-ID");
                clearText();
            }
            c.close();
        }
        if(view==btnViewAll)
        {
            Cursor c=db.rawQuery(getString(R.string.SelectAllDataForNotes), null);
            if(c.getCount()==0)
            {
                showMessage("Information", "No records found");
                return;
            }
            StringBuilder buffer=new StringBuilder();
            while(c.moveToNext())
            {
                buffer.append("Unique-ID: ").append(c.getString(0)).append("\n");
                buffer.append("Note: ").append(c.getString(1)).append("\n");
                buffer.append("Category: ").append(c.getString(2)).append("\n\n");
            }
            showMessage("Database Details", buffer.toString());
            c.close();
        }
        if(view==btnShowInfo)
        {
            showMessage("Multi-Media Management Application", "Developed By J.Feeke © 2018. All Rights Reserved.");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showDataInList();
            } else {
                //Toast.makeText(this, "Until you grant the permission, we cannot display the Data from the Database", Toast.LENGTH_SHORT).show();
                showMessage("Information", "Until you grant the permission, we cannot display the Data from the Database");
            }
        }

    }
    private void getData()
    {
        //String fileName = "Multi-Media iTunes Library.sqlite";
        //String myDirectory = "MultiMediaManager";

        this.listView = findViewById(R.id.listView);
        // Request for permission to read external storage
        if (fromExternalSource && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            showDataInList();
            showMessage("Information", "Touch a Row to get Data from All the Fields!");
            this.progressBar = findViewById(R.id.progressbar);
            displayProgress(false);
        }
    }
    private void showDataInList()
    {
        DatabaseAccess databaseAccess;
        if (fromExternalSource) {
            // Check the external database file. External database must be available for the first time deployment.
            String externalDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MultiMediaManager";
            File dbFile = new File(externalDirectory, DatabaseOpenHelper.DATABASE_NAME);
            if (!dbFile.exists()) {
                return;
            }
            // If external database is available, deploy it
            databaseAccess = DatabaseAccess.getInstance(this, externalDirectory);
        } else {
            // From assets
            databaseAccess = DatabaseAccess.getInstance(this, null);
        }

        databaseAccess.open();
        ArrayList<List<String>> databaseData = databaseAccess.getData(searchText);
        databaseAccess.close();

        int dataCount = databaseData.size();
        textViewDataCount.setText(String.format(getString(R.string.dataRecordCount), dataCount));

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, databaseData);
        //this.listView.setAdapter(adapter);

        TextView columnHeader1 = findViewById(R.id.column_header1);
        TextView columnHeader2 = findViewById(R.id.column_header2);
        TextView columnHeader3 = findViewById(R.id.column_header3);
        TextView columnHeader4 = findViewById(R.id.column_header4);
        TextView columnHeader5 = findViewById(R.id.column_header5);
        TextView columnHeader6 = findViewById(R.id.column_header6);
        TextView columnHeader7 = findViewById(R.id.column_header7);

        columnHeader1.setText(R.string.TrackID);
        columnHeader2.setText(R.string.TrackName);
        columnHeader3.setText(R.string.TrackArtist);
        columnHeader4.setText(R.string.Genre);
        columnHeader5.setText(R.string.Rating);
        columnHeader6.setText(R.string.Time);
        columnHeader7.setText(R.string.Year);

        ListView view = findViewById(R.id.listView);
        final ArrayList<MyStringPair> myStringPairList = MyStringPair.makeData(databaseData);
        MyStringPairAdapter adapter = new MyStringPairAdapter(this, myStringPairList);

        view.setAdapter(adapter);
    }
    private void showDataInPopup(int position)
    {
        DatabaseAccess databaseAccess;
        if (fromExternalSource) {
            // Check the external database file. External database must be available for the first time deployment.
            String externalDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MultiMediaManager";
            File dbFile = new File(externalDirectory, DatabaseOpenHelper.DATABASE_NAME);
            if (!dbFile.exists()) {
                return;
            }
            // If external database is available, deploy it
            databaseAccess = DatabaseAccess.getInstance(this, externalDirectory);
        } else {
            // From assets
            databaseAccess = DatabaseAccess.getInstance(this, null);
        }

        databaseAccess.open();
        ArrayList<List<String>> databaseData = databaseAccess.getDataRow(position+1);
        databaseAccess.close();

        StringBuilder thePopupString = new StringBuilder();
        for (int i = 0;i < databaseData.get(0).size();i++)
        {
            thePopupString.append(databaseData.get(0).get(i)).append(": ").append(databaseData.get(1).get(i)).append("\n");
        }
        showMessage("Information", thePopupString.toString());
    }
    private void displayProgress(Boolean canShow)
    {
        this.progressBar = findViewById(R.id.progressbar);
        progressBar.setIndeterminate(canShow);
        progressBar.requestFocus();
        this.loader = findViewById(R.id.loader);
        this.wait = findViewById(R.id.wait);
        if(canShow){this.loader.setVisibility(View.VISIBLE);}else{this.loader.setVisibility(View.INVISIBLE);}
        this.wait.setVisibility(View.INVISIBLE);
    }
    private void deleteCache()
    {
        try {
            // Delete local cache dir (ignoring any errors):
            Boolean success = FileUtils.deleteQuietly(getCacheDir());
            if(success) {
                showMessage("Information", "Cache and App Data Cleared Successfully!");
            }
            else
            {
                showMessage("Information","Failed to Clear Cache and App Data!");
            }
        } catch (Exception e) {
            //Failed to Delete
            showMessage("Information","Failed to Clear Cache and App Data with Error: " + e.getMessage());
        }
    }
    private void showMessage(String title, String message)
    {
        Builder builder=new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    private void clearText()
    {
        editUniqueIDDropDown.setText("");
        editName.setText("");
        editType.setText("");
        //editUniqueIDDropDown.requestFocus();
    }
    private void setDropDownValues()
    {
        ArrayList<String> uniqueIDs = new ArrayList<>();
        Cursor c=db.rawQuery(getString(R.string.SelectAllDataForNotes), null);
        while(c.moveToNext())
        {
            uniqueIDs.add(c.getString(0));
        }
        c.close();

        //Creating the instance of ArrayAdapter containing list of language names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, uniqueIDs);
        //Getting the instance of AutoCompleteTextView
        AutoCompleteTextView act=findViewById(R.id.editUniqueIDDropDown);
        act.setThreshold(1);//will start working from first character
        act.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
    }
}