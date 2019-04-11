package com.example.abdulkadir.abdulkadir_mpdcoursework;

//StudentName:Abdulkadir Abdulrehman
//        StudentID           S1712579
//        MPD: Coursework
//        program of study:computing

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

// this class implements on click listener
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener
{


    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private XmlHandler handleXML = new XmlHandler();
    private ArrayList<ListItemObject> items;
    private ArrayList<ListItemObject> itemstemp;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportActionBar().setTitle("Abdulkadir-Abdulrehman");
        getSupportActionBar().setSubtitle("S1712579:MPD-CWK @GCU");

        if (!isNetworkAvailable()){
            Log.d("MyTag", "device is Not Online");

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Network Error");
            alertDialog.setMessage("Please enable your internet services");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

        }else {
            Log.d("MyTag", "device is Online network is ready");


            new Thread(new Task()).start();
        }
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //search menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu,menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) { return false; }

    //searching the array with user input text and updating the listView
    @Override
    public boolean onQueryTextChange(String newText)
    {

        String input = newText.toLowerCase();
        items = new ArrayList<>();

        int i = 0;
        while ( i < itemstemp.size()) {
            ListItemObject getitem = itemstemp.get(i);
            getitem.getTitle();

            if (getitem.getTitle().toLowerCase().contains(input))
            {
                items.add(getitem);
            }
            i++;
        }

        if (items.isEmpty()){
            items = new ArrayList<>();
            listView(items);
            return true;
        }
        else {
            listView(items);
            return true;
        }

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    private class Task implements Runnable
    {

        @Override
        public void run()
        {
            try
            {
                Log.e("MyTag","in try");
                URL url=new URL(urlSource);
                HttpURLConnection http=(HttpURLConnection)url.openConnection();
                http.setDoInput(true);
                http.connect();
                InputStream is=http.getInputStream();
                XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
                xmlPullParser.setInput(is,null);
                handleXML.ProcessData(xmlPullParser);

            }
            catch (IOException e) { Log.e("MyTag", "ioexception"); }
            catch (XmlPullParserException e) { e.printStackTrace(); }

            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    System.out.println(handleXML.listitem.size());
                    System.out.println(handleXML.itemObjects.size());
                    items = new ArrayList<>(handleXML.listitem);
                    itemstemp = items;
                    //spinner();
                    listView(items);
                }
            });
        }
    }



    //listViews
    public void listView(final ArrayList<ListItemObject> item)
    {
        ListView listView = findViewById(R.id.listView);
        ListAdapter listAdapter = new adapter(MainActivity.this,item);
        listView.setAdapter(listAdapter);
        //if list view clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //open map map location
                if (isServicesOK()){ OpenMap(position);}
            }
        });

    }


    public void acknowledgement(View v)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Acknowledgement");
        alertDialog.setMessage("information source: British Geological Survey ©NERC 2019");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Back",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

  //checking services
    public boolean isServicesOK()
    {
        final int ERROR_DIALOG_REQUEST = 9001;
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(available == ConnectionResult.SUCCESS){

            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){

            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
           //if things went wrong displays error message
            Toast.makeText(this, "Can't Make Map Requests", Toast.LENGTH_SHORT).show();
        }
        return false;

    }

    public void OpenMap(int pos)
    {
        ListItemObject getitem = items.get(pos);
        String T = getitem.getTitle();
        ItemObject getitemObjects;


        int i = 0;
        while ( i < handleXML.listitem.size()){
            getitemObjects = handleXML.itemObjects.get(i);

            if (getitemObjects.getTitle() == T)
            {

                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                intent.putExtra("Object", handleXML.itemObjects.get(i));
                startActivity(intent);
                break;
            }
            i++;
        }
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if (position == 0 ){
            items = itemstemp;
            Collections.sort(items , Alphabetical);
            listView(items);

        }else if (position == 1){
            items = itemstemp;
            listView(handleXML.listitem);

        }else if (position == 2){
            items = itemstemp;
            Collections.sort(items , Depth);
            Collections.reverse(items);
            listView(items);

        }else if (position == 3){
            items = itemstemp;
            Collections.sort(items , Magnitude);
            Collections.reverse(items);
            listView(items);
        } else if (position == 4){

            //ic_scotland
            items = new ArrayList<>();
            ItemObject getitemObjects;
            boolean Scotland=false;
            int i = 0;
            while ( i < handleXML.listitem.size()){

                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    ListItemObject item = new ListItemObject();
                    getitemObjects = handleXML.itemObjects.get(i);

                    double lat = Double.parseDouble(getitemObjects.getgLat());
                    double lng = Double.parseDouble(getitemObjects.getgLong());

                    List<Address> addresses = gcd.getFromLocation(lat, lng, 1);
                    if (addresses.size() > 0)
                    {
                        String AdminArea=addresses.get(0).getAdminArea();
                        if (AdminArea == null){Scotland = false;}
                        else { Scotland = AdminArea.equals("Scotland");}

                        if (Scotland) {

                            String mString = getitemObjects.getMagnitude();
                            mString = mString.replace("Magnitude: ", "");
                            double lMagnitude = Double.parseDouble(mString);


                            int D = Integer.parseInt(getitemObjects.getDepth());
                            item.setTitle(getitemObjects.getTitle());
                            item.setDate(getitemObjects.getDate());
                            item.setMagnitude(lMagnitude);
                            item.setDepth(D);
                            items.add(item);
                        }
                    }
                } catch (IOException e) { e.printStackTrace(); }

                i++;
            }
            System.out.println(items.size());
            listView(items);

        }
        else if (position == 5){

            items = new ArrayList<>();
            ItemObject getitemObjects;
            boolean England=false;
            int i = 0;
            while ( i < handleXML.listitem.size()){

                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    ListItemObject item = new ListItemObject();
                    getitemObjects = handleXML.itemObjects.get(i);

                    double lat = Double.parseDouble(getitemObjects.getgLat());
                    double lng = Double.parseDouble(getitemObjects.getgLong());

                    List<Address> addresses = gcd.getFromLocation(lat, lng, 1);
                    if (addresses.size() > 0)
                    {
                        String AdminArea=addresses.get(0).getAdminArea();
                        if (AdminArea == null){England = false;}
                        else { England = AdminArea.equals("England");}

                        if (England) {

                            String mString = getitemObjects.getMagnitude();
                            mString = mString.replace("Magnitude: ", "");
                            double lMagnitude = Double.parseDouble(mString);


                            int D = Integer.parseInt(getitemObjects.getDepth());
                            item.setTitle(getitemObjects.getTitle());
                            item.setDate(getitemObjects.getDate());
                            item.setMagnitude(lMagnitude);
                            item.setDepth(D);
                            items.add(item);
                        }
                    }
                } catch (IOException e) { e.printStackTrace(); }

                i++;
            }
            System.out.println(items.size());
            listView(items);

        }
        else if (position == 6){
            //Wales
            items = new ArrayList<>();
            ItemObject getitemObjects;
            boolean Wales=false;
            int i = 0;
            while ( i < handleXML.listitem.size()){

                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    ListItemObject item = new ListItemObject();
                    getitemObjects = handleXML.itemObjects.get(i);

                    double lat = Double.parseDouble(getitemObjects.getgLat());
                    double lng = Double.parseDouble(getitemObjects.getgLong());

                    List<Address> addresses = gcd.getFromLocation(lat, lng, 1);
                    if (addresses.size() > 0)
                    {
                        String AdminArea=addresses.get(0).getAdminArea();
                        if (AdminArea == null){Wales = false;}
                        else { Wales = AdminArea.equals("Wales");}

                        if (Wales) {

                            String mString = getitemObjects.getMagnitude();
                            mString = mString.replace("Magnitude: ", "");
                            double lMagnitude = Double.parseDouble(mString);



                            int D = Integer.parseInt(getitemObjects.getDepth());
                            item.setTitle(getitemObjects.getTitle());
                            item.setDate(getitemObjects.getDate());
                            item.setMagnitude(lMagnitude);
                            item.setDepth(D);
                            items.add(item);
                        }
                    }
                } catch (IOException e) { e.printStackTrace(); }

                i++;
            }
            System.out.println(items.size());
            listView(items);
        }
        else if (position == 7){
            //Other
            items = new ArrayList<>();
            ItemObject getitemObjects;

            int i = 0;
            while ( i < handleXML.listitem.size()){

                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    ListItemObject item = new ListItemObject();
                    getitemObjects = handleXML.itemObjects.get(i);

                    double lat = Double.parseDouble(getitemObjects.getgLat());
                    double lng = Double.parseDouble(getitemObjects.getgLong());

                    List<Address> addresses = gcd.getFromLocation(lat, lng, 1);


                        if (addresses.isEmpty())
                        {
                            String mString = getitemObjects.getMagnitude();
                            mString = mString.replace("Magnitude: ", "");
                            double lMagnitude = Double.parseDouble(mString);
                            int D = Integer.parseInt(getitemObjects.getDepth());
                            item.setTitle(getitemObjects.getTitle());
                            item.setDate(getitemObjects.getDate());
                            item.setMagnitude(lMagnitude);
                            item.setDepth(D);
                            items.add(item);
                        }
                        else {
                            String AdminArea=addresses.get(0).getAdminArea();

                            if (AdminArea == null){
                                String mString = getitemObjects.getMagnitude();
                                mString = mString.replace("Magnitude: ", "");
                                double lMagnitude = Double.parseDouble(mString);
                                int D = Integer.parseInt(getitemObjects.getDepth());
                                item.setTitle(getitemObjects.getTitle());
                                item.setDate(getitemObjects.getDate());
                                item.setMagnitude(lMagnitude);
                                item.setDepth(D);
                                items.add(item);
                            }
                        }

                } catch (IOException e) { e.printStackTrace(); }
                i++;
            }
            System.out.println(items.size());
            listView(items);
        }

    }

    java.util.Comparator<ListItemObject> Alphabetical = new java.util.Comparator<ListItemObject>() {
        @Override
        public int compare(ListItemObject o1, ListItemObject o2) {
            int compareInt = o1.getTitle().compareTo(o2.getTitle());
            if (compareInt < 0) {return -1;}
            if (compareInt > 0) {return 1;}
            return 0; }};

    java.util.Comparator<ListItemObject> Depth = new java.util.Comparator<ListItemObject>() {
        @Override
        public int compare(ListItemObject o1, ListItemObject o2) {
                if (o1.getDepth() > o2.getDepth()){ return 1;}
                else if (o1.getDepth() < o2.getDepth()){ return -1;}
            return 0; }};

    java.util.Comparator<ListItemObject> Magnitude = new java.util.Comparator<ListItemObject>() {
        @Override
        public int compare(ListItemObject o1, ListItemObject o2) {
            if (o1.getMagnitude() > o2.getMagnitude()){ return 1;}
            else if (o1.getMagnitude() < o2.getMagnitude()){ return -1;}
            return 0; }};


}

