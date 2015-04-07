package com.singhdd.dftpclient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.singhdd.dftpclient.adapters.FileListAdapter;
import com.singhdd.dftpclient.common.view.ProgressDialogFragment;
import com.singhdd.dftpclient.resuable.FileItem;
import com.singhdd.dftpclient.resuable.Globals;
import com.singhdd.dftpclient.resuable.Utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class LocalFragment extends Fragment{

    private ListView mLocalFileListView;
    private TextView mCurrentDirTextView;
    protected FileListAdapter mAdapter = null;
    protected ArrayList<FileItem> filesInDir = null;
    public static String currentDirPath = null;
    private int sortType ;

    private String STATE_FILESINDIR = "filesindir";
    private String STATE_CURRENTDIR = "currentdir";
    private String STATE_SORTTYPE = "sorttype";


    FloatingActionsMenu addMenu;
    FloatingActionButton addFile;
    FloatingActionButton addFolder;

    private ProgressDialogFragment mProgressDialogFragment;

    //int originalOrientation;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(STATE_FILESINDIR, filesInDir);
        outState.putString(STATE_CURRENTDIR,currentDirPath);
        outState.putInt(STATE_SORTTYPE, sortType);

    }


    public LocalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_local, container, false);
        mLocalFileListView = (ListView) rootView.findViewById(R.id.local_file_list);
        mCurrentDirTextView = (TextView) rootView.findViewById(R.id.local_current_dir_path);
        addMenu = (FloatingActionsMenu) rootView.findViewById(R.id.action_adds);
        addFile = (FloatingActionButton) rootView.findViewById(R.id.action_new_file);
        addFolder = (FloatingActionButton) rootView.findViewById(R.id.action_new_folder);


        addFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder pathAlert = new AlertDialog.Builder(getActivity());
                pathAlert.setTitle("File Name");
                pathAlert.setMessage("Enter the File Name & Press OK");

                final View pop = getActivity().getLayoutInflater().inflate(R.layout.popup_filedirname, null);

                final EditText nInput = (EditText) pop.findViewById(R.id.popup_name_input);

                pathAlert.setView(pop);

                pathAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String fname = nInput.getText().toString();

                        File f = new File(currentDirPath+File.separator+fname);
                        boolean created = false;
                        if(!f.exists())
                        {
                            try {
                                created = f.createNewFile();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if(created)
                            {
                                reloadFiles();
                            }
                            else {
                                Toast.makeText(getActivity(),"Error Creating File",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(getActivity(),"File Already exits",Toast.LENGTH_SHORT).show();
                        }
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

        addFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder pathAlert = new AlertDialog.Builder(getActivity());
                pathAlert.setTitle("Folder Name");
                pathAlert.setMessage("Enter the Folder Name & Press OK");

                final View pop = getActivity().getLayoutInflater().inflate(R.layout.popup_filedirname, null);

                final EditText nInput = (EditText) pop.findViewById(R.id.popup_name_input);

                pathAlert.setView(pop);

                pathAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String fname = nInput.getText().toString();

                        File f = new File(currentDirPath+File.separator+fname+File.separator);
                        boolean created = false;
                        if(!f.isDirectory() && (!f.exists()))
                        {
                                created = f.mkdir();

                            if(created)
                            {
                                reloadFiles();
                            }
                            else {
                                Toast.makeText(getActivity(),"Error Creating Folder",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(getActivity(),"Folder Already exits",Toast.LENGTH_SHORT).show();
                        }
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

        filesInDir = new ArrayList<FileItem>();

        if(savedInstanceState == null){
            sortType = Globals.SORT_BY_NAME;
            File sdCardDir = Environment.getExternalStorageDirectory();
            currentDirPath = sdCardDir.getAbsolutePath();
            filesInDir.addAll(Utilities.fileList(sdCardDir,sortType));//Utilities.fileList(sdCardDir, sortType);
        }
        else {
            sortType = savedInstanceState.getInt(STATE_SORTTYPE);
            currentDirPath = savedInstanceState.getString(STATE_CURRENTDIR);
            filesInDir = savedInstanceState.getParcelableArrayList(STATE_FILESINDIR);

        }

        mCurrentDirTextView.setText(currentDirPath);

        mAdapter = new FileListAdapter(getActivity(),R.layout.file_list_item,filesInDir);

        mLocalFileListView.setAdapter(mAdapter);

        mLocalFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final FileItem fileItem = mAdapter.getItem(position);

                if (fileItem.getType() == Globals.FILE_TYPE_DIRECTORY || fileItem.getType() == Globals.FILE_TYPE_PARENT) {
                    // filesInDir = Utilities.fileList(new File(fileItem.getPath()),sortType);
                    filesInDir.clear();
                    currentDirPath = fileItem.getPath();
                    mCurrentDirTextView.setText(currentDirPath);
                    filesInDir.addAll(Utilities.fileList(new File(currentDirPath), sortType));
                    mAdapter.notifyDataSetChanged();
                } else {
                    PopupMenu localFilePopupMenu = new PopupMenu(getActivity(),view);
                    localFilePopupMenu.inflate(R.menu.local_file);
                    Menu localFileMenu = localFilePopupMenu.getMenu();
                    if(RemoteFragment.FTPConnected)
                    {
                        localFileMenu.findItem(R.id.action_upload).setEnabled(true);
                    }
                    else
                    {
                        localFileMenu.findItem(R.id.action_upload).setEnabled(false);
                    }
                    localFilePopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int itemId = item.getItemId();

                            switch (itemId){
                                case R.id.action_open:
                                    try {
                                        Utilities.openLocalFile(getActivity(),fileItem.getPath());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case R.id.action_upload:
                                    mProgressDialogFragment = new ProgressDialogFragment.Builder()
                                            .setTitle("Upload")
                                            .setMessage(fileItem.getName() + " - " + Utilities.getHumanReadableFileSize(fileItem.getData(), true))
                                            .build();
                                    mProgressDialogFragment.show(getActivity().getSupportFragmentManager(),"task_progress");
                                    mProgressDialogFragment.setIndeterminate(getActivity().getSupportFragmentManager(),true);

                                    Intent uploadServiceIntent = new Intent(getActivity(),UploadFTPFileService.class);
                                    uploadServiceIntent.putExtra(Globals.ORIGIN_PATH, fileItem.getPath());
                                    uploadServiceIntent.putExtra(Globals.DEST_PATH, fileItem.getName());
                                    uploadServiceIntent.putExtra(Globals.RESULT_RECEIVER, new UploadReceiver(new Handler()));
                                    getActivity().startService(uploadServiceIntent);
                                    break;
                                case R.id.action_delete:
                                    File tempFile = new File(fileItem.getPath());
                                    tempFile.delete();
                                    reloadFiles();
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
                                                File tempFile = new File(fileItem.getPath());
                                                File dir = new File(tempFile.getParent());
                                                tempFile.renameTo(new File(dir, fname));
                                                reloadFiles();
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
                    localFilePopupMenu.show();
                }
            }
        });

        return rootView;
    }

    public void reloadFiles(){
        filesInDir.clear();
        filesInDir.addAll(Utilities.fileList(new File(currentDirPath),sortType));
        mAdapter.notifyDataSetChanged();
    }


    class UploadReceiver extends ResultReceiver{

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public UploadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            mProgressDialogFragment.setIndeterminate(getActivity().getSupportFragmentManager(), false);
            if(resultCode == Globals.RR_CODE_UPLOAD){
                int progress = resultData.getInt(Globals.PROGRESS);
                if(progress >= 0)
                {
                    mProgressDialogFragment.setProgress(getActivity().getSupportFragmentManager(),progress);

                    if(progress == 100){
                        mProgressDialogFragment.dismiss(getActivity().getSupportFragmentManager());
                        Toast.makeText(getActivity(),"Upload Completed",Toast.LENGTH_SHORT).show();
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.reloadFTPFiles();
                       // getActivity().setRequestedOrientation(originalOrientation);
                    }
                }
                else if(progress == -10)
                {
                    Toast.makeText(getActivity(),"File already exists",Toast.LENGTH_SHORT).show();
                    mProgressDialogFragment.dismiss(getActivity().getSupportFragmentManager());
                }
                else {
                    Toast.makeText(getActivity(),"Upload Failed",Toast.LENGTH_SHORT).show();
                    mProgressDialogFragment.dismiss(getActivity().getSupportFragmentManager());
                }
            }
        }
    }


}
