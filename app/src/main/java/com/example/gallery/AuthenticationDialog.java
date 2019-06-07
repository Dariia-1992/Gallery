package com.example.gallery;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

public class AuthenticationDialog extends Dialog {

    public interface AuthenticationListener{
       void onTokenReceived(String authentication_token);
    }

    private final String redirect_url; //адрес, на который Инстаграм перенаправляет пользователя после успешной аутентификации
    private final String request_url; //это запрос, который направит пользователя на страницу входа в Инстаграм
    private AuthenticationListener listener; //слушатель, которым будет MainActivity. Когда токен доступа будет получен, он будет передан слушателю, а диалог, исполнив свою функцию, закроется.

    public AuthenticationDialog(Context context, AuthenticationListener listener) {
        super(context);
        this.listener = listener;
        this.redirect_url = context.getResources().getString(R.string.redirect_url);
        this.request_url = context.getResources().getString(R.string.base_url) +
                "oauth/authorize/?client_id=" +
                context.getResources().getString(R.string.client_id) +
                "&redirect_uri=" + redirect_url +
                "&response_type=token&display=touch&scope=public_content";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.authentication_dialog);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(request_url);
        WebViewClient webClient = new WebViewClient(){
            //при открытии страницы с адресом redirect_url диалог закрывается, пользователь вошел, можно получить токен доступа
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request.getUrl().toString().startsWith(redirect_url))
                {
                    AuthenticationDialog.this.dismiss();
                    return true;//super.shouldOverrideUrlLoading(view, request);
                }
               return false;
            }
            //получение токена доступа
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("access_token=")){
                    Uri uri = Uri.EMPTY.parse(url);
                    String access_token = uri.getEncodedFragment();
                    access_token = access_token.substring(access_token.lastIndexOf("=") + 1);
                    listener.onTokenReceived(access_token);
                }
            }
        };

        webView.setWebViewClient(webClient);
    }
}
