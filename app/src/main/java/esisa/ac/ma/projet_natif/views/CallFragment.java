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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import esisa.ac.ma.projet_natif.R;
import esisa.ac.ma.projet_natif.adapters.CallAdapter;
import esisa.ac.ma.projet_natif.dal.CallDao;
import esisa.ac.ma.projet_natif.entities.Call;

public class CallFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private ActivityResultLauncher<String> launcher;
    private RecyclerView recyclerView;
    private CallAdapter callAdapter;
    private List<Call> callList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_call, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.call_recycler);

        launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        displayCallLogs();
                    }
                }
        );

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            displayCallLogs();
        } else {
            launcher.launch(Manifest.permission.READ_CALL_LOG);
        }
    }

    private void displayCallLogs() {
        CallDao callDao = new CallDao(requireContext());
        Map<String, Call> latestCallsMap = new HashMap<>();
        Map<String, List<Call>> groupedCallsMap = new HashMap<>();
        this.callList = callDao.getCalls(); // Update the class-level variable

        // Add only the latest call for each contact to the latestCallsMap
        for (Call call : callList) {
            if (!latestCallsMap.containsKey(call.getTitle())) {
                latestCallsMap.put(call.getTitle(), call);
            }
        }

        // Group the calls by title and add them to the groupedCallsMap
        Map<String, List<Call>> tempGroupedCallsMap = new HashMap<>();
        for (Call call : callList) {
            List<Call> callsForTitle = tempGroupedCallsMap.computeIfAbsent(call.getTitle(), k -> new ArrayList<>());
            callsForTitle.add(call);
        }
        groupedCallsMap.putAll(tempGroupedCallsMap);
        callAdapter = new CallAdapter(requireContext(), latestCallsMap, groupedCallsMap);
        recyclerView.setAdapter(callAdapter);
        DividerItemDecoration dividerItem = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItem);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    public void filterCalls(String query) {
        callAdapter.filterList2(query);
    }
}
