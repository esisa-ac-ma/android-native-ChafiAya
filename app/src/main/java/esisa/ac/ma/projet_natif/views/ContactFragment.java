package esisa.ac.ma.projet_natif.views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import esisa.ac.ma.projet_natif.R;
import esisa.ac.ma.projet_natif.adapters.ContactAdapter;

public class ContactFragment extends Fragment {
    private ActivityResultLauncher<String> launcher;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.contact_recycler);

        contactAdapter = new ContactAdapter(requireContext());

        launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        loadContacts(); // Load contacts if permission is granted
                    } else {
                        // Permission denied, show a toast message or take appropriate action
                        Toast.makeText(requireContext(), "Permission denied, cannot access contacts", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Check if permission is already granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            loadContacts(); // Load contacts if permission is already granted
        } else {
            // Request permission if not granted
            launcher.launch(Manifest.permission.READ_CONTACTS);
        }
    }

    private void loadContacts() {
        recyclerView.setAdapter(contactAdapter);
        DividerItemDecoration dividerItem = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItem);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }


    public void filterContacts(String query) {
        if (contactAdapter != null) {
            contactAdapter.filterContacts(query);
        }
    }
}
