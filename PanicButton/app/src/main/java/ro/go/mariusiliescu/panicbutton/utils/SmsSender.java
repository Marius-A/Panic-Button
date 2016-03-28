package ro.go.mariusiliescu.panicbutton.utils;

import android.content.Context;
import android.telephony.SmsManager;

/**
 * Created by maryus on 26.03.2016.
 */
public class SmsSender {
    Context c;

    public SmsSender(Context c){
        this.c = c;
    }

    public void sendSms(String smsNumber ,String smsText){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(smsNumber, null,smsText, null, null);
    }
}
