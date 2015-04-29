package vershitsky.kirill.myapp;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Вершицкий on 20.04.2015.
 */
public class NavDrawAdapter extends RecyclerView.Adapter<NavDrawAdapter.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private AppUser user;
    private String[] items;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_nav_drawer, parent, false);
            ViewHolder vhItem = new ViewHolder(v, viewType);
            return vhItem;
        }
        else if(viewType == TYPE_HEADER){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_nav_drawer, parent, false);
            ViewHolder vhItem = new ViewHolder(v, viewType);
            return vhItem;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(holder.HolderId == TYPE_HEADER){
            holder.userPhoto.setImageBitmap(user.getUserPhoto());
            holder.userName.setText(user.getFirstName() + " " + user.getLastName());
        }
        else{
            holder.rowText.setText(items[position - 1]);
        }

    }

    @Override
    public int getItemCount() {
        return items.length + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int HolderId;
        CircleImageView userPhoto;
        TextView rowText;
        TextView userName;
        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_HEADER){
                userPhoto = (CircleImageView) itemView.findViewById(R.id.circle_user_photo);
                userName = (TextView) itemView.findViewById(R.id.name);
                HolderId = TYPE_HEADER;
            }
            else{
                rowText = (TextView) itemView.findViewById(R.id.rowText);
                HolderId = TYPE_ITEM;
            }
        }
    }
    public NavDrawAdapter(AppUser user, String[] items){
        this.user = user;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position)){
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    public boolean isPositionHeader(int position){
        return position == TYPE_HEADER;
    }
}
