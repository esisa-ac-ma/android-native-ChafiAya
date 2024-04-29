package esisa.ac.ma.projet_natif.dal;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import esisa.ac.ma.projet_natif.entities.Sms;

public class SmsDao {

    private ContentResolver contentResolver;
    private Map<String, Sms> smsMap = new HashMap<>();

    public SmsDao(Context context) {
        contentResolver = context.getContentResolver();
        load();
    }

    public Map<String, Sms> getSMSMap() {
        return smsMap;
    }

    public List<Sms> getSMSList() {
        // Sort the SMS entries by date in descending order
        List<Sms> smsList = new ArrayList<>(smsMap.values());
        Collections.sort(smsList, new Comparator<Sms>() {
            @Override
            public int compare(Sms sms1, Sms sms2) {
                return Long.compare(sms2.getDate(), sms1.getDate());
            }
        });
        return smsList;
    }
    public void load() {
        Uri uri = Telephony.Sms.CONTENT_URI;
        String[] projection = new String[]{Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE, Telephony.Sms.TYPE};
        String selection = Telephony.Sms.TYPE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(Telephony.Sms.MESSAGE_TYPE_INBOX)};
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, Telephony.Sms.DATE + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String address = cursor.getString(0);
                String body = cursor.getString(1);
                String dateString = cursor.getString(2);
                int type = cursor.getInt(3);

                if (!dateString.isEmpty()) {
                    long date = Long.parseLong(dateString);

                    Sms sms;
                    if (smsMap.containsKey(address)) {
                        sms = smsMap.get(address);
                    } else {
                        sms = new Sms(address, "", date, type);
                        smsMap.put(address, sms);
                    }

                    sms.addMessage(body);
                }
            }
            cursor.close();
        }
    }

}
