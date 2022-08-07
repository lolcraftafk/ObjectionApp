package com.example.objection1;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    String[] menuNoPages = { "login", "signup", "emrn", "lawyer", "done", "recordings", "chat"};
    WebView myWebView;
    BottomNavigationView nav;
    String message = "";
    SOSButton sosButton;

    //On creation of main activity, finds WebView element, sets it up and starts it
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sosButton = new SOSButton(this);

        nav = findViewById(R.id.bottom_nav);

        myWebView = (WebView) findViewById(R.id.webview);
        ChatManager chatManager = new ChatManager(this, myWebView, sosButton);

        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Boolean menu = true;
                for(int i = 0; i < menuNoPages.length; i++){
                    if (url.contains(menuNoPages[i]) || url.replace("https://objection1.herokuapp.com/", "") == "") {
                        menu = false;
                    }
                    if(url.contains("login")){

                    }
                    if(url.contains("guide")){
                        myWebView.loadUrl("https://objection1.herokuapp.com/settings");
                        lawLearnList(null);
                    }
                    if(url.contains("chat")){
//                        chatManager.sendMessage("HIIII", "0585301005");
//                        chatManager.getMessage();
//                        if(chatManager.message != null){
//                            String[] messages = getMessage(chatManager.message);
//                            for(int j = 0; j < messages.length; j ++){
//                                myWebView.loadUrl("javascript:addHisMessage("+messages[i]+");");
//                            }
//                        }
                    }
                    if(url.contains("instagram")){
                        myWebView.loadUrl("https://objection1.herokuapp.com/");
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/objection22?igshid=YmMyMTA2M2Y="));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setPackage("com.android.chrome");
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            // Chrome browser presumably not installed so allow user to choose instead
                            intent.setPackage(null);
                            startActivity(intent);
                        }
                    }

                }
                if(menu){
                    nav.setVisibility(View.VISIBLE);
                } else{
                    nav.setVisibility(View.INVISIBLE);
                }
            };
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());

                return false;
            }
        });


        myWebView.loadUrl("https://objection1.herokuapp.com/");

        myWebView.addJavascriptInterface(new WebViewJSInterface(this, myWebView, chatManager, sosButton), "Android");

        IntentFilter intentFilter = new IntentFilter();

        // Add network connectivity change action.
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");

        // Set broadcast receiver priority.
        intentFilter.setPriority(100);

        // Create a network change broadcast receiver.
        OnOffReceiver reciever = new OnOffReceiver(sosButton);

        // Register the broadcast receiver with the intent filter object.
        registerReceiver(reciever, intentFilter);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                myWebView.goBack();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);


    }


    public void getHelp(MenuItem v){
        myWebView.loadUrl("https://objection1.herokuapp.com/emrn");

    }

    public void lawyersList(MenuItem v){
        myWebView.loadUrl("https://objection1.herokuapp.com/lawyerlist");
    }

    public void recordingsList(MenuItem v){
        myWebView.loadUrl("https://objection1.herokuapp.com/recordings");

    }

    public void lawLearnList(MenuItem v){
        Toast.makeText(this,"Coming Soon...",Toast.LENGTH_SHORT).show();
    }

    public void menuButton(MenuItem v){
        myWebView.loadUrl("https://objection1.herokuapp.com/settings");

    }

    private String[] getMessage(String result) {

        Log.d("TAG", "getMessage: " + result);
        try {
            JSONObject json= (JSONObject) new JSONTokener(result).nextValue();
            JSONArray json2 = (JSONArray) json.getJSONArray("message");
            String[] messages = new String[json2.length()];
            for (int i = 0; i < json2.length(); i++){
                messages[i] = (String)json2.get(i);
            }
            return messages;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return null;
    }
}