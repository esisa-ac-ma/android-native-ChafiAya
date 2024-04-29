package esisa.ac.ma.projet_natif.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import esisa.ac.ma.projet_natif.entities.Call;
import esisa.ac.ma.projet_natif.entities.Contact;
import esisa.ac.ma.projet_natif.entities.Favorite;
import esisa.ac.ma.projet_natif.entities.Sms;

public class FirebaseManager {
    private DatabaseReference contactsRef;
    private DatabaseReference smsRef;
    private DatabaseReference callsRef;
    private DatabaseReference favoritesRef;

    public FirebaseManager() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        contactsRef = database.getReference("contacts");
        smsRef = database.getReference("sms");
        favoritesRef = database.getReference("favorites");
        callsRef = database.getReference("calls");
    }

    public void uploadContacts(List<Contact> contacts) {
        for (Contact contact : contacts) {
            contactsRef.push().setValue(contact);
        }
    }

    public void uploadCalls(List<Call> calls) {
        for (Call call : calls) {
            callsRef.push().setValue(call);
        }
    }

    public void uploadSMS(List<Sms> smsList) {
        for (Sms sms : smsList) {
            smsRef.push().setValue(sms);
        }
    }

    public void uploadFavorites(List<Favorite> favorites) {
        for (Favorite favorite : favorites) {
            favoritesRef.push().setValue(favorite);
        }
    }
}
