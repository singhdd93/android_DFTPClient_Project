package com.singhdd.dftpclient.resuable;

import com.singhdd.dftpclient.R;

/**
 * Created by damandeep on 6/3/15.
 */
public class Globals {
    /*
    * Static Variables for Sorting
    */
    public static int SORT_BY_NAME = 0;
    public static int SORT_BY_DATE = 1;
    public static int SORT_BY_SIZE = 2;
    public static int SORT_BY_TYPE = 3;

    /*
    * Static Variables for File Type
    */
    public static int FILE_TYPE_DIRECTORY = 0;
    public static int FILE_TYPE_APPLICATION = 1;
    public static int FILE_TYPE_AUDIO = 2;
    public static int FILE_TYPE_IMAGE = 3;
    public static int FILE_TYPE_TEXT = 4;
    public static int FILE_TYPE_VIDEO = 5;
    public static int FILE_TYPE_OTHERS = 6;
    public static int FILE_TYPE_PARENT = 7;

    /*
    * Static Refernce to File Type Drawables
    */
    public static int ICON_DIRECTORY = R.drawable.ic_circle_dir;
    public static int ICON_APPLICATION = R.drawable.ic_circle_application;
    public static int ICON_AUDIO = R.drawable.ic_circle_audio;
    public static int ICON_IMAGE = R.drawable.ic_circle_image;
    public static int ICON_TEXT = R.drawable.ic_circle_text;
    public static int ICON_VIDEO = R.drawable.ic_circle_video;
    public static int ICON_OTHERS = R.drawable.ic_circle_others;
    public static int ICON_PARENT = R.drawable.ic_circle_parent_dir;


    /*
    * Server Activity Intent Actions
    */
    public static String ACTION_NEW_SERVER = "newserver";
    public static String ACTION_EDIT_SERVER = "editserver";


    /*
    * Server Activity Intent Data
    */
    public static String SERVER_NAME = "name";
    public static String SERVER_HOST = "host";
    public static String SERVER_USERNAME = "uname";
    public static String SERVER_PASSWORD = "pass";
    public static String SERVER_PORT = "port";
    public static String SERVER_ID = "id";


    /*
    * Passing Data to Intent Service
    */

    public static String FTPCLIENT = "mftpcl";
    public static String ORIGIN_PATH = "originpath";
    public static String DEST_PATH = "destpath";
    public static String RESULT_RECEIVER = "rr";


    /*
    * Result Receiver
    */

    public static final int RR_CODE_DOWNLOAD = 8456;
    public static final int RR_CODE_UPLOAD = 8457;
    public static final String PROGRESS = "progress";



}
