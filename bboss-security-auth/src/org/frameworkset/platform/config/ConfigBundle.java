package org.frameworkset.platform.config;

import java.util.HashMap;
import java.util.ResourceBundle;

public class ConfigBundle implements java.io.Serializable
{
    private static HashMap bundles = new HashMap();

    public static ResourceBundle getResourceBundle(String bundleName)
    {
      ResourceBundle rb = (ResourceBundle)bundles.get(bundleName);
      if(rb == null)
      {
            rb = ResourceBundle.getBundle(bundleName);
            bundles.put(bundleName,rb);
      }
      return rb;
    }
}
