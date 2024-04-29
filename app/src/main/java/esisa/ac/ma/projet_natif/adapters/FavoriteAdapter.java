package esisa.ac.ma.projet_natif.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import esisa.ac.ma.projet_natif.R;
import esisa.ac.ma.projet_natif.entities.Favorite;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.Item> {
    private List<Favorite> favorites;
    private List<Favorite> filteredFavorites;
    private FavoriteClickListener favoriteClickListener;
    private Context context;

    public FavoriteAdapter(List<Favorite> favorites, Context context) {
        this.favorites = favorites != null ? favorites : new ArrayList<>(); // Ensure favorites list is not null
        this.filteredFavorites = new ArrayList<>(this.favorites);
        this.context = context;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        Favorite favorite = filteredFavorites.get(position);
        holder.name.setText(favorite.getName());
        holder.phone.setText(favorite.getPhone());


    }

    @Override
    public int getItemCount() {
        return filteredFavorites.size();
    }

    public void updateFavorites(List<Favorite> newFavorites) {
        favorites.clear();
        filteredFavorites.clear();
        favorites.addAll(newFavorites);
        filteredFavorites.addAll(newFavorites);
        notifyDataSetChanged();
    }

    public void setFavoriteClickListener(FavoriteClickListener listener) {
        this.favoriteClickListener = listener;
    }

    public void filterFavorites(String query) {
        query = query.toLowerCase();
        filteredFavorites.clear();
        if (query.isEmpty()) {
            filteredFavorites.addAll(favorites);
        } else {
            for (Favorite favorite : favorites) {
                // Check if the favorite name contains the query
                if (favorite.getName().toLowerCase().contains(query)) {
                    filteredFavorites.add(favorite); // Add favorites that match the query to the filtered list
                }
            }
        }
        notifyDataSetChanged(); // Notify RecyclerView about the data change
    }

    static class Item extends RecyclerView.ViewHolder {
        TextView name;
        TextView phone;


        Item(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.favoriteName);
            phone = itemView.findViewById(R.id.favoritePhones);

        }
    }

    public interface FavoriteClickListener {
        void onDeleteClick(Favorite favorite);
    }

    // Swipe to delete functionality
    public void enableSwipeToDelete(RecyclerView recyclerView) {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (favoriteClickListener != null) {
                    favoriteClickListener.onDeleteClick(filteredFavorites.get(position));
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public static abstract class SwipeToDeleteCallback extends ItemTouchHelper.Callback {

        private final ColorDrawable background;
        private final Drawable deleteIcon;

        public SwipeToDeleteCallback(Context context) {
            background = new ColorDrawable(Color.RED);
            deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20; //so background is behind the rounded corners of itemView

            if (dX > 0) { // Swiping to the right
                background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
                deleteIcon.setBounds(itemView.getLeft() + backgroundCornerOffset, itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2, itemView.getLeft() + backgroundCornerOffset + deleteIcon.getIntrinsicWidth(), itemView.getBottom() - (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2);
            } else { // Swiping to the left
                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                deleteIcon.setBounds(itemView.getRight() - backgroundCornerOffset - deleteIcon.getIntrinsicWidth(), itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2, itemView.getRight() - backgroundCornerOffset, itemView.getBottom() - (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2);
            }

            background.draw(c);
            deleteIcon.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }
    }
}
