package com.singhdd.dftpclient.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.singhdd.dftpclient.R;
import com.singhdd.dftpclient.common.FTPServerEntry;

/**
 * Created by damandeep on 28/3/15.
 */
public class ServerAdapter extends CursorAdapter {


    public static class ViewHolder{
        public final TextView serverName;
        public final TextView serverUsernameHost;

        public ViewHolder(View view){
            serverName = (TextView) view.findViewById(R.id.server_name);
            serverUsernameHost = (TextView) view.findViewById(R.id.server_username_host);
        }

    }

    /**
     * Recommended constructor.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     * @param flags   Flags used to determine the behavior of the adapter; may
     *                be any combination of {@link #FLAG_AUTO_REQUERY} and
     *                {@link #FLAG_REGISTER_CONTENT_OBSERVER}.
     */
    public ServerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.server_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String sName = cursor.getString(cursor.getColumnIndex(FTPServerEntry.COLUMN_NAME));
        viewHolder.serverName.setText(sName);

        String sUName = cursor.getString(cursor.getColumnIndex(FTPServerEntry.COLUMN_USERNAME));
        String sHost = cursor.getString(cursor.getColumnIndex(FTPServerEntry.COLUMN_HOST));
        viewHolder.serverUsernameHost.setText(sUName+"@"+sHost);


    }
}
