package cloudtu.util;

import java.util.ResourceBundle;

public class SysConfig
{  
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("sysconfig");
    
    private SysConfig()
    {            
    }
    
    public static int getInt(String key)
    {
        try
        {
            return Integer.parseInt(resourceBundle.getString(key));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static String getString(String key)
    {
        try
        {
            return resourceBundle.getString(key);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }       
}
