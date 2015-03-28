package com.singhdd.dftpclient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.singhdd.dftpclient.adapters.FileListAdapter;
import com.singhdd.dftpclient.resuable.FileItem;
import com.singhdd.dftpclient.resuable.Globals;
import com.singhdd.dftpclient.resuable.Utilities;

import java.io.File;
import java.util.ArrayList;


public class FileSelectActivity extends ActionBarActivity {

    private File mPrivateRootDir;

    private File mDownloadsDir;

    ListView fileListView;

    FileListAdapter mAdapter;

    ArrayList<FileItem> mFiles;

    Uri fileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);
        fileListView = (ListView) findViewById(R.id.file_list);
        mFiles =  new ArrayList<>();

        final Intent mResultIntent = new Intent("com.singhdd.dftpclient.ACTION_RETURN_FILE");

        mPrivateRootDir = getFilesDir();
        mDownloadsDir = new File(mPrivateRootDir,"downloads");
        mFiles.addAll(Utilities.fileList(mDownloadsDir, Globals.SORT_BY_NAME));
        mFiles.remove(0);

        mAdapter = new FileListAdapter(this,R.layout.file_list_item,mFiles);

        fileListView.setAdapter(mAdapter);

        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileItem fileItem = mAdapter.getItem(position);
                File requestFile = new File(fileItem.getPath());
                fileUri = FileProvider.getUriForFile(FileSelectActivity.this, "com.singhdd.dftpclient.fileprovider", requestFile);

                if (fileUri != null) {
                    mResultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    mResultIntent.setDataAndType(fileUri, getContentResolver().getType(fileUri));
                    FileSelectActivity.this.setResult(Activity.RESULT_OK, mResultIntent);
                    finish();
                } else {
                    mResultIntent.setDataAndType(null, "");
                    FileSelectActivity.this.setResult(RESULT_CANCELED,mResultIntent);
                    finish();
                }

            }
        });

        //setResult(Activity.RESULT_CANCELED, null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
