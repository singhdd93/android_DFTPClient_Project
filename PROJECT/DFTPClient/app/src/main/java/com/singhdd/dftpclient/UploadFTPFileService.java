package com.singhdd.dftpclient;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.singhdd.dftpclient.common.FTPHelper;
import com.singhdd.dftpclient.resuable.Globals;

import java.io.File;
import java.io.IOException;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;


public class UploadFTPFileService extends IntentService {

    long lengthTransferred = 0;
    long fileLength = 0;
    ResultReceiver receiver;


    public UploadFTPFileService() {
        super("UploadFTPFileService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String originFile =  intent.getStringExtra(Globals.ORIGIN_PATH);
            String destinationPath = intent.getStringExtra(Globals.DEST_PATH);
            receiver = intent.getParcelableExtra(Globals.RESULT_RECEIVER);

            if(!exists(destinationPath)){

                File upFile = new File(originFile);
                fileLength = upFile.length();

                try {
                    FTPHelper.mFTPClient.upload(upFile, new UploadTransferListener());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (FTPIllegalReplyException e) {
                    e.printStackTrace();
                } catch (FTPException e) {
                    e.printStackTrace();
                } catch (FTPDataTransferException e) {
                    e.printStackTrace();
                } catch (FTPAbortedException e) {
                    e.printStackTrace();
                }

            }
            else
            {
                Bundle resultData = new Bundle();
                resultData.putInt(Globals.PROGRESS,-10);
                receiver.send(Globals.RR_CODE_UPLOAD,resultData);
            }

        }
    }

    private boolean exists(String path)
    {
        try {
            FTPHelper.mFTPClient.fileSize(path);
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


    class UploadTransferListener implements FTPDataTransferListener {

        @Override
        public void started() {
            lengthTransferred = 0;
            Bundle resultData = new Bundle();
            resultData.putInt(Globals.PROGRESS, 0);
            receiver.send(Globals.RR_CODE_UPLOAD, resultData);

        }

        @Override
        public void transferred(int i) {

            lengthTransferred = lengthTransferred + i;

            Log.d("TAG", "" + lengthTransferred);

            int percent = (int) ((lengthTransferred*100)/fileLength);

            Log.d("TAG P",""+percent);

            if(percent < 100) {
                Bundle resultData = new Bundle();
                resultData.putInt(Globals.PROGRESS, percent);
                receiver.send(Globals.RR_CODE_UPLOAD, resultData);
            }

        }

        @Override
        public void completed() {

            Bundle resultData = new Bundle();
            resultData.putInt(Globals.PROGRESS, 100);
            receiver.send(Globals.RR_CODE_UPLOAD, resultData);

        }

        @Override
        public void aborted() {

            Bundle resultData = new Bundle();
            resultData.putInt(Globals.PROGRESS, -2);
            receiver.send(Globals.RR_CODE_UPLOAD, resultData);

        }

        @Override
        public void failed() {

            Bundle resultData = new Bundle();
            resultData.putInt(Globals.PROGRESS, -1);
            receiver.send(Globals.RR_CODE_UPLOAD, resultData);

        }
    }


}
