package com.singhdd.dftpclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.singhdd.dftpclient.R;
import com.singhdd.dftpclient.resuable.FileItem;
import com.singhdd.dftpclient.resuable.Globals;
import com.singhdd.dftpclient.resuable.Utilities;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


public class FileListAdapter extends BaseAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<FileItem> files;


    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use when
     *                           instantiating views.
     * @param objects            The objects to represent in the ListView.
     */
    public FileListAdapter(Context context, int resource, ArrayList<FileItem> objects) {
        this.context = context;
        this.layoutResourceId = resource;
        this.files = objects;
    }

    /*public FileItem getFileItem(int postion) {
        return files.get(postion);
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(layoutResourceId, null);
        }

        final FileItem fItem = files.get(position);
        if(fItem != null) {
            TextView fNameTextView = (TextView) view.findViewById(R.id.file_list_item_name);
            TextView fDataTextView = (TextView) view.findViewById(R.id.file_list_item_data);
            TextView fDateTextView = (TextView) view.findViewById(R.id.file_list_item_date);
            ImageView fIconImageView = (ImageView) view.findViewById(R.id.file_list_item_icon);

            fNameTextView.setText(fItem.getName());
            if(fItem.getDate() != null) {
                Date lastModifiedDate = fItem.getDate();
                DateFormat formatter = DateFormat.getDateTimeInstance();
                fDateTextView.setText(formatter.format(lastModifiedDate));
            }
            else {
                fDateTextView.setText("");
            }

            int fileType = fItem.getType();
            long data = fItem.getData();

            if(fileType == Globals.FILE_TYPE_DIRECTORY) {

                if(data <= 1){
                    fDataTextView.setText(data+" Item");
                }
                else if(data == Long.MAX_VALUE) {
                    fDataTextView.setText("Folder");
                }
                else {
                    fDataTextView.setText(data+" Items");
                }
                fIconImageView.setImageResource(Globals.ICON_DIRECTORY);
            }
            else if (fileType == Globals.FILE_TYPE_APPLICATION) {
                fDataTextView.setText(Utilities.getHumanReadableFileSize(data,true));
                fIconImageView.setImageResource(Globals.ICON_APPLICATION);
            }
            else if (fileType == Globals.FILE_TYPE_AUDIO) {
                fDataTextView.setText(Utilities.getHumanReadableFileSize(data,true));
                fIconImageView.setImageResource(Globals.ICON_AUDIO);
            }
            else if (fileType == Globals.FILE_TYPE_IMAGE) {
                fDataTextView.setText(Utilities.getHumanReadableFileSize(data,true));
                fIconImageView.setImageResource(Globals.ICON_IMAGE);
            }
            else if (fileType == Globals.FILE_TYPE_TEXT) {
                fDataTextView.setText(Utilities.getHumanReadableFileSize(data,true));
                fIconImageView.setImageResource(Globals.ICON_TEXT);
            }
            else if (fileType == Globals.FILE_TYPE_VIDEO) {
                fDataTextView.setText(Utilities.getHumanReadableFileSize(data,true));
                fIconImageView.setImageResource(Globals.ICON_VIDEO);
            }
            else if (fileType == Globals.FILE_TYPE_PARENT) {
                fDataTextView.setText("Parent Directory");
                fIconImageView.setImageResource(Globals.ICON_PARENT);
            }
            else {
                fDataTextView.setText(Utilities.getHumanReadableFileSize(data,true));
                fIconImageView.setImageResource(Globals.ICON_OTHERS);
            }
        }

        return view;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public FileItem getItem(int position) {
        return files.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }
}
