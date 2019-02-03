package com.giltesa.nfcsimple.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;



public class Preferences
{
    final private SharedPreferences        read;
    final private SharedPreferences.Editor write;
    final private String                   NULLABLE_STR_VALUE = "";
    final private String                   NULLABLE_INT_VALUE = "0";

    //Parameters from MEL:
    final private       String KEY_EVENT_ID         = "EVENT_ID";
    final private       String KEY_EVENT_TYPE       = "EVENT_TYPE";
    final private       String KEY_MOVILIZER_CLIENT = "MOVILIZER_CLIENT";
    //
    public final static String LOG_TAG              = "MOVILIZER_HANDLER";



    public Preferences( Context context )
    {
        this.read = PreferenceManager.getDefaultSharedPreferences(context);
        this.write = read.edit();
    }


    /// GETTERS ////////////////////////////////////////////////////////////////////////////////////



    public int getEventID()
    {
        return Integer.parseInt(read.getString(KEY_EVENT_ID, NULLABLE_INT_VALUE));
    }



    /**
     * 0 = Synchronous (default)
     * 1 = Asynchronous Guaranteed
     * 2 = Asynchronous
     *
     * @return
     */
    public int getEventType()
    {
        return Integer.valueOf(read.getString(KEY_EVENT_TYPE, "0"));
    }



    public String getMovilizerClient()
    {
        return read.getString(KEY_MOVILIZER_CLIENT, NULLABLE_STR_VALUE);
    }



    /// SETTERS ////////////////////////////////////////////////////////////////////////////////////



    public void setEventID( String value )
    {
        write.putString(KEY_EVENT_ID, value);
        write.commit();
    }



    public void setEventType( String value )
    {
        write.putString(KEY_EVENT_TYPE, value);
        write.commit();
    }



    public void setMovilizerClient( String value )
    {
        write.putString(KEY_MOVILIZER_CLIENT, value);
        write.commit();
    }


}
