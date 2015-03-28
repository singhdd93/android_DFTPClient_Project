package com.singhdd.dftpclient;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.singhdd.dftpclient.adapters.FileListAdapter;
import com.singhdd.dftpclient.common.FTPHelper;
import com.singhdd.dftpclient.common.view.CustomProgessDialog;
import com.singhdd.dftpclient.resuable.FileItem;
import com.singhdd.dftpclient.resuable.Globals;
import com.singhdd.dftpclient.resuable.Utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import it.sauronsoftware.ftp4j.FTPFile;

/**
 * A placeholder fragment containing a simple view.
 */
public class RemoteFragment extends Fragment {

    private RelativeLayout loadingLayout;
    private TextView loadingTextView;
    private ListView mRemoteFileListView;
    private TextView mCurrentDirTextView;

    public static boolean FTPConnected = false;
    protected ArrayList<FileItem> filesInDir = null;
    protected FileListAdapter mAdapter = null;
    FTPHelper mFTPHelper;
    String currentDir = null;

    FloatingActionsMenu addMenu;
    FloatingActionButton addFile;
    FloatingActionButton addFolder;

    CustomProgessDialog mProgressDialog;

    //int originalOrientation;


    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    /*public static RemoteFragment newInstance(int sectionNumber) {
        RemoteFragment fragment = new RemoteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }*/

    public RemoteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        filesInDir = new ArrayList<FileItem>();
        currentDir = "";
        mFTPHelper = new FTPHelper(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.remote, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem disconnectItem = menu.findItem(R.id.action_disconnect);
        if(FTPConnected)
        {
            disconnectItem.setVisible(true);
        }
        else
        {
            disconnectItem.setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_disconnect)
        {
            new FTPDiconnect().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_remote, container, false);
        mRemoteFileListView = (ListView) rootView.findViewById(R.id.remote_file_list);
        mCurrentDirTextView = (TextView) rootView.findViewById(R.id.remote_current_dir_path);
        loadingLayout = (RelativeLayout) rootView.findViewById(R.id.loading_layout);
        loadingTextView = (TextView) rootView.findViewById(R.id.loading_text);
        addMenu = (FloatingActionsMenu) rootView.findViewById(R.id.action_adds);
        addFile = (FloatingActionButton) rootView.findViewById(R.id.action_new_file);
        addFolder = (FloatingActionButton) rootView.findViewById(R.id.action_new_folder);

        //originalOrientation = getActivity().getRequestedOrientation();

        addFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder pathAlert = new AlertDialog.Builder(getActivity());
                pathAlert.setTitle("Directory Name");
                pathAlert.setMessage("Enter the Directory Name & Press OK");

                final View pop = getActivity().getLayoutInflater().inflate(R.layout.popup_filedirname, null);

                final EditText nInput = (EditText) pop.findViewById(R.id.popup_name_input);

                pathAlert.setView(pop);

                pathAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String fname = nInput.getText().toString();

                        new FTPNewDir().execute(fname);
                    }
                });

                pathAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                pathAlert.show();
                addMenu.collapse();

            }
        });

        if(!FTPConnected){
            addMenu.setVisibility(View.GONE);
        }

        mCurrentDirTextView.setText(currentDir);
        mAdapter = new FileListAdapter(getActivity(),R.layout.file_list_item,filesInDir);

        mRemoteFileListView.setAdapter(mAdapter);

        mRemoteFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final FileItem fItem = mAdapter.getItem(position);

                if (fItem.getType() == Globals.FILE_TYPE_DIRECTORY) {
                    new FTPChangeDirectory().execute(fItem.getName());
                } else if (fItem.getType() == Globals.FILE_TYPE_PARENT) {
                    new FTPChangeDirParent().execute();
                }
                else
                {
                    PopupMenu remoteFilePopupMenu = new PopupMenu(getActivity(),view);
                    remoteFilePopupMenu.inflate(R.menu.remote_file);
                    
                    remoteFilePopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int itemId = item.getItemId();

                            switch (itemId){
                                case R.id.action_download_curr_dir:
                                    //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                                    mProgressDialog = new CustomProgessDialog(getActivity());
                                    mProgressDialog.setTitle("Download");
                                    mProgressDialog.setMessage(fItem.getName() + " - " + Utilities.getHumanReadableFileSize(fItem.getData(), true));
                                    mProgressDialog.setIndeterminate(true);
                                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    mProgressDialog.setCancelable(false);
                                    mProgressDialog.show();

                                    Intent downloadServiceIntent = new Intent(getActivity(), DownloadFTPFileService.class);
                                    downloadServiceIntent.putExtra(Globals.ORIGIN_PATH,fItem.getName());
                                    downloadServiceIntent.putExtra(Globals.DEST_PATH, LocalFragment.currentDirPath + File.separator + fItem.getName());
                                    downloadServiceIntent.putExtra(Globals.RESULT_RECEIVER,new DownloadReciver(new Handler()));
                                    getActivity().startService(downloadServiceIntent);

                                    break;
                                case R.id.action_download_private_dir:

                                    //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                                    mProgressDialog = new CustomProgessDialog(getActivity());
                                    mProgressDialog.setTitle("Download");
                                    mProgressDialog.setMessage(fItem.getName() + " - " + Utilities.getHumanReadableFileSize(fItem.getData(), true));
                                    mProgressDialog.setIndeterminate(true);
                                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    mProgressDialog.setCancelable(false);
                                    mProgressDialog.show();

                                    File privateRootDir = getActivity().getFilesDir();
                                    File downloadsDir = new File(privateRootDir,"downloads");
                                    downloadsDir.mkdirs();
                                    String dirPath = downloadsDir.getAbsolutePath();

                                    Intent downloadServiceIntent1 = new Intent(getActivity(), DownloadFTPFileService.class);
                                    downloadServiceIntent1.putExtra(Globals.ORIGIN_PATH,fItem.getName());
                                    downloadServiceIntent1.putExtra(Globals.DEST_PATH, dirPath + File.separator + fItem.getName());
                                    downloadServiceIntent1.putExtra(Globals.RESULT_RECEIVER,new DownloadReciver(new Handler()));
                                    getActivity().startService(downloadServiceIntent1);


                                    break;
                                case R.id.action_delete:
                                    new DeleteFTPFile().execute(fItem.getName());

                                    break;
                                case R.id.action_rename:
                                {
                                    AlertDialog.Builder renameAlert = new AlertDialog.Builder(getActivity());
                                    renameAlert.setTitle("Rename");
                                    renameAlert.setMessage("Enter the New Name & Press OK");

                                    final View pop = getActivity().getLayoutInflater().inflate(R.layout.popup_filedirname, null);

                                    final EditText nInput = (EditText) pop.findViewById(R.id.popup_name_input);

                                    renameAlert.setView(pop);

                                    renameAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String fname = nInput.getText().toString();
                                            if (fname.length() > 0) {
                                               new RenameFTPFile().execute(fItem.getName(),fname);
                                            } else {
                                                Toast.makeText(getActivity(), "Rename Failed", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                                    renameAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                                    renameAlert.show();
                                }
                                break;
                            }

                            return true;
                        }
                    });
                    remoteFilePopupMenu.show();
                }

            }
        });

        /*MainActivity mainActivity = (MainActivity) getActivity();

        if(!FTPConnected && !mainActivity.drawerVisible()){
            mainActivity.showDrawer();
        }*/

        return rootView;
    }


    public void reloadFiles(){
        new FTPListFiles().execute();
    }

    public void connectFTP(String host,String username,String password, String port) {
        if(!FTPConnected) {
            new FTPConnect().execute(host, username, password, port);
        }
        else
        {
            boolean b = false;
            try {
                b = new FTPDiconnect().execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if(b)
            {
                new FTPConnect().execute(host, username, password, port);
            }
        }
    }


    class FTPConnect extends AsyncTask<String,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            loadingLayout.setVisibility(View.VISIBLE);
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Boolean doInBackground(String... params) {
            return mFTPHelper.ftpConnect(params[0], params[1], params[2], Integer.parseInt(params[3]));
        }

        @Override
        protected void onPostExecute(Boolean b) {
            loadingLayout.setVisibility(View.GONE);
            if(b) {
                FTPConnected = true;

                Toast.makeText(getActivity(),"Connected",Toast.LENGTH_SHORT).show();
                new FTPGetCurrentDirectory().execute();
                new FTPListFiles().execute();
                addMenu.setVisibility(View.VISIBLE);
            }
            else {
                Toast.makeText(getActivity(),"Failed",Toast.LENGTH_SHORT).show();
            }
            getActivity().invalidateOptionsMenu();
        }
    }

    class FTPDiconnect extends AsyncTask<String,Void,Boolean> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Boolean doInBackground(String... params) {

            return mFTPHelper.ftpDisconnect();
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if(b) {
                FTPConnected = false;
                getActivity().invalidateOptionsMenu();
                filesInDir.clear();
                currentDir = "";
                mCurrentDirTextView.setText(currentDir);
                mAdapter.notifyDataSetChanged();
                addMenu.setVisibility(View.GONE);
                Toast.makeText(getActivity(),"DisConnected",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(),"Failed",Toast.LENGTH_SHORT).show();
            }
        }
    }

    class FTPListFiles extends AsyncTask<String,Void,ArrayList<FileItem>> {

        @Override
        protected void onPreExecute() {
            loadingLayout.setVisibility(View.VISIBLE);
            filesInDir.clear();
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected ArrayList<FileItem> doInBackground(String... params) {
            return mFTPHelper.ftpFileList(Globals.SORT_BY_NAME);
        }

        @Override
        protected void onPostExecute(ArrayList<FileItem> ftpFiles) {
            filesInDir.addAll(ftpFiles);
            mAdapter.notifyDataSetChanged();
            loadingLayout.setVisibility(View.GONE);

            super.onPostExecute(ftpFiles);
        }
    }

    class FTPGetCurrentDirectory extends AsyncTask<String,Void,String> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected String doInBackground(String... params) {
            return mFTPHelper.getFTPCurrentDirectory();
        }

        @Override
        protected void onPostExecute(String s) {
            currentDir = s;
            mCurrentDirTextView.setText(currentDir);
        }
    }

    class FTPChangeDirectory extends AsyncTask<String,Void,Boolean> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Boolean doInBackground(String... params) {
            Log.d("Change",params[0]);
            return mFTPHelper.changeFTPDirectory(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean)
            {
                new FTPGetCurrentDirectory().execute();
                new FTPListFiles().execute();
            }
            else
            {
                Toast.makeText(getActivity(),"Directory Change Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class FTPChangeDirParent extends AsyncTask<String,Void,Boolean>{

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Boolean doInBackground(String... params) {
            return mFTPHelper.changeFTPDirParent();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean)
            {
                new FTPGetCurrentDirectory().execute();
                new FTPListFiles().execute();
            }
            else
            {
                Toast.makeText(getActivity(),"Directory Change Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
    


    class FTPNewDir extends AsyncTask<String,Void,Boolean> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Boolean doInBackground(String... params) {


            return mFTPHelper.makeFTPDirectory(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean)
            {
                new FTPListFiles().execute();
            }
        }
    }

    class DeleteFTPFile extends AsyncTask<String,Void,Boolean> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Boolean doInBackground(String... params) {


            return mFTPHelper.deleteFTPFile(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean)
            {
                new FTPListFiles().execute();
                Toast.makeText(getActivity(),"File Deleted",Toast.LENGTH_SHORT).show();
            }
        }
    }

    class RenameFTPFile extends AsyncTask<String,Void,Boolean> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Boolean doInBackground(String... params) {


            return mFTPHelper.renameFTPFile(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean)
            {
                new FTPListFiles().execute();
                Toast.makeText(getActivity(),"File Renamed",Toast.LENGTH_SHORT).show();
            }
        }
    }

    class DownloadReciver extends ResultReceiver{

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public DownloadReciver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            mProgressDialog.setIndeterminate(false);
            if(resultCode == Globals.RR_CODE_DOWNLOAD){
                int progress = resultData.getInt(Globals.PROGRESS);
                if(progress >= 0)
                {
                    mProgressDialog.setProgress(progress);

                    if(progress == 100){
                        mProgressDialog.dismiss();
                        Toast.makeText(getActivity(),"Download Completed",Toast.LENGTH_SHORT).show();
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.reloadLocalFiles();
                        //getActivity().setRequestedOrientation(originalOrientation);
                    }
                }
                else if(progress == -10)
                {
                    Toast.makeText(getActivity(),"File already exists",Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
                else {
                    Toast.makeText(getActivity(),"Download Failed",Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }
        }
    }


}
