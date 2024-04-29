package esisa.ac.ma.projet_natif;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.FirebaseApp;
import java.util.List;
import esisa.ac.ma.projet_natif.adapters.FragmentAdapter;
import esisa.ac.ma.projet_natif.dal.CallDao;
import esisa.ac.ma.projet_natif.dal.ContactDao;
import esisa.ac.ma.projet_natif.dal.SmsDao;
import esisa.ac.ma.projet_natif.databinding.ActivityMainBinding;
import esisa.ac.ma.projet_natif.entities.Call;
import esisa.ac.ma.projet_natif.entities.Contact;
import esisa.ac.ma.projet_natif.entities.Favorite;
import esisa.ac.ma.projet_natif.entities.Sms;
import esisa.ac.ma.projet_natif.services.FirebaseManager;
import esisa.ac.ma.projet_natif.services.ManageFavorite;
import esisa.ac.ma.projet_natif.views.CallFragment;
import esisa.ac.ma.projet_natif.views.ContactFragment;
import esisa.ac.ma.projet_natif.views.FavoriteFragment;
import esisa.ac.ma.projet_natif.views.SmsFragment;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private SearchView searchView;
    private FragmentAdapter fragmentAdapter;
    private TabLayout tabLayout;
    private String[] titles;
    private ActivityMainBinding binding;
    private ManageFavorite manageFavorite;
    private FirebaseManager firebaseManager;
    private CallDao callDao;
    private ContactDao contactDao;
    private SmsDao  smsDao;
    static final int[] icon_id = {R.drawable.contact, R.drawable.sms, R.drawable.call_log, R.drawable.favorite};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FirebaseApp.initializeApp(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        manageFavorite = new ManageFavorite(this);
        firebaseManager = new FirebaseManager();
        callDao = new CallDao(this);
        contactDao = new ContactDao(this);
        smsDao = new SmsDao(this);
        viewPager = binding.viewPager;
        tabLayout = binding.tableLayout;
        searchView = binding.searchView;
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        fragmentAdapter = new FragmentAdapter(this);
        viewPager.setAdapter(fragmentAdapter);
        titles = getResources().getStringArray(R.array.titles);
        init();

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int i) {
                tab.setText(titles[i]);
                tab.setIcon(icon_id[i]);
            }
        }).attach();

        binding.exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportCallsSmsContactsFavoriteToFirebase();
            }
        });

        Log.d("room_data_test", manageFavorite.getAll().toString());
    }

    private void filterList(String newText) {
        fragmentAdapter.filterFragments(newText);
    }

    public void init() {
        List<Favorite> results = manageFavorite.getAll();
        if (results.isEmpty()) {
            Favorite favorite = new Favorite();
            favorite.setPhone("0600000000");
            favorite.setName("contact_0");
            manageFavorite.add(favorite);
        }
    }

    private void exportCallsSmsContactsFavoriteToFirebase() {
        List<Contact> contacts = contactDao.getVcontact();
        List<Call> calls = callDao.getCalls();
        List<Sms> smsList = smsDao.getSMSList();
        List<Favorite> favorites = manageFavorite.getAll();
        // Export the data to Firebase
        firebaseManager.uploadContacts(contacts);
        firebaseManager.uploadCalls(calls);
        firebaseManager.uploadSMS(smsList);
        firebaseManager.uploadFavorites(favorites);
    }
}
