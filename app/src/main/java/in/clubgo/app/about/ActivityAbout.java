package in.clubgo.app.about;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.defuzed.clubgo.R;

import in.clubgo.app.app.AppController;
import in.clubgo.app.base.BaseActivity;

public class ActivityAbout extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        findViewById(R.id.back).setOnClickListener(this);

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/demo/about.html");
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
