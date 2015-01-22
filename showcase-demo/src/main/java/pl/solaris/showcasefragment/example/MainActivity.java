package pl.solaris.showcasefragment.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pl.solaris.showcasefragment.ShowcaseFragment;

public class MainActivity extends ActionBarActivity implements ShowcaseFragment.ShowcaseListener {
    public static final String GITHUB_WEBPAGE = "https://github.com/";

    @InjectView((R.id.toolbar))
    Toolbar toolbar;

    @InjectView(R.id.webView)
    WebView webView;

    @InjectView(R.id.githubImageView)
    View githubView;

    @InjectView(R.id.pb)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl(GITHUB_WEBPAGE);
    }

    @OnClick(R.id.fabBtn)
    public void fabClicked() {
        ShowcaseFragment.startShowcase(MainActivity.this, githubView,
                getString(R.string.github_title),
                getString(R.string.github_desc),
                getString(android.R.string.ok), ((SwitchCompat) findViewById(R.id.switchForToolbar)).isChecked() ? R.style.CustomShowcase : 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem itemSwitch = menu.findItem(R.id.action_switch);
        itemSwitch.setActionView(R.layout.item_switch);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.abc_shareactionprovider_share_with)));
                return true;
            case R.id.action_switch:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShowcaseClose() {
        onBackPressed();
    }
}
