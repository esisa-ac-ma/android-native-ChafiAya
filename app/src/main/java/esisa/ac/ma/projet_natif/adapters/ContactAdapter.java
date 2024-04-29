package esisa.ac.ma.projet_natif.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import esisa.ac.ma.projet_natif.R;
import esisa.ac.ma.projet_natif.dal.ContactDao;
import esisa.ac.ma.projet_natif.entities.Contact;
import esisa.ac.ma.projet_natif.entities.Favorite;
import esisa.ac.ma.projet_natif.services.ManageFavorite;
import esisa.ac.ma.projet_natif.views.FavoriteFragment;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.Item> {
    private ManageFavorite manageFavorite;
    private List<Contact> model = new ArrayList<>();
    private List<Contact> filteredModel = new ArrayList<>();
    private ContactDao contactDao;
    private static Context context;

    private FavoriteClickListener favoriteClickListener;

    public ContactAdapter(Context context) {
        this.context = context;
        contactDao = new ContactDao(context);
        model.addAll(contactDao.getVcontact());
        filteredModel.addAll(model);
        manageFavorite = new ManageFavorite(context);
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        Contact contact = filteredModel.get(position);
        holder.name.setText(contact.getName());
        holder.date.setText(contact.getDate());
        StringBuilder phones = new StringBuilder();
        for (String p : contact.getPhones()) {
            phones.append(p).append("\n");
        }
        holder.phones.setText(phones);

        List<Favorite> favorites = manageFavorite.getAll();
        boolean isFavorite = false;
        for (Favorite favorite : favorites) {
            if (favorite.getPhone().equals(contact.getPhones().get(0))) {
                isFavorite = true;
                break;
            }
        }
        contact.setFavorite(isFavorite); // Update the favorite status in Contact object

        if (contact.isFavorite()) {
            holder.favoriteButton.setImageResource(R.drawable.favorite);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.favorite_empty);
        }

        if (isEmoji(contact.getName())) {
            // Load the emoji image from drawable and apply random color filter
            holder.photo.setImageResource(R.drawable.emoji);
            holder.photo.setColorFilter(getRandomColor(), PorterDuff.Mode.SRC_ATOP);
        } else {
            // If the contact photo is not null, load it into the ImageView with round effect
            if (contact.getPhotoUri() != null) {
                Picasso.get().load(contact.getPhotoUri()).into(holder.photo);
                // Apply round effect to the ImageView
                holder.photo.setClipToOutline(true);
                holder.photo.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 20);
                    }
                });
            } else {
                // If the contact photo is null, create initials bitmap with random color
                Bitmap initialsBitmap = getInitialsBitmap(contact.getName(), getRandomColor());
                // Set the initials bitmap to the ImageView
                holder.photo.setImageBitmap(initialsBitmap);
                // Apply round effect to the ImageView
                holder.photo.setClipToOutline(true);
                holder.photo.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 20);
                    }
                });
            }
        }

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the favorite status of the contact
                boolean isFavorite = false;
                for (Favorite favorite : favorites) {
                    if (favorite.getPhone().equals(contact.getPhones().get(0))) {
                        isFavorite = true;
                        break;
                    }
                }
                if (!isFavorite) {
                    holder.favoriteButton.setImageResource(R.drawable.favorite);
                    // Add the contact to favorites using ManageFavorite service
                    manageFavorite.add(convertFromContactToFavorite(contact));
                } else {
                    holder.favoriteButton.setImageResource(R.drawable.favorite_empty);
                    // Remove the contact from favorites using ManageFavorite service
                    manageFavorite.delete(convertFromContactToFavorite(contact));
                }

                // Update the favorite status in Contact object
                contact.setFavorite(!isFavorite);

                if (favoriteClickListener != null) {
                    favoriteClickListener.onFavoriteClick(contact);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return filteredModel.size();
    }

    public void filterContacts(String query) {
        query = query.toLowerCase();
        List<Contact> filteredContacts = new ArrayList<>();
        if (query.isEmpty()) {
            filteredContacts.addAll(model);
        } else {
            for (Contact contact : model) {
                if (contact.getName().toLowerCase().contains(query)) {
                    filteredContacts.add(contact);
                }
            }
        }

        filteredModel = filteredContacts;
        notifyDataSetChanged();
    }

    private boolean isEmoji(String text) {
        return text.length() == 1 && text.codePoints().anyMatch(Character::isSupplementaryCodePoint) && !text.matches("\\d+");
    }

    public void setFavoriteClickListener(FavoriteClickListener listener) {
        this.favoriteClickListener = listener;
    }

    public void onFavoriteClick(Contact contact) {
        if (favoriteClickListener != null) {
            favoriteClickListener.onFavoriteClick(contact);
        }
    }

    private Favorite convertFromContactToFavorite(Contact contact) {
        Favorite favorite = new Favorite();
        favorite.setName(contact.getName());
        favorite.setPhone(contact.getPhones().get(0));
        return favorite;
    }

    class Item extends RecyclerView.ViewHolder {
        TextView name;
        TextView date;
        TextView phones;
        ImageView photo;
        ImageButton favoriteButton;

        public Item(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            phones = itemView.findViewById(R.id.phones);
            photo = itemView.findViewById(R.id.photo);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }
    }

    private int getRandomColor() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private Bitmap getInitialsBitmap(String name, int color) {
        String initials = name.substring(0, 1).toUpperCase();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(48);
        paint.setTypeface(Typeface.create("YourCustomFont", Typeface.NORMAL)); // Set the custom font

        int width = 150; // Adjust the width and height as per your requirement
        int height = 150;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, paint);
        paint.setColor(Color.BLACK); // Set the text color to black
        canvas.drawText(initials, width / 2f, height / 2f - ((paint.descent() + paint.ascent()) / 2), paint);
        return bitmap;
    }

    public interface FavoriteClickListener {
        void onFavoriteClick(Contact contact);
    }

}
