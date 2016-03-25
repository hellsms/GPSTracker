package fii.licenta.ioana.rollndice;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ioana on 17/03/2016.
 */
public class HttpRequest extends AsyncTask<Void, Integer, String> {

    private TextView textView;
    private String result;

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    protected String doInBackground(Void... params) {
        result = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL("https://www.random.org/integers/?num=1&min=1&max=6&col=1&base=10&format=plain&rnd=new");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            result = readStream(in);
        }
        catch(Exception e){
            Log.e("getHML", e.toString());
        }
        finally {
            urlConnection.disconnect();
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        setData(s);
    }

    private String readStream(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuffer buffer = new StringBuffer();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        }
        catch(Exception e){
            Log.e("readStream", "readLine error.");
        }
        return buffer.toString();
    }

    private void setData(String data){
        textView.setText(data);
    }
}
