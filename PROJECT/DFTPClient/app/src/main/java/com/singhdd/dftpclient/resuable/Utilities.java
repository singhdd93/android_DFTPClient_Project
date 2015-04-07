package com.singhdd.dftpclient.resuable;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Utilities {

    /*
    * Function for Opening Local Files in Respective Applications
    */
    public static void openLocalFile(Context context, String path) throws IOException {
        File file=new File(path);
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String ext = MimeTypeMap.getFileExtensionFromUrl(path.toLowerCase());
        // Log.v("MIME",ext);
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        // Toast.makeText(FileChooser.this,"Mime : "+mime, Toast.LENGTH_SHORT).show();
        intent.setDataAndType(uri,mime);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "No Application to Handle this file type", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * Function to get Human Readable File Size in MB or MiB formats
    */
    public static String getHumanReadableFileSize(long bytes, boolean hr) {
        int unit = hr ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (hr ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (hr ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }


    /*
    * Function to get List of Directories & Files in Specified Directory
    */
    public static ArrayList<FileItem> fileList(File dirPath, int sortType)
    {
        File[] directories = dirPath.listFiles();
        ArrayList<FileItem> allFiles = new ArrayList<>();
        try{
            for(File ff : directories)
            {
                if(ff.isDirectory()){
                    File[] tDir = ff.listFiles();
                    long numOfFiles = 0;
                    if(tDir != null) {
                        numOfFiles = tDir.length;
                    }

                    allFiles.add(new FileItem(ff.getName(),numOfFiles,new Date(ff.lastModified()),ff.getAbsolutePath(),Globals.FILE_TYPE_DIRECTORY));
                }
                else
                {

                    allFiles.add(new FileItem(ff.getName(),ff.length(),new Date(ff.lastModified()),ff.getAbsolutePath(),getFileType(ff.getAbsolutePath())));
                }
            }
        }catch(Exception e)
        {
           // Log.e(Utilities.class.getSimpleName(),e.getMessage());
            e.printStackTrace();
        }
        FileSorter fileSorter = new FileSorter(sortType);

        Collections.sort(allFiles,fileSorter);
        if(!dirPath.getName().equalsIgnoreCase("")) {
            allFiles.add(0, new FileItem("..", 0, null, dirPath.getParent(), Globals.FILE_TYPE_PARENT));
        }
        return allFiles;
    }


    public static int getFileType(String path) {
        String ext = MimeTypeMap.getFileExtensionFromUrl(path.toLowerCase());
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        if(mime != null) {
            int index = mime.indexOf("/");
            String type = mime.substring(0, index);
            if (type.equals("application")) {
                return Globals.FILE_TYPE_APPLICATION;
            } else if (type.equals("audio")) {
                return Globals.FILE_TYPE_AUDIO;
            } else if (type.equals("image")) {
                return Globals.FILE_TYPE_IMAGE;
            } else if (type.equals("text")) {
                return Globals.FILE_TYPE_TEXT;
            } else if (type.equals("video")) {
                return Globals.FILE_TYPE_VIDEO;
            } else {
                return Globals.FILE_TYPE_OTHERS;
            }
        }
        else
        {
            return Globals.FILE_TYPE_OTHERS;
        }
    }



}
