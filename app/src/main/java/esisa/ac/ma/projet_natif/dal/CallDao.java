package esisa.ac.ma.projet_natif.dal;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import esisa.ac.ma.projet_natif.entities.Call;

public class CallDao {
    private ContentResolver contentResolver;

    public CallDao(Context context) {
        contentResolver = context.getContentResolver();
    }

    public List<Call> getCalls() {
        List<Call> calls = new ArrayList<>();
        String[] projection = {
                CallLog.Calls.CACHED_PHOTO_URI, CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION};
        Cursor cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, projection,
                null, null, CallLog.Calls.DATE + " DESC");

        if (cursor != null) {
            int photoIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI);
            int cachedNameIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);

            while (cursor.moveToNext()) {
                String photo = cursor.getString(photoIndex);
                String title = cursor.getString(cachedNameIndex);
                String number = cursor.getString(numberIndex);

                // Check if the title is null or empty and if the number is not null or empty
                if ((title == null || title.isEmpty()) && number != null && !number.isEmpty()) {
                    title = number;
                } else if (title == null || title.isEmpty() || title.equals("nombre privee")) {
                    title = "Unknown";
                }

                int typeCode = cursor.getInt(typeIndex);
                String type = getType(typeCode);
                long date = cursor.getLong(dateIndex);
                long duration = cursor.getLong(durationIndex);
                String callTime = getFormattedDate(date);

                calls.add(new Call(title, type, "", date, duration, callTime, photo));
            }
            cursor.close();
        }

        return calls;
    }

    private String getType(int typeCode) {
        switch (typeCode) {
            case CallLog.Calls.INCOMING_TYPE:
                return "Incoming";
            case CallLog.Calls.OUTGOING_TYPE:
                return "Outgoing";
            case CallLog.Calls.MISSED_TYPE:
                return "Missed";
            default:
                return "Unknown";
        }
    }

    private String getFormattedDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}