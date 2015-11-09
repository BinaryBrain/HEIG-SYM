package download.my_project.lab02;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {
    private AsyncSendRequest asr;

    public NetworkReceiver(AsyncSendRequest asr) {
        this.asr = asr;
    }

    /**
     * Détécte si la connection à Internet est possible. Si c'est le cas, la pile de requêtes sera executée.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("myapp", "OnReceive");

        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            Log.i("myapp", "Network " + networkInfo.getTypeName() + " connected");
        } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
            Log.d("myapp", "There's no network connectivity");
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            asr.sendQueue();
        }
    }
}
