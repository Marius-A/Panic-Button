package ro.go.mariusiliescu.panicbutton.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ro.go.mariusiliescu.panicbutton.R;
import ro.go.mariusiliescu.panicbutton.services.ButtonCheckService;
import ro.go.mariusiliescu.panicbutton.utils.TxtManager;

public class MainActivity extends AppCompatActivity {

    private Button setNum;
    private EditText getNum;
    private TextView phoneNum;

    private long prevTime;

    TxtManager txtManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setNum = (Button) findViewById(R.id.set_num_btn);
        getNum = (EditText) findViewById(R.id.getNum);
        phoneNum = (TextView) findViewById(R.id.numTextView);

        txtManager = new TxtManager(MainActivity.this);
        final String phoneNumS=  txtManager.readFromFile();
        phoneNum.setText(phoneNumS);

        ButtonCheckService.manageService(MainActivity.this);
        setNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = getNum.getText().toString();
                String regexStr = "^[0-9]{10}$";

                if (isValidPhoneNumber(number)) {
                    txtManager.writeToFile(getNum.getText());
                    phoneNum.setText(getNum.getText());
                    getNum.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "This is not a phone number ", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

}
