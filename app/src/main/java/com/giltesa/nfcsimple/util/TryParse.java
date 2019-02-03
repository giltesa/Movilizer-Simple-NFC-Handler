package com.giltesa.nfcsimple.util;


import org.json.JSONObject;



public class TryParse
{
    private TryParse()
    {
    }



    /**
     * @param json
     * @param key
     * @param defaultValue
     *
     * @return
     */
    public static String tryParseString( JSONObject json, String key, String defaultValue )
    {
        try
        {
            String value = json.getString(key);
            String tmp   = null;

            if( value != null )
            {
                tmp = String.valueOf(value);
            }

            return (tmp != null && !tmp.isEmpty()) ? tmp : defaultValue;
        }
        catch( Exception e )
        {
            e.printStackTrace();
            return defaultValue;
        }
    }


}
