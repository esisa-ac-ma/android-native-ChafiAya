package esisa.ac.ma.projet_natif.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import esisa.ac.ma.projet_natif.R;
import esisa.ac.ma.projet_natif.adapters.FavoriteAdapter;
import esisa.ac.ma.projet_natif.entities.Favorite;
import esisa.ac.ma.projet_natif.services.ManageFavorite;

public class FavoriteFragment extends Fragment implements FavoriteAdapter.FavoriteClickListener {
    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;
    private ManageFavorite manageFavorite;
    private List<Favorite> favorites;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.favorite_recycler);

        // Initialize and set up RecyclerView with the list of favorites
        manageFavorite = new ManageFavorite(requireContext());
        loadFavorites(); // Load favorites initially
        favoriteAdapter = new FavoriteAdapter(favorites, requireContext());
        recyclerView.setAdapter(favoriteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));

        // Set the click listener to the adapter
        favoriteAdapter.setFavoriteClickListener(this);

        // Enable swipe to delete
        favoriteAdapter.enableSwipeToDelete(recyclerView);
    }

    private void loadFavorites() {
        favorites = manageFavorite.getAll();
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
        if (favoriteAdapter != null) {
            favoriteAdapter.updateFavorites(favorites);
            // Notify the RecyclerView that the dataset has changed
            favoriteAdapter.notifyDataSetChanged();
        }
    }

    public void filterFavourites(String query) {
        if (favoriteAdapter != null) {
            favoriteAdapter.filterFavorites(query);
        }
    }

    @Override
    public void onDeleteClick(Favorite favorite) {
        // Delete the favorite from the database
        manageFavorite.delete(favorite);
        // Reload the favorites after deletion
        loadFavorites();
        // Update the adapter
        favoriteAdapter.updateFavorites(favorites);
    }
}
