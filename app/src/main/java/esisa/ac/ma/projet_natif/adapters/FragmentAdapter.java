package esisa.ac.ma.projet_natif.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import esisa.ac.ma.projet_natif.views.CallFragment;
import esisa.ac.ma.projet_natif.views.ContactFragment;
import esisa.ac.ma.projet_natif.views.FavoriteFragment;
import esisa.ac.ma.projet_natif.views.SmsFragment;

public class FragmentAdapter extends FragmentStateAdapter {
    private ContactFragment contactFragment;
    private SmsFragment smsFragment;
    private CallFragment callFragment;
    private FavoriteFragment favouriteFragment;

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                if (contactFragment == null) {
                    contactFragment = new ContactFragment();
                }
                return contactFragment;
            case 1:
                if (smsFragment == null) {
                    smsFragment = new SmsFragment();
                }
                return smsFragment;
            case 2:
                if (callFragment == null) {
                    callFragment = new CallFragment();
                }
                return callFragment;
            case 3:
                if (favouriteFragment == null) {
                    favouriteFragment = new FavoriteFragment();
                }
                return favouriteFragment;
            default:
                return null;
        }
    }

    public void filterFragments(String query) {
        if (contactFragment != null) {
            contactFragment.filterContacts(query);
        }
        if (smsFragment != null) {
        smsFragment.filterSms(query);
        }
        if (callFragment != null) {
            callFragment.filterCalls(query);
        }
        if (favouriteFragment != null) {
         favouriteFragment.filterFavourites(query);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
