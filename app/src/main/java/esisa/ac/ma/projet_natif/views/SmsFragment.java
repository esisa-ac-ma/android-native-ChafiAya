package esisa.ac.ma.projet_natif.views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import esisa.ac.ma.projet_natif.R;
import esisa.ac.ma.projet_natif.adapters.SmsAdapter;
import esisa.ac.ma.projet_natif.dal.SmsDao;
import esisa.ac.ma.projet_natif.entities.Sms;

public class SmsFragment extends Fragment {

    private ActivityResultLauncher<String> launcher;
    private RecyclerView recyclerView;
    private SmsAdapter smsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_sms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.sms_recycler);

        launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        loadSMSList();
                    }
                }
        );

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            loadSMSList();
        } else {
            launcher.launch(Manifest.permission.READ_SMS);
        }
    }

    private void loadSMSList() {
        SmsDao smsDao = new SmsDao(requireContext());
        List<Sms> smsList = smsDao.getSMSList();
        smsAdapter = new SmsAdapter(requireActivity());
        smsAdapter.setSMSList(smsList);
        recyclerView.setAdapter(smsAdapter);
        DividerItemDecoration dividerItem = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItem);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
    }

    public void filterSms(String query) {
        if (smsAdapter != null) {
            smsAdapter.filterSMS(query);
            smsAdapter.notifyDataSetChanged();
        }
    }


}
