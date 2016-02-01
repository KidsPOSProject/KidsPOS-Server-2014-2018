package info.nukoneko.kidspos.util;

import info.nukoneko.kidspos.util.config.TableName;

/**
 * Created by TEJNEK on 2016/01/31.
 */
final public class GlobalConfiguration {
    private static GlobalConfiguration instance = new GlobalConfiguration();
    private GlobalConfiguration(){
    }
    public static GlobalConfiguration get(){
        return instance;
    }
}
