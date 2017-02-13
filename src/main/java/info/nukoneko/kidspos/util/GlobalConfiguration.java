package info.nukoneko.kidspos.util;

final public class GlobalConfiguration {
    private static final GlobalConfiguration instance = new GlobalConfiguration();
    private GlobalConfiguration(){
    }
    public static GlobalConfiguration get(){
        return instance;
    }
}
