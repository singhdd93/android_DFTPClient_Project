package com.singhdd.dftpclient.resuable;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

public class FileItem implements Parcelable {

    private String name;
    private long data;
    private Date date;
    private String path;
    private int type;


    public FileItem(String name, long data, Date date, String path, int type) {
        this.name = name;
        this.data = data;
        this.date = date;
        this.path = path;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }
    public long getData()
    {
        return data;
    }
    public Date getDate()
    {
        return date;
    }
    public String getPath()
    {
        return path;
    }
    public int getType() {
        return type;
    }




    protected FileItem(Parcel in) {
        name = in.readString();
        data = in.readLong();
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
        path = in.readString();
        type = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(data);
        dest.writeLong(date != null ? date.getTime() : -1L);
        dest.writeString(path);
        dest.writeInt(type);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FileItem> CREATOR = new Parcelable.Creator<FileItem>() {
        @Override
        public FileItem createFromParcel(Parcel in) {
            return new FileItem(in);
        }

        @Override
        public FileItem[] newArray(int size) {
            return new FileItem[size];
        }
    };
}
