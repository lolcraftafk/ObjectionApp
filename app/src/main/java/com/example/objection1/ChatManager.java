package com.example.objection1;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;

import java.io.InputStream;
import java.net.HttpCookie;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;


public class ChatManager {
    private final String MAIL_BOX_URL = "https://objection1.herokuapp.com/mailbox";

    Context mContext;
    CookieManager cookieManager;
    WebView webView;

    ChatManager(Context m, WebView webView1){
        webView = webView1;
        mContext =m;
        cookieManager = CookieManager.getInstance();
    }

    public void sendMessage(String message, String recipient){
        List<HttpCookie> a;
        a = HttpCookie.parse(cookieManager.getCookie("https://objection1.herokuapp.com/"));

//        String pnumber = "0";
//        for (HttpCookie n : a) {
//            if(n.getName() == "pnumber")
//            {
//                pnumber = n.getValue();
//            }
//        }

        WebView webView = new WebView(mContext);
        webView.setVisibility(View.INVISIBLE);
        String formData = "reciver_number=" + recipient + "&message="+message;

        webView.postUrl(MAIL_BOX_URL, formData.getBytes());
        Log.d("TAG", "sendMessage: ");
    }
}
