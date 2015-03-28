package com.singhdd.dftpclient.resuable;


import java.io.Serializable;
import java.util.Date;

public class FileItem implements Serializable {

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



}
