package com.remoteclient.nikil.remoteclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText ipField;
    private EditText portField;
    private AlertDialog alert;
    private AlertDialog network_alert;
    private SeekBar sensitivity;

    private boolean firstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ipField = (EditText) findViewById(R.id.EditText01);
        portField = (EditText) findViewById(R.id.EditText02);
        sensitivity = (SeekBar) findViewById(R.id.SeekBar01);

        ipField.setText("192.168.1.2");
        portField.setText("5444");

        alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Server Connection Unavailable");
        alert.setMessage("Please make sure the server is running on your computer");
        alert.setButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        network_alert = new AlertDialog.Builder(this).create();
        network_alert.setTitle("Network Unreachable");
        network_alert.setMessage("Your device is not connected to a network.");
        network_alert.setButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onResume(){

        super.onResume();
        AppDelegate appDel = (AppDelegate) getApplication();


        if(!appDel.connected && !firstRun){
            alert.show();
        }

        appDel.stopServer();
    }
    @Override
    public void onPause(){
        super.onPause();
        firstRun = false;
    }


    public void clickHandler(View view) {
        AppDelegate appDel = ((AppDelegate)getApplicationContext());
        int s = sensitivity.getProgress();
        appDel.mouse_sensitivity = Math.round(s/20) + 1;

        if(!appDel.connected){
            String serverIp;
            int serverPort;

            serverIp = ipField.getText().toString();
            serverPort = Integer.parseInt(portField.getText().toString());

            appDel.createClientThread(serverIp, serverPort);
        }

        int x;
        for(x=0;x<4;x++){// every quarter second for one second check if the server is reachable
            if(appDel.connected){
                startActivity(new Intent(view.getContext(), Controller.class));
                x = 6;
            }
            try{Thread.sleep(250);}
            catch(Exception e){}
        }

        if(!appDel.connected)
            if(!appDel.network_reachable)
                network_alert.show();
            else
                alert.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_rate) {

            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
            // Handle the camera action
        }else if (id == R.id.nav_more){

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=5599685400679741226")));
        }
        else {
            Intent aboutIntent = new Intent(MainActivity.this, About.class);
            startActivity(aboutIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
