package com.ntp.openthegate;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import helpers.MqttHelper;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OTGStatus.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        OTGStatus.readSettings();
        Intent sIntent;

        if(OTGStatus.gateUserID == "") {
            setTestMode();
            sIntent = new Intent(this, AccountSettingsActivity.class);
            startActivity(sIntent);
        }
        else if(OTGStatus.mqServerUri == "") {
            setTestMode();
            sIntent = new Intent(this, ServerSettingsActivity.class);
            startActivity(sIntent);
        }else
            {
            if (!OTGStatus.mqttStarted) {
                Log.w("Debug", "Start MQTT");
                OTGStatus.command = "open";
                startMqtt();
            }
        }
    }

    //This method runs when returning from a menu selection
    @Override
    protected void onResume() {
        Log.w("Debug","MainActivity onResume");
        super.onResume();

        OTGStatus.readSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //This is where items on the menu are run
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent sIntent;
        switch(item.getItemId()) {
            case R.id.action_account:
                setTestMode();
                sIntent = new Intent(this, AccountSettingsActivity.class);
                startActivity(sIntent);
                return(true);
            case R.id.action_server:
                setTestMode();
                sIntent = new Intent(this, ServerSettingsActivity.class);
                startActivity(sIntent);
                return(true);
            case R.id.action_exit:
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
        }
        return(super.onOptionsItemSelected(item));
    }


    //Make the Test button visible and set the app to test mode so connectivity can be tested without opening the gate
    void setTestMode(){
        Button btnTest = (Button) findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testConnect();
            }
        });
        btnTest.setVisibility(View.GONE);  //hide button
        OTGStatus.testMode = true;
        btnTest.setVisibility(View.VISIBLE);  //Show test button

    }

    //Test connection without opening gate
    private  boolean testConnect(){
        OTGStatus.command = "test";
        startMqtt();
        return true;
    }

    private void startMqtt(){
        if(OTGStatus.mqttHelper == null) {

            OTGStatus.mqttHelper = new MqttHelper(getApplicationContext());
            OTGStatus.mqttHelper.mqttAndroidClient.setCallback(new MqttCallbackExtended() {

                @Override
                public void connectComplete(boolean b, String s) {
                    Log.w("Debug", "Connected");
                }

                @Override
                public void connectionLost(Throwable throwable) {
                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    Log.w("Debug", mqttMessage.toString());
                    if (mqttMessage.toString().contains(OTGStatus.GRESULT_OK)) {
                        displayToast(getString(R.string.open_message));
                        setScreenMessage(R.string.gate_opening);
                        //Message received ok so kill the app as the gate should open
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        }, 5000);
                    } else if (mqttMessage.toString().contains(OTGStatus.GRESULT_DISABLED)) {
                        setScreenMessage(R.string.account_disabled);
                        displayToast(getString(R.string.account_disabled));
                        setTestMode();
                    } else if (mqttMessage.toString().contains(OTGStatus.GRESULT_INVALID)) {
                        setScreenMessage(R.string.invalid_account);
                        displayToast(getString(R.string.invalid_account));
                        setTestMode();
                    } else if (mqttMessage.toString().contains(OTGStatus.GRESULT_TESTOK)) {
                        setScreenMessage(R.string.test_ok);
                        displayToast(getString(R.string.test_ok));
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }
            });
        }
        else
        {
            OTGStatus.mqttHelper.connect();
            setScreenMessage(OTGStatus.mqttHelper.connectResult);
        }
    }

    /**
     * Displays a Toast message.
     * @param message Message to display.
     */
    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays a message on the main screen.
     * @param message Message to display.
     */
    public void setScreenMessage(int message){
        String string = getString(message);
        setScreenMessage(string);
    }

    public void setScreenMessage(String message){

        if(message == null)
            return;
        int textSize = 20;
        int textLength = message.length();

        if(textLength < 17)
            textSize = 50;
        if((textLength > 17)&&(textLength < 34))
            textSize = 30;

        TextView textElement = (TextView) findViewById(R.id.result_text);
        textElement.setTextSize(COMPLEX_UNIT_SP, textSize);
        textElement.setText(message);
    }
}
