package me.personal.jfeeke.multimediamobileviewer;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BusyActivity extends AppCompatActivity {
    SQLiteDatabase dbMap;
    DatabaseAccess databaseAccess;
    TextView updateProgressText;
    private static String clientID  = "2068636120"; // Put your clientID here.
    private static String clientTag = "23ED0FAD97FD68FFB77292D146A64912"; // Put your clientTag here.
    private static String userID = "48232543321839177-91601FFAD6496D28059A11BE76AB1C44";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private final boolean fromExternalSource = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busy);

        updateProgressText = findViewById(R.id.busyIndicatorUpdateMessage);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.override_busy_title);

        // Check the external database file. External database must be available for the first time deployment.
        String externalDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MultiMediaManager";
        File dbFile = new File(externalDirectory, DatabaseOpenHelper.DATABASE_NAME);
        if (!dbFile.exists()) {
            return;
        }
        // If external database is available, deploy it
        databaseAccess = DatabaseAccess.getMappingInstance(this, externalDirectory);

        GracenoteTest();

        Runnable task = new Runnable() {
            @Override
            public void run() {
                Thread busyThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            super.run();
                            //permissions
                            // Request for permission to read external storage
                            if (fromExternalSource && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                            } else {
                                // Run code to do the Artist checks
                                //#######################################################
                                //get artists from the database
                                updateProgressText = findViewById(R.id.busyIndicatorUpdateMessage);
                                updateProgressText.setText(R.string.loader_database);
                                databaseAccess.open();
                                ArrayList<List<String>> databaseData = databaseAccess.getDistinctData();
                                databaseAccess.close();
                                //#######################################################
                                //database to update with artists locations
                                dbMap = openOrCreateDatabase("MultiMediaDB", Context.MODE_PRIVATE, null);
                                dbMap.execSQL(getString(R.string.createLocalDBStringArtists));

                                int dataCount = databaseData.size();
                                int newDataCount = databaseData.size();
                                for (int i = 0; i < dataCount; i++) {
                                    updateProgressText = findViewById(R.id.busyIndicatorUpdateMessage);
                                    updateProgressText.setText(String.format(getString(R.string.busy_counter), i+1, dataCount));

                                    List<String> currRow = databaseData.get(i);

                                    String myString = currRow.get(0);
                                    myString = myString.replaceAll("[^a-zA-Z0-9\\.\\-]", "");

                                    Cursor a = dbMap.rawQuery("SELECT * FROM map_database WHERE artist = '" + myString + "'", null);
                                    boolean hasData;
                                    hasData = a.getCount() != 0;

                                    if (!hasData) {
                                        //search the web for other attributes
                                        //MusicGraph Key: db501a8b13d6eba5dd58f0799c4b5b20
                                        //sample http://api.musicgraph.com/api/v2/artist/search?api_key=db501a8b13d6eba5dd58f0799c4b5b20&name=currRow.get(3)&fields=country
                                        String sUrl = "http://api.musicgraph.com/api/v2/artist/search?api_key=db501a8b13d6eba5dd58f0799c4b5b20&name=" + currRow.get(0) + "&fields=country";

                                        GetUrlContentTask asyncTask = new GetUrlContentTask(new GetUrlContentTaskResponse() {
                                            @Override
                                            public void processFinish(String output) {
                                                String returnedTask = output;

                                            }
                                        });
                                        asyncTask.execute(sUrl);

                                        //uniqueid VARCHAR,country VARCHAR,city VARCHAR,lat VARCHAR,long VARCHAR
                                        //dbMap.execSQL("INSERT INTO map_database VALUES(" + newDataCount + ",'','','',''," + currRow.get(0) + ");");
                                    }
                                    a.close();
                                }

                                //add
                                //dbMap.execSQL("INSERT INTO mmdatabase VALUES('','','','','');");
                                //#######################################################
                                updateProgressText = findViewById(R.id.busyIndicatorUpdateMessage);
                                updateProgressText.setText(R.string.loading_map);
                            }
                        } catch (Exception e) {
                            //Error occurred
                        } finally {
                            Runnable task = new Runnable() {
                                @Override
                                public void run() {
                                    Thread busyThread = new Thread() {

                                        @Override
                                        public void run() {
                                            try {
                                                super.run();
                                                //sleep(3000);  //Delay of 3 seconds
                                            } catch (Exception e) {
                                                //Error occurred
                                            } finally {

                                                Intent i = new Intent(BusyActivity.this,
                                                        MapActivity.class);
                                                startActivity(i);
                                                //finish();
                                            }
                                        }
                                    };
                                    busyThread.start();
                                    finish();
                                }
                            };
                            task.run();
                        }
                    }
                };
                busyThread.start();
            }
        };
        task.run();
    }

    public void GracenoteTest()
    {
        try
        {
            /* You first need to register your client information in order to get a userID.
            Best practice is for an application to call this only once, and then cache the userID in
            persistent storage, then only use the userID for subsequent API calls. The class will cache
            it for just this session on your behalf, but you should store it yourself. */
            GracenoteWebAPI api = new GracenoteWebAPI(clientID, clientTag,userID); // If you have a userID, you can specify it as the third parameter to constructor.
            String userID = api.register();
            //System.out.println("UserID = " + userID);

            // Once you have the userID, you can search for tracks, artists or albums easily.
            //System.out.println("Search Track:");
            GracenoteMetadata results = api.searchTrack("Moby", "Play", "Porcelin");
            //results.print();

            //System.out.println("Search Artist:");
            results = api.searchArtist("Moby");
            //results.print();

            //System.out.println("Search Album:");
            results = api.searchAlbum("Moby", "Play");
            results.print();

            //System.out.println("Fetch Album:");
            results = api.fetchAlbum("2068636120-23ED0FAD97FD68FFB77292D146A64912");
            //results.print();
        }
        catch (GracenoteException e)
        {
            e.printStackTrace();
        }
    }
}
