package ro.go.mariusiliescu.panicbutton.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ro.go.mariusiliescu.panicbutton.R;
import ro.go.mariusiliescu.panicbutton.constants.KeyConstant;
import ro.go.mariusiliescu.panicbutton.services.ButtonCheckService;
import ro.go.mariusiliescu.panicbutton.utils.DeviceAdminUtil;
import ro.go.mariusiliescu.panicbutton.utils.PhoneData;
import ro.go.mariusiliescu.panicbutton.utils.FileTxtManager;

public class MainActivity extends AppCompatActivity {

    private Button setNum;
    private EditText getNum;
    private TextView phoneNum;

    private long prevTime;

    FileTxtManager fileTxtManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setNum = (Button) findViewById(R.id.set_num_btn);
        getNum = (EditText) findViewById(R.id.getNum);
        phoneNum = (TextView) findViewById(R.id.numTextView);

        fileTxtManager = new FileTxtManager(MainActivity.this);
        final String phoneNumS=  fileTxtManager.readFromFile();
        phoneNum.setText(phoneNumS);

        //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        PhoneData.savePhoneData(MainActivity.this, KeyConstant.UNLOCK_STR, true);
        ButtonCheckService.manageService(MainActivity.this);

        setNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = getNum.getText().toString();

                if (FileTxtManager.isValidPhoneNumber(number)) {
                    fileTxtManager.writeToFile(getNum.getText());
                    phoneNum.setText(getNum.getText());
                    getNum.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "This is not a phone number ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        boolean x =DeviceAdminUtil.checkisDeviceAdminEnabled();
        if(!x){
            DeviceAdminUtil.openDeviceManagerEnableAction(this);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();

        return true;
    }
    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("x","activRes");
        PhoneData.savePhoneData(this, KeyConstant.VOLUME_LOCK_ENABLE_STR, DeviceAdminUtil.checkisDeviceAdminEnabled());
        ButtonCheckService.manageService(MainActivity.this);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed()
    {
        if(System.currentTimeMillis() - prevTime < 2000)
        {
            super.onBackPressed();
        }
        else
        {
            Toast.makeText(this, "Press Again to Exit ", Toast.LENGTH_SHORT).show();
        }

        prevTime = System.currentTimeMillis();
    }
}
