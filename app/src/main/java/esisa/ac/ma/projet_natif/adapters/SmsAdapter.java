package esisa.ac.ma.projet_natif.adapters;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import esisa.ac.ma.projet_natif.R;
import esisa.ac.ma.projet_natif.entities.Sms;

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.Item> {
    private Context context;
    private List<Sms> smsList = new ArrayList<>();
    private List<Sms> originalSmsList = new ArrayList<>(); // Added originalSmsList to store the original SMS list

    public SmsAdapter(Context context) {
        this.context = context;
    }

    public void setSMSList(List<Sms> smsList) {
        this.smsList.clear();
        this.smsList.addAll(smsList);
        this.originalSmsList.clear(); // Clear originalSmsList
        this.originalSmsList.addAll(smsList); // Populate originalSmsList with the provided SMS list
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sms, parent, false);
        return new Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        if (smsList == null || smsList.isEmpty()) {
            return; // Do nothing if the list is empty or null
        }

        Sms sms = smsList.get(position);

        String title = getContactNameOrPhoneNumber(sms.getAddress());
        holder.titleTextView.setText(title);

        if (isPhoneNumber(title)) {
            holder.profilePicture.setImageResource(R.drawable.emoji);
        } else {
            holder.profilePicture.setImageBitmap(getProfileBitmap(title));
        }

        holder.messageTime.setText(getFormattedDate(sms.getDate()));

        int messageCount = sms.getMessageList().size();
        holder.messageCount.setText("" + messageCount + "");
        holder.messageCount.setBackgroundResource(R.drawable.circle_red);

        // Set message count visibility based on expanded state
        holder.messageCount.setVisibility(!sms.isExpanded() ? View.VISIBLE : View.GONE);

        //holder.messageCount.setBackgroundColor(ContextCompat.getColor(context, R.color.pink));
        holder.messageBody.setText(sms.isExpanded() ? getAllMessagesAsString(sms) : sms.getMessageList().get(0));

        holder.divider.setVisibility(position < smsList.size() - 1 ? View.VISIBLE : View.GONE);

        // Collapse/Expand message on click
        holder.itemView.setOnClickListener(v -> {
            sms.setExpanded(!sms.isExpanded());
            // Hide message count when collapsing the message
            holder.messageCount.setVisibility(sms.isExpanded() ? View.VISIBLE : View.GONE);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    public void filterSMS(String query) {
        query = query.toLowerCase();
        List<Sms> filteredSMS = new ArrayList<>();
        if (query.isEmpty()) {
            filteredSMS.addAll(originalSmsList);
        } else {
            for (Sms sms : originalSmsList) {
                String contactName = getContactNameOrPhoneNumber(sms.getAddress()).toLowerCase();
                if (contactName.contains(query)) {
                    filteredSMS.add(sms);
                }
            }
        }

        smsList.clear();
        smsList.addAll(filteredSMS);
        notifyDataSetChanged();
    }



    public static class Item extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        TextView titleTextView;
        TextView messageBody;
        TextView messageTime;
        TextView messageCount;
        View divider;

        public Item(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.image_view_profile_picture);
            titleTextView = itemView.findViewById(R.id.text_view_title);
            messageBody = itemView.findViewById(R.id.text_view_message_body);
            messageTime = itemView.findViewById(R.id.text_view_message_time);
            messageCount = itemView.findViewById(R.id.text_view_message_count);
            divider = itemView.findViewById(R.id.divider);
        }
    }

    private String getFormattedDate(long date) {
        SimpleDateFormat dateFormat;
        Date currentDate = new Date(System.currentTimeMillis());
        Date messageDate = new Date(date);

        // Check if the message date is today
        if (isSameDay(currentDate, messageDate)) {
            dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault()); // Display time with AM/PM
        } else if (isSameWeek(currentDate, messageDate)) {
            dateFormat = new SimpleDateFormat("EEE", Locale.getDefault()); // Display day of week (3 letters)
        } else {
            dateFormat = new SimpleDateFormat("d MMM", Locale.getDefault()); // Display day and month
            // Check if the message date is in the same year as the current date
            if (currentDate.getYear() != messageDate.getYear()) {
                dateFormat.applyPattern("d MMM yyyy"); // Display day, month, and year
            }
        }

        return dateFormat.format(messageDate);
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isSameWeek(Date currentDate, Date messageDate) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentDate);
        Calendar messageCalendar = Calendar.getInstance();
        messageCalendar.setTime(messageDate);

        int currentWeek = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int messageWeek = messageCalendar.get(Calendar.WEEK_OF_YEAR);

        return currentCalendar.get(Calendar.YEAR) == messageCalendar.get(Calendar.YEAR) && currentWeek == messageWeek;
    }

    private String getContactNameOrPhoneNumber(String phoneNumber) {
        String contactName = "";
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = contentResolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                if (columnIndex != -1) {
                    contactName = cursor.getString(columnIndex);
                }
            }
            cursor.close();
        }

        return !contactName.isEmpty() ? contactName : phoneNumber;
    }

    private boolean isPhoneNumber(String text) {
        return text.matches("^\\+?\\d+$");
    }

    private Bitmap getProfileBitmap(String name) {
        // Check if the name contains emojis
        if (containsEmojis(name)) {
            return getInitialsBitmap(name, generateRandomDarkColor());
        } else {
            // Check if the name corresponds to a contact
            String contactName = getContactNameOrPhoneNumber(name);
            if (!contactName.isEmpty()) {
                return getInitialsBitmap(contactName, generateRandomDarkColor());
            } else {
                // Log if contact name is empty
                Log.d("ProfileBitmap", "Contact name is empty for number: " + name);
                // Handle the case where the name is a string with emojis
                if (containsEmojis(name)) {
                    return getInitialsBitmap(name, generateRandomDarkColor());
                } else {
                    // Fallback to emoji with random color
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.emoji);
                }
            }
        }
    }

    private boolean containsEmojis(String text) {
        // Check if the text contains emojis
        return text.codePoints().anyMatch(Character::isSupplementaryCodePoint);
    }

    private Bitmap getInitialsBitmap(String name, int color) {
        String initials = name.isEmpty() ? "" : name.substring(0, 1).toUpperCase();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(70);
        paint.setTypeface(Typeface.DEFAULT);
        int width = 120;
        int height = 120;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, paint);
        paint.setColor(Color.BLACK);
        canvas.drawText(initials, width / 2f, height / 2f - ((paint.descent() + paint.ascent()) / 2), paint);
        return bitmap;
    }

    private int generateRandomDarkColor() {
        Random random = new Random();
        int red = random.nextInt(128) + 128;
        int green = random.nextInt(128) + 128;
        int blue = random.nextInt(128) + 128;
        return Color.argb(255, red, green, blue);
    }

    private boolean isEmoji(String text) {
        return text.codePoints().anyMatch(Character::isSupplementaryCodePoint);
    }

    private String getAllMessagesAsString(Sms sms) {
        StringBuilder allMessages = new StringBuilder();
        for (String message : sms.getMessageList()) {
            allMessages.append(message).append("\n");
        }
        return allMessages.toString();
    }
}
