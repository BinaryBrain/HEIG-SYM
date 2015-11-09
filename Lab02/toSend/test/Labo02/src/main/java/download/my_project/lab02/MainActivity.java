package download.my_project.lab02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private AsyncSendRequest asr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        asr = new AsyncSendRequest(getApplicationContext());
        asr.addCommunicationEventListener(new CommunicationEventListener() {
            public boolean handleServerResponse(String response) {
                TextView textView = (TextView) findViewById(R.id.output);
                textView.setText(response);
                return true;
            }
        });

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData();
            }
        });
    }

    private void sendData() {
        asr.sendRequest("{ RoomNumber: " + (int) Math.floor(Math.random() * 1000) + " }", "http://moap.iict.ch:8080/Moap/Basic");
    }
}
