package ro.go.mariusiliescu.panicbutton.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by maryus on 26.03.2016.
 */
public class FileTxtManager {
    Context c;
    public FileTxtManager(Context c){
        this.c = c;
    }
    public void writeToFile(Editable data) {

        String filename = "myfile.txt";

        File file = new File(c.getFilesDir(), filename);
        FileOutputStream outputStream;

        try {
            outputStream = c.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String readFromFile() {

        String ret = "";

        try {
            Log.d("txt","read number");
            InputStream inputStream = c.openFileInput("myfile.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }
}
