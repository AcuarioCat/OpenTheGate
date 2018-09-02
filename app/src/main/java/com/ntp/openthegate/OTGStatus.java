package com.ntp.openthegate;

import android.content.SharedPreferences;
import android.os.Handler;

import helpers.MqttHelper;

public class OTGStatus {
    public static MqttHelper mqttHelper;
    public static Handler handler = null;

    //
    // MQTT settings
    public static String mqServerUri = "";
    public static String mqPort = "";
    public static String mqUsername = "";
    public static String mqPassword = "";

    public static String mqClientId = "";
    public static String mqSubscriptionTopic = "";
    public static String mqPublishTopic = "";
    public static String lastMqSubscriptionTopic = "";
    public static String gateUserID = "";
    public static String gatePassword = "";

    public static boolean mqttStarted = false;                          //Flag to only start mqtt once
    public static boolean testMode = false;                             //For testing while configuring
    public static String command;
    public static SharedPreferences sharedPref;

    public static final String GRESULT_OK = "1";
    public static final String GRESULT_DISABLED="2";
    public static final String GRESULT_INVALID="3";
    public static final String GRESULT_ERROR="4";
    public static final String GRESULT_TESTOK = "5";

    //Read the settings from shared preferences
    public static void readSettings() {
        //Get account settings
        gateUserID = sharedPref.getString("gateUserID", "");
        gatePassword = sharedPref.getString("gatePassword", "");
        mqPort = sharedPref.getString("mqPort", "");
        mqUsername = sharedPref.getString("mqUsername", "");
        mqPassword = sharedPref.getString("mqPassword", "");
        mqClientId = sharedPref.getString("mqClientId", "");
        mqPublishTopic = sharedPref.getString("mqPublishTopic", "");
        mqServerUri = sharedPref.getString("mqServerUri", "");
        mqSubscriptionTopic = mqPublishTopic + "r";
    }
}
