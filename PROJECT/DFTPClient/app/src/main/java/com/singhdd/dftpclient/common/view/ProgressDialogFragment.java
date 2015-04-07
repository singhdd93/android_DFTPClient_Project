package com.singhdd.dftpclient.common.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by damandeep on 8/4/15.
 */
public class ProgressDialogFragment extends DialogFragment {

    private String dialogTag;
    /**
     * Builder for progress dialog fragment
     **/
    public static class Builder
    {
        private String title;
        private String message;
        private boolean cancelableOnTouchOutside = false;
        public ProgressDialogFragment.Builder setTitle(String title)
        {
            this.title = title;
            return this;
        }
        public ProgressDialogFragment.Builder setMessage(String message)
        {
            this.message = message;
            return this;
        }
        public ProgressDialogFragment.Builder setCancelableOnTouchOutside(boolean cancelable)
        {
            this.cancelableOnTouchOutside = cancelable;
            return this;
        }
        public ProgressDialogFragment build()
        {
            return ProgressDialogFragment.newInstance(title, message, cancelableOnTouchOutside);
        }
    }
    protected static ProgressDialogFragment newInstance()
    {
        return newInstance("", "", true);
    }
    protected static ProgressDialogFragment newInstance(String title, String message, boolean cancelableOnTouchOutside)
    {
        ProgressDialogFragment frag = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putBoolean("cancelableOnTouchOutside", cancelableOnTouchOutside);
        frag.setArguments(args);
        return frag;
    }
    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Override public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        dialog.setCanceledOnTouchOutside(getArguments().getBoolean("cancelableOnTouchOutside"));

            dialog.setTitle(title);


            dialog.setMessage(message);

        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMax(100);
        dialog.setCancelable(false);
        return dialog;
    }
    /**
     * Sets the progress of the dialog, we need to make sure we get the right dialog reference here
     * which is why we obtain the dialog fragment manually from the fragment manager
     * @param manager
     * @param progress
     */
    public void setProgress(FragmentManager manager, int progress)
    {
        ProgressDialogFragment dialog = (ProgressDialogFragment)manager.findFragmentByTag(dialogTag);
        if (dialog != null)
        {
            ((ProgressDialog)dialog.getDialog()).setProgress(progress);
        }
    }
    /**
     * Dismisses the dialog from the fragment manager. We need to make sure we get the right dialog reference
     * here which is why we obtain the dialog fragment manually from the fragment manager
     * @param manager
     */
    public void dismiss(FragmentManager manager)
    {
        ProgressDialogFragment dialog = (ProgressDialogFragment)manager.findFragmentByTag(dialogTag);
        if (dialog != null)
        {
            if(dialog.getDialog().isShowing()) {
                dialog.dismiss();
            }
        }
    }

    public void setIndeterminate(FragmentManager manager, boolean indeterminate)
    {
        ProgressDialogFragment dialog = (ProgressDialogFragment)manager.findFragmentByTag(dialogTag);
        if (dialog != null)
        {
            ((ProgressDialog)dialog.getDialog()).setIndeterminate(indeterminate);
        }
    }

    @Override
    public void show(FragmentManager manager, String tag)
    {
        dialogTag = tag;
        super.show(manager, dialogTag);
    }

}
