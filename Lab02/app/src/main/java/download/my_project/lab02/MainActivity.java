package download.my_project.lab02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AsyncSendRequest asr = new AsyncSendRequest(getApplicationContext());
        asr.addCommunicationEventListener(new CommunicationEventListener() {
            public boolean handleServerResponse(String response) {
                TextView textView = (TextView) findViewById(R.id.output);
                textView.setText(response);
                return true;
            }
        });

        asr.sendRequest("{ RoomNumber: " + (int) Math.floor(Math.random() * 1000) + " }", "http://moap.iict.ch:8080/Moap/Basic"); // ZipBase64
    }
}
