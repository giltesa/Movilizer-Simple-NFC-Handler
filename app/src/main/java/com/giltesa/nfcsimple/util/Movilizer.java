package com.giltesa.nfcsimple.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONObject;

import java.util.Map;



/**
 * Created by Alberto Gil Tesa on 16/03/2018.
 */
public class Movilizer
{
    private Messenger   replyTo   = null;    //Invocation replies are processed by this Messenger
    private Messenger   messenger = null;    //Used to make an RPC invocation
    private Message     message   = null;
    private int         counter   = 0;
    //
    private Context     context   = null;
    private Handler     handler   = null;
    private Preferences pref      = null;


    /**
     *
     */
    public Movilizer( Context context, Handler handler )
    {
        this.context = context;
        this.handler = handler;

        this.pref = new Preferences(context);
    }


    /**
     * Method to fetch the UI data and send the message to Movilizer using Intent
     *
     * @param values
     */
    public void doSendMessage( Map< String, Object > values )
    {
        final String MOVILIZER_INPUT_INTENT = "com.movilizer.client.android.EXT_EVENT";
        final String MOVILIZER_EVENT_ID     = "evtSrcId";
        final String MOVILIZER_JSON_KEY     = "JSON";

        try
        {
            if( values != null && values.size() > 0 )
            {
                replyTo = new Messenger(handler);
                message = Message.obtain();

                Intent serviceIntent = new Intent(MOVILIZER_INPUT_INTENT);
                serviceIntent.setPackage(pref.getMovilizerClient());
                serviceIntent.putExtra(MOVILIZER_EVENT_ID, pref.getEventID());

                //Set the ReplyTo Messenger for processing the invocation response
                final Bundle data = new Bundle();
                data.putString(MOVILIZER_JSON_KEY, new JSONObject(values).toString());

                message.what = pref.getEventID();
                message.arg1 = counter++;
                message.arg2 = pref.getEventType();
                message.replyTo = replyTo;
                message.setData(data);

                context.bindService(serviceIntent, new RemoteServiceConnection(), Context.BIND_AUTO_CREATE);
            }
        }
        catch( Exception ex )
        {
            Log.i(Preferences.LOG_TAG, ex.toString());
        }
    }


    /**
     * Make the invocation
     */
    private class RemoteServiceConnection implements ServiceConnection
    {
        @Override
        public void onServiceConnected( ComponentName component, IBinder binder )
        {
            try
            {
                messenger = new Messenger(binder);
                messenger.send(message);
            }
            catch( RemoteException e )
            {
                Log.e(Preferences.LOG_TAG, "Exception occured:", e);
            }
        }


        @Override
        public void onServiceDisconnected( ComponentName component )
        {
            messenger = null;
        }
    }

}
