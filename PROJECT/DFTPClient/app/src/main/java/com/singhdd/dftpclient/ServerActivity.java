package com.singhdd.dftpclient;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.singhdd.dftpclient.common.FTPServerEntry;
import com.singhdd.dftpclient.common.ServerDbHelper;
import com.singhdd.dftpclient.resuable.Globals;


public class ServerActivity extends ActionBarActivity {

    private static String ACTION = Globals.ACTION_NEW_SERVER;
    private String sId,sName, sHost, sUsername, sPassword, sPort;
    private EditText nameEditText, hostEditText, usernameEditText, passwordEditText, portEditText;
    private boolean bName = false, bHost = false, bUsername = false, bPassword = false, bPort = true;
    private SQLiteDatabase db;
    private ServerDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        nameEditText = (EditText) findViewById(R.id.edittext_name_server);
        hostEditText = (EditText) findViewById(R.id.edittext_host_server);
        usernameEditText = (EditText) findViewById(R.id.edittext_username_server);
        passwordEditText = (EditText) findViewById(R.id.edittext_password_server);
        portEditText = (EditText) findViewById(R.id.edittext_port_server);

        final Drawable originalEditTextBg = nameEditText.getBackground();

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().length() < 1){
                  //  nameEditText.setBackgroundColor(getResources().getColor(R.color.red_dim));
                    nameEditText.getBackground().setColorFilter(getResources().getColor(R.color.red_dim),PorterDuff.Mode.SRC_ATOP);
                    bName = false;
                }
                else {
                    nameEditText.getBackground().setColorFilter(getResources().getColor(R.color.green_dim), PorterDuff.Mode.SRC_ATOP);
                    bName = true;
                }

            }
        });

        hostEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().matches("^[a-zA-Z0-9\\-\\.]+\\.([a-z]*)$") || s.toString().matches("^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$")) {
                    hostEditText.getBackground().setColorFilter(getResources().getColor(R.color.green_dim), PorterDuff.Mode.SRC_ATOP);
                    bHost = true;
                }
                else {
                    hostEditText.getBackground().setColorFilter(getResources().getColor(R.color.red_dim), PorterDuff.Mode.SRC_ATOP);
                    bHost = false;
                }

            }
        });

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().length() < 1){
                    usernameEditText.getBackground().setColorFilter(getResources().getColor(R.color.red_dim), PorterDuff.Mode.SRC_ATOP);
                    bUsername = false;
                }
                else {
                    usernameEditText.getBackground().setColorFilter(getResources().getColor(R.color.green_dim), PorterDuff.Mode.SRC_ATOP);
                    bUsername = true;
                }

            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().trim().length() > 0){
                    bPassword = true;
                }
                else
                {
                    bPassword = false;
                }

            }
        });

        portEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().matches("^(6553[0-5]|655[0-2]\\d|65[0-4]\\d\\d|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)$")) {
                    portEditText.getBackground().setColorFilter(getResources().getColor(R.color.green_dim), PorterDuff.Mode.SRC_ATOP);
                    bPort = true;
                }
                else {
                    portEditText.getBackground().setColorFilter(getResources().getColor(R.color.red_dim), PorterDuff.Mode.SRC_ATOP);
                    bPort = false;
                }


            }
        });




        Intent receivedIntent = getIntent();

        ACTION = receivedIntent.getAction();

        if(ACTION == Globals.ACTION_EDIT_SERVER)
        {
            sName = receivedIntent.getStringExtra(Globals.SERVER_NAME);
            sHost = receivedIntent.getStringExtra(Globals.SERVER_HOST);
            sUsername = receivedIntent.getStringExtra(Globals.SERVER_USERNAME);
            sPassword = receivedIntent.getStringExtra(Globals.SERVER_PASSWORD);
            sPort = receivedIntent.getStringExtra(Globals.SERVER_PORT);
            sId = receivedIntent.getStringExtra(Globals.SERVER_ID);
        }

        dbHelper = new ServerDbHelper(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_server, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_server) {

            if(bName && bHost && bUsername && bPort){

                if(!bPassword){
                    Toast.makeText(this,"You will be prompted for Password", Toast.LENGTH_SHORT).show();
                }
                else{
                    sPassword = passwordEditText.getText().toString();
                }

                sName = nameEditText.getText().toString();
                sHost = hostEditText.getText().toString();
                sUsername = usernameEditText.getText().toString();
                sPort = portEditText.getText().toString();


                ContentValues cv = new ContentValues();
                cv.put(FTPServerEntry.COLUMN_NAME,sName);
                cv.put(FTPServerEntry.COLUMN_HOST,sHost);
                cv.put(FTPServerEntry.COLUMN_USERNAME,sUsername);
                cv.put(FTPServerEntry.COLUMN_PORT,Integer.parseInt(sPort));
                cv.put(FTPServerEntry.COLUMN_PASSIVE,true);
                if(bPassword){
                    cv.put(FTPServerEntry.COLUMN_PASSWORD,sPassword);
                }
                db = dbHelper.getWritableDatabase();
                long insId = db.insert(FTPServerEntry.TABLE_NAME, null, cv);
                db.close();
                if(insId > -1){
                    Toast.makeText(this,"Server Added",Toast.LENGTH_SHORT).show();
                    getContentResolver().notifyChange(Uri.parse("content://ftpservers"), null);
                    finish();
                }
                else
                {
                    Toast.makeText(this,"Database Error",Toast.LENGTH_SHORT).show();
                }
            }
            else {
                String msg = "Check ";
                if(!bName){
                    msg = msg+"Username ";
                }
                if(!bHost){
                    msg = msg +"Host ";
                }
                if(!bUsername){
                    msg = msg +"Username ";
                }
                if(!bPort){
                    msg = msg +"Port ";
                }
                Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
