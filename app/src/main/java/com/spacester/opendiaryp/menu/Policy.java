package com.spacester.opendiaryp.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;

import com.spacester.opendiaryp.R;

public class Policy extends AppCompatActivity {

    WebView webView;
    ImageView imageView3;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        webView = findViewById(R.id.webView);
        imageView3 = findViewById(R.id.imageView3);
        imageView3.setOnClickListener(v -> onBackPressed());
        webView.requestFocus();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.setSoundEffectsEnabled(true);
        webView.loadData("<!DOCTYPE html>\n" +
                        "    <html>\n" +
                        "    <head>\n" +
                        "      <meta charset='utf-8'>\n" +
                        "      <meta name='viewport' content='width=device-width'>\n" +
                        "      <title>Privacy Policy</title>\n" +
                        "      <style> body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; padding:1em; } </style>\n" +
                        "    </head>\n" +
                        "    <body>\n" +
                        "    <strong>Privacy Policy</strong> <p>\n" +
                        "                  OpenDiary is a final year course project app as\n" +
                        "                  a Free app. This app is developed by\n" +
                        "                  Hossain Md Sahadat and Lei Yati at no cost and is intended for use as\n" +
                        "                  is.\n" +
                        "                </p> <p>\n" +
                        "                  This page is used to inform visitors regarding our\n" +
                        "                  policies with the collection, use, and disclosure of Personal\n" +
                        "                  Information if anyone decided to use our Service.\n" +
                        "                </p> <p>\n" +
                        "                  If you choose to use our Service, then you agree to\n" +
                        "                  the collection and use of information in relation to this\n" +
                        "                  policy. The Personal Information that we collect is\n" +
                        "                  used for providing and improving the Service. We will not use or share your information with\n" +
                        "                  anyone except as described in this Privacy Policy.\n" +
                        "                </p> <p>\n" +
                        "                  The terms used in this Privacy Policy have the same meanings\n" +
                        "                  as in our Terms and Conditions, which is accessible at\n" +
                        "                  Memespace unless otherwise defined in this Privacy Policy.\n" +
                        "                </p> <p><strong>Information Collection and Use</strong></p> <p>\n" +
                        "                  For a better experience, while using our Service, we\n" +
                        "                  may require you to provide us with certain personally\n" +
                        "                  identifiable information, including but not limited to email. The information that\n" +
                        "                  we request will be retained by us and used as described in this privacy policy.\n" +
                        "                </p> <div><p>\n" +

                        "                </p> <p>\n" +
                        "    </body>\n" +
                        "    </html>\n" +
                        "      ",
                "text/html", "UTF-8");
    }
}