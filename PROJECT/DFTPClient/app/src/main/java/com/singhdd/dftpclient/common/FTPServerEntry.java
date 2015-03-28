package com.singhdd.dftpclient.common;

import android.provider.BaseColumns;

public final class FTPServerEntry implements BaseColumns {

    public static final String TABLE_NAME = "servers";

    public static final String COLUMN_NAME = "sname";

    public static final String COLUMN_HOST = "host";

    public static final String COLUMN_USERNAME = "username";

    public static final String COLUMN_PASSWORD = "password";

    public static final String COLUMN_PORT = "port";

    public static final String COLUMN_PASSIVE = "ispassive";
}
