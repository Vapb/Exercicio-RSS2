package br.ufpe.cin.if1001.rss.util;

import android.app.IntentService;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import br.ufpe.cin.if1001.rss.db.SQLiteRSSHelper;
import br.ufpe.cin.if1001.rss.domain.ItemRSS;
import android.support.v4.content.LocalBroadcastManager;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>

 */
public class DownloadIntentService extends IntentService {

    private static final String TAG = "br.ufpe.cin.if1001.rss.";

    // Mensagem Para Broadcast
    public static String RSS_READY = "br.ufpe.cin.if1001.rss.action.RSS_READY";


    public DownloadIntentService() {
        super("DownloadIntentService");
    }

    // GEt RSS FEED to use in onHandleIntent
    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        /// What the service does
        Log.i(TAG,"Service começou");

        // get db
        SQLiteRSSHelper db = SQLiteRSSHelper.getInstance(getApplicationContext());

        List<ItemRSS> items = null;
        try {
            String feed = getRssFeed(intent.getStringExtra("url"));
            items = ParserRSS.parse(feed);
            for (ItemRSS i : items) {
                Log.d("DB", "Buscando no Banco por link: " + i.getLink());
                ItemRSS item = db.getItemRSS(i.getLink());
                if (item == null) {
                    Log.d("DB", "Encontrado pela primeira vez: " + i.getTitle());
                    db.insertItem(i);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        // Send Broadcast
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(RSS_READY));

    }

}


