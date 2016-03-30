package ro.go.mariusiliescu.mygps;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {

    GPSTracker gpsTracker;

    TextView latitudeTextView;
    TextView longitudeTetView;
    ImageButton toMapButon;

    private GoogleApiClient client;

    private void initComponents() {

        toMapButon = (ImageButton) findViewById(R.id.imageButton);
        latitudeTextView = (TextView) findViewById(R.id.currentLatitude);
        longitudeTetView = (TextView) findViewById(R.id.currentLogitude);


        gpsTracker = new GPSTracker(MainActivity.this);

        gpsTracker.getLocation();


        toMapButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.google.ro/maps/place/" + gpsTracker.getLatitude() + "," + gpsTracker.getLongitude()));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        initComponents();
        getAndSetLocation();

        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        Intent sharingIntent = new Intent(Intent.ACTION_SENDTO);
                        sharingIntent.setType("text/plain");
                        sharingIntent.setData(Uri.parse("mailto:"));
                        String shareBody = "https://www.google.ro/maps/place/" + gpsTracker.getLatitude() + "," +gpsTracker.getLongitude() + "";
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "My Location");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }
                }).start();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_locate) {
            getAndSetLocation();
            Toast.makeText(getApplicationContext(), "Location Updated", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getAndSetLocation() {

        if (gpsTracker.canGetLocation()) {
            gpsTracker.getLocation();
            latitudeTextView.setText(String.format("%s", gpsTracker.getLatitude()));
            longitudeTetView.setText(String.format("%s", gpsTracker.getLongitude()));
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://ro.go.mariusiliescu.mygps/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://ro.go.mariusiliescu.mygps/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}


