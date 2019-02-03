package com.giltesa.nfcsimple.activity;


import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.giltesa.nfcsimple.R;
import com.giltesa.nfcsimple.util.Movilizer;
import com.giltesa.nfcsimple.util.Preferences;
import com.giltesa.nfcsimple.util.TryParse;

import org.json.JSONObject;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;



public class MainActivity extends AppCompatActivity
{
    private Movilizer     movilizer;
    private NfcAdapter    nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter  writeTagFilters[];



    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent moviIntent = getIntent();

        if( moviIntent != null && Intent.ACTION_VIEW.equals(moviIntent.getAction()) )
        {
            Preferences pref = new Preferences(this);

            // Get input parameters from Movilizer for configure the handler:
            String  eventID    = null;
            String  eventType  = null;
            boolean runHandler = true;

            try
            {
                JSONObject json = new JSONObject(moviIntent.getDataString());
                eventID = TryParse.tryParseString(json, "EVENT_ID", "");
                eventType = TryParse.tryParseString(json, "EVENT_TYPE", "0");
            }
            catch( Exception e )
            {
                runHandler = false;
                Log.e(Preferences.LOG_TAG, getString(R.string.toast_fatal_error), e);
                Toast.makeText(this, getString(R.string.toast_fatal_error), Toast.LENGTH_SHORT).show();
            }


            if( runHandler )
            {
                //Check MEL mandatory parameters:
                if( eventID == null || eventID.isEmpty() )
                {
                    Log.e(Preferences.LOG_TAG, getString(R.string.toast_event_id_field_missing));
                    Toast.makeText(this, getString(R.string.toast_event_id_field_missing), Toast.LENGTH_SHORT).show();
                    finish();
                }
                //OK:
                else
                {
                    pref.setEventID(eventID);
                    pref.setEventType(eventType);
                    pref.setMovilizerClient(getReferrer().getHost());

                    movilizer = new Movilizer(this, new Handler());
                    nfcAdapter = NfcAdapter.getDefaultAdapter(this);

                    if( nfcAdapter == null )
                    {
                        Toast.makeText(this, getString(R.string.toast_nfc_not_available), Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                    IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                    tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
                    writeTagFilters = new IntentFilter[]{tagDetected};
                }
            }
        }
    }



    /**
     * @param intent
     */
    @SuppressLint("NewApi")
    protected void onNewIntent( Intent intent )
    {
        if( NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) )
        {
            Tag    myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String id    = new BigInteger(1, myTag.getId()).toString();


            Map<String, Object> values = new HashMap<>();
            values.put("ID", id);
            movilizer.doSendMessage(values);

            finish();
        }
    }



    /**
     *
     */
    public void onPause()
    {
        super.onPause();

        if( nfcAdapter != null )
        {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }



    /**
     *
     */
    public void onResume()
    {
        super.onResume();

        if( nfcAdapter != null )
        {
            if( !nfcAdapter.isEnabled() )
            {
                Toast.makeText(this, getString(R.string.toast_nfc_disabled), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));

                finish();
                return;
            }

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
        }
    }
}
