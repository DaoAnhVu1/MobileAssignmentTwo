package vn.daoanhvu.assignmenttwo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import vn.daoanhvu.assignmenttwo.R;
import vn.daoanhvu.assignmenttwo.model.Site;
import vn.daoanhvu.assignmenttwo.model.User;

public class UserListAdapter extends BaseAdapter {
    private List<User> userList;

    public UserListAdapter(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.user_list_item, parent, false);
        }

        User user = (User) userList.get(position);

        ((TextView) convertView.findViewById(R.id.name)).setText(user.getUsername());
        ((TextView) convertView.findViewById(R.id.email)).setText(user.getEmail());
        ((TextView) convertView.findViewById(R.id.index)).setText(Integer.toString(position + 1));
        return convertView;
    }
}
