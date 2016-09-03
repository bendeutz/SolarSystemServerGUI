package main;

/**
 * Main
 * <p>
 * Created by @author bendeutz on @created 7/22/16.
 *
 * @version 0.1
 *          Description here!
 */
public class Settings {

    public final static int PORT = 16000;

    //values which can be changed by gui

    public static byte[] IP_STARTS_WITH;
    public static byte[] SUBNET;
    public static int SUBNET_MIN1;
    public static int SUBNET_MAX1;
    public static int SUBNET_MIN2;
    public static int SUBNET_MAX2;
    public static int MAX_CONNECTIONS;
    public static int TIMEOUT;


    public static void setIpStartsWith(byte[] ipStartsWith) {
        IP_STARTS_WITH = ipStartsWith;
    }

    public static void setSUBNET(byte[] SUBNET) {
        Settings.SUBNET = SUBNET;
    }

    public static void setSubnetMin1(int subnetMin1) {
        SUBNET_MIN1 = subnetMin1;
    }

    public static void setSubnetMax1(int subnetMax1) {
        SUBNET_MAX1 = subnetMax1;
    }

    public static void setSubnetMin2(int subnetMin2) {
        SUBNET_MIN2 = subnetMin2;
    }

    public static void setSubnetMax2(int subnetMax2) {
        SUBNET_MAX2 = subnetMax2;
    }

    public static void setMaxConnections(int maxConnections) {
        MAX_CONNECTIONS = maxConnections;
    }

    public static void setTIMEOUT(int TIMEOUT) {
        Settings.TIMEOUT = TIMEOUT;
    }
}
