package in.clubgo.app.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by Jitendra Soam on 19/8/16.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody().trim();
                    String[] separate = senderNum.split("-");
                    if (separate.length == 2) {
                        try {
                            if (separate[1].equalsIgnoreCase("CLUBGO")) {
                                //Pass on the text to our listener.
                                String code = "";
                                char[] soam = message.toCharArray();
                                for (int j = 18; j < 22; j++) {
                                    code += soam[j];
                                }
                                if (!code.equalsIgnoreCase("")) {
                                    mListener.messageReceived(code);
                                }
                            }
                        } catch (Exception e) {
                        }
                    }

                }
            }

        } catch (Exception e) {

        }
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    public interface SmsListener {
        public void messageReceived(String messageText);
    }
}

