package esisa.ac.ma.projet_natif.dal;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Vector;

import esisa.ac.ma.projet_natif.entities.Contact;

public class ContactDao {
    private final Vector<Contact> vcontact = new Vector<>();
    private final Context ctx;
    private SimpleDateFormat simpleDateFormat;

    public ContactDao(Context ctx) {
        this.ctx = ctx;
        load();
    }

    public Vector<Contact> getVcontact() {
        return vcontact;
    }

    public void load() {
        // Check if the app has permission to read contacts
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            try (Cursor contacts = new CursorLoader(ctx, Contacts.CONTENT_URI, null,
                    null, null, null).loadInBackground()) {
                Contact c;
                if (contacts != null && contacts.getCount() != 0) {
                    int displayNameIndex = contacts.getColumnIndex(Contacts.DISPLAY_NAME);
                    int idIndex = contacts.getColumnIndex(Contacts._ID);
                    int lastUpdatedIndex = contacts.getColumnIndex(Contacts.CONTACT_LAST_UPDATED_TIMESTAMP);
                    int photoIndex = contacts.getColumnIndex(Photo.PHOTO_URI);
                    while (contacts.moveToNext()) {
                        // Check if column indices are valid
                        if (displayNameIndex != -1 && idIndex != -1 && lastUpdatedIndex != -1) {
                            String name = contacts.getString(displayNameIndex);
                            if (name != null && !name.isEmpty()) {
                                // Skip contacts with null or empty names
                                c = new Contact();
                                String id = contacts.getString(idIndex);
                                long date = contacts.getLong(lastUpdatedIndex);
                                c.setName(name);
                                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                c.setDate(simpleDateFormat.format(new Date(date)));
                                Vector<String> phones = getPhones(id);
                                // Remove duplicates and validate phone numbers
                                HashSet<String> uniquePhones = removeDuplicates(phones);
                                HashSet<String> validPhones = validatePhones(uniquePhones);
                                c.setPhones(new Vector<>(validPhones));
                                c.setEmails(getEmails(id));
                                String photoUri = contacts.getString(photoIndex);
                                c.setPhotoUri(photoUri);
                                // Check if the contact has at least one valid phone number
                                if (!validPhones.isEmpty()) {
                                    vcontact.add(c);
                                }
                            }
                        } else {
                            // Log a warning or handle the situation where column indices are invalid
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Remove duplicates from phone numbers after cleaning
    private HashSet<String> removeDuplicates(Vector<String> phones) {
        HashSet<String> cleanedPhones = new HashSet<>();
        for (String phone : phones) {
            // Remove special characters and spaces (except at the beginning or after '+')
            String cleanedPhone = phone.replaceAll("(?<!^|\\+)\\D", "");
            cleanedPhones.add(cleanedPhone);
        }
        return cleanedPhones;
    }

    // Validate Phone Numbers
    private HashSet<String> validatePhones(HashSet<String> phones) {
        HashSet<String> validPhones = new HashSet<>();
        for (String phone : phones) {
            if (isValidMobile(phone)) {
                validPhones.add(phone);
            }
        }
        return validPhones;
    }

    // Validate Phone Number
    private boolean isValidMobile(String phone) {
        // Check if the phone number matches the general phone number pattern
        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            return false; // If it doesn't match, return false
        }
        // Custom validation: Check if the phone number has at least 7 digits (adjust as needed)
        if (phone.replaceAll("\\D", "").length() < 7) {
            return false; // If it has less than 7 digits, return false
        }
        return true;
    }

    public Vector<String> getPhones(String contact_id) {
        Vector<String> phones = new Vector<>();
        String[] projection = {Phone.NUMBER};
        String selection = Phone.CONTACT_ID + " = ?";
        String[] selectionArgs = {contact_id};

        try (Cursor cursor = ctx.getContentResolver().query(Phone.CONTENT_URI,
                projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    phones.add(cursor.getString(0));
                }
            }
        }
        return phones;
    }

    // Validate Email
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public Vector<String> getEmails(String contact_id) {
        Vector<String> emails = new Vector<>();
        String[] projection = {Email.ADDRESS};
        String selection = Email.CONTACT_ID + " = ?";
        String[] selectionArgs = {contact_id};

        try (Cursor cursor = ctx.getContentResolver().query(Email.CONTENT_URI,
                projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    emails.add(cursor.getString(0));
                }
            }
        }
        return emails;
    }
}
