package com.singhdd.dftpclient.common;

import android.content.Context;
import android.util.Log;

import com.singhdd.dftpclient.resuable.FileItem;
import com.singhdd.dftpclient.resuable.FileSorter;
import com.singhdd.dftpclient.resuable.Globals;
import com.singhdd.dftpclient.resuable.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;


public class FTPHelper{

    private static final String TAG = "FTP Helper";
    public static FTPClient mFTPClient = null;
    private Context mContext;
    private ArrayList<FileItem> allFiles;

    public FTPHelper(Context ctx) {

        this.mContext = ctx;
        allFiles = new ArrayList<>();
    }

    public boolean ftpConnect(String host, String username, String password, int port)
    {
        mFTPClient = new FTPClient();

        try {
            String[] a = mFTPClient.connect(host, port);
            Log.d("LENGTH",""+a.length);
            for(int i = 0; i<a.length; i++) {
                Log.w(TAG, a[i]);
            }
            mFTPClient.login(username, password);
            mFTPClient.setType(FTPClient.TYPE_BINARY);
            mFTPClient.setPassive(true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return false;
        } catch (FTPException e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean ftpDisconnect() {
        try {
          //  mFTPClient.logout();
            mFTPClient.disconnect(true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return false;
        } catch (FTPException e) {
            e.printStackTrace();
            return false;
        }

    }

    public String getFTPCurrentDirectory() {
        try {
            String curDir = mFTPClient.currentDirectory();
            return curDir;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return null;
        } catch (FTPException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean changeFTPDirectory(String path) {
        try {
            mFTPClient.changeDirectory(path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return false;
        } catch (FTPException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changeFTPDirParent() {
        try {
            mFTPClient.changeDirectoryUp();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return false;
        } catch (FTPException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean makeFTPDirectory(String dir_path) {
        try {
            mFTPClient.createDirectory(dir_path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return false;
        } catch (FTPException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeFTPDirectory(String dir_path) {
        try {
            mFTPClient.deleteDirectory(dir_path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return false;
        } catch (FTPException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<FileItem> ftpFileList(int sortType) {
        allFiles.clear();
        FTPFile[] ftpFList = listFTPFiles();
        if (ftpFList != null) {
            for(FTPFile f : ftpFList) {
                if(f.getType() == FTPFile.TYPE_DIRECTORY){
                    allFiles.add(new FileItem(f.getName(),Long.MAX_VALUE, f.getModifiedDate(), f.getName(), Globals.FILE_TYPE_DIRECTORY));
                }
                else {
                    allFiles.add(new FileItem(f.getName(), f.getSize(), f.getModifiedDate(), f.getName(), Utilities.getFileType(f.getName())));
                }
            }
            FileSorter fileSorter = new FileSorter(sortType);

            Collections.sort(allFiles, fileSorter);

            if(!getFTPCurrentDirectory().equalsIgnoreCase("/")) {
                allFiles.add(0, new FileItem("..", 0, null, getParentFTPDirectory(), Globals.FILE_TYPE_PARENT));
            }
            return allFiles;
        }
        else
        {
            return null;
        }
    }

    private FTPFile[] listFTPFiles() {

        try {
            FTPFile[] ftpFiles = mFTPClient.list();
            return ftpFiles;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return null;
        } catch (FTPException e) {
            e.printStackTrace();
            return null;
        } catch (FTPDataTransferException e) {
            e.printStackTrace();
            return null;
        } catch (FTPAbortedException e) {
            e.printStackTrace();
            return null;
        } catch (FTPListParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public long getFTPFileSize(String filepath) {
        try {
            long size = mFTPClient.fileSize(filepath);
            return size;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return 0;
        } catch (FTPException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String getParentFTPDirectory() {
        String currdir = null;
        String updir = null;
        try {
            currdir = mFTPClient.currentDirectory();
            mFTPClient.changeDirectoryUp();
            updir = mFTPClient.currentDirectory();
            mFTPClient.changeDirectory(currdir);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
        } catch (FTPException e) {
            e.printStackTrace();
        }
        return updir;
    }

    public long getDirectoryItemCount(String dir_path) {

        FTPFile[] tList = null;

        try {
            mFTPClient.changeDirectory(dir_path);
            tList = mFTPClient.list();
            mFTPClient.changeDirectoryUp();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
        } catch (FTPException e) {
            e.printStackTrace();
        } catch (FTPAbortedException e) {
            e.printStackTrace();
        } catch (FTPListParseException e) {
            e.printStackTrace();
        } catch (FTPDataTransferException e) {
            e.printStackTrace();
        }
        long count = (long)tList.length;
        return count;
    }

    public boolean deleteFTPFile(String path) {
        try {
            mFTPClient.deleteFile(path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return false;
        } catch (FTPException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean renameFTPFile(String currentPath, String newPath) {
        try {
            mFTPClient.rename(currentPath, newPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return false;
        } catch (FTPException e) {
            e.printStackTrace();
            return false;
        }
    }




}
