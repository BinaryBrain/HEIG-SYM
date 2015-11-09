package download.my_project.lab02;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Pair;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class AsyncSendRequest {
    private ArrayList<CommunicationEventListener> listeners = new ArrayList<>();
    private Context ctx;
    private ArrayList<Pair<String, String>> waitingRequests = new ArrayList<>();
    private NetworkReceiver nr;

    public AsyncSendRequest(Context ctx) {
        this.ctx = ctx;
        this.nr = new NetworkReceiver(this);
        ctx.registerReceiver(nr, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * Permet d'envoyer un document vers le serveur dont l'URL est désignée par link.
     * @param request
     * @param url
     */
    public void sendRequest(String request, String url) {
        ConnectivityManager connMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new SendRequestTask().execute(url, request);
        } else {
            Toast.makeText(ctx, "Error: Unable to access the network", Toast.LENGTH_LONG).show();
            waitingRequests.add(new Pair(url, request));
        }
    }

    /**
     * Permet de définir un listener qui sera invoqué lorsque la réponse parviendra au client.
     * @param listener
     */
    public void addCommunicationEventListener(CommunicationEventListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Envoie les requêtes en attentes
     */
    public void sendQueue() {
        for (Pair<String, String> waitingRequest: waitingRequests) {
            ConnectivityManager connMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                new SendRequestTask().execute(waitingRequest.first, waitingRequest.second);
            }
        }

        waitingRequests.clear();
    }

    /**
     * Crée une tâche asynchrone afin d'envoyer une requête POST et de récupérer sa réponse
     */
    private class SendRequestTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(strings[0]).openConnection();

                conn.setRequestMethod("POST");

                OutputStream os = conn.getOutputStream();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream zos = new GZIPOutputStream(baos);

                zos.write(strings[1].getBytes());
                zos.flush();
                zos.close();
                baos.flush();
                baos.close();

                os.write(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
                os.close();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // Lecture de la réponse
                    InputStream is = conn.getInputStream();

                    int bytesRead = -1;
                    byte[] buffer = new byte[1024];

                    String response = "";
                    while ((bytesRead = is.read(buffer)) >= 0) {
                        response = new String(buffer, 0, bytesRead);
                    }

                    String lines[] = response.split("\\r?\\n");
                    String base64 = lines[lines.length - 1];

                    // Unzip et decode Base64
                    ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(base64.getBytes(), Base64.DEFAULT));
                    GZIPInputStream gzis = new GZIPInputStream(bais);
                    InputStreamReader reader = new InputStreamReader(gzis);
                    BufferedReader in = new BufferedReader(reader);

                    String line;
                    while ((line = in.readLine()) != null) {
                        response += line;
                    }

                    return response;
                }
                else {
                    return "fail";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "fail";
        }

        /**
         * On donne le résultat aux listeners.
         */
        protected void onPostExecute(String result) {
            for (CommunicationEventListener listener: listeners) {
                listener.handleServerResponse(result);
            }
        }
    }
}

