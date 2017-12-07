package net.december1900.gozhihugo;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.drawee.view.SimpleDraweeView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity {

    private EditText mUserName;
    private EditText mPassword;
    private EditText mCaptcha;
    private Button mButton;
    private SimpleDraweeView mDraweeView;

    private String _xsrf;

    private OkHttpClient mClient;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mUserName = (EditText) findViewById(R.id.et_username);
        mPassword = (EditText) findViewById(R.id.et_psw);
        mCaptcha = (EditText) findViewById(R.id.et_captcha);
        mButton = (Button) findViewById(R.id.btn_go);
        mDraweeView = (SimpleDraweeView) findViewById(R.id.captcha_code);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getXsrf();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Uri uri = Uri.parse("http://www.zhihu.com/captcha.gif?r=" + System.currentTimeMillis() + "&type=login");
                        mDraweeView.setImageURI(uri);
                    }
                });
            }
        }).start();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Login();
                    }
                }).start();
            }
        });

    }


    private void getXsrf() throws IOException {
        Request request = new Request.Builder()
                .url("https://www.zhihu.com/#signin")
                .build();

        Response response = mClient.newCall(request).execute();
        String result = response.body().string();
        Document document = Jsoup.parse(result);
        Elements select = document.select("input[type=hidden]");
        Element element = select.get(0);
        _xsrf = element.attr("value");
        Log.d(TAG, _xsrf);
    }


    private void Login() {
        FormBody formBody = new FormBody.Builder()
                .add("captcha", mCaptcha.getText().toString())
                .add("email", mUserName.getText().toString())
                .add("password", mPassword.getText().toString())
                .add("_xsrf", _xsrf)
                .build();

        Request request = new Request.Builder()
                .url("http://www.zhihu.com/login/email")
                .build();

        try {
            Response response = mClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


