package vershitsky.kirill.myapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import vershitsky.kirill.myapp.SaveData.VolleySingleton;

/**
 * Created by Вершицкий on 14.03.2015.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<AppUser> users;
    private int lastPosition = -1;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView nameTextView;
        private TextView dateTextView;
        private RelativeLayout container;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.circle_image_view);
            nameTextView = (TextView) v.findViewById(R.id.name_textView);
            dateTextView = (TextView) v.findViewById(R.id.date_textView);
            container = (RelativeLayout) v.findViewById(R.id.rec_view_item_container);
        }
    }

    public MyAdapter(ArrayList<AppUser> users) {
        this.users = users;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageBitmap(users.get(position).getUserPhoto());
        holder.nameTextView.setText(users.get(position).getFirstName() + " " + users.get(position).getLastName());
        if(users.get(position).getStringDateBirthday() != null)
            holder.dateTextView.setText(users.get(position).getStringDateBirthday());
        setAnimation(holder.container, position);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.container.clearAnimation();
    }
}
