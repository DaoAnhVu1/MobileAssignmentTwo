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

public class SiteAdapter extends BaseAdapter {
    private List<Site> siteList;

    public SiteAdapter(List<Site> siteList) {
        this.siteList = siteList;
    }

    @Override
    public int getCount() {
        return siteList.size();
    }

    @Override
    public Object getItem(int position) {
        return siteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.site_list, parent, false);
        }

        Site site = siteList.get(position);

        ((TextView) convertView.findViewById(R.id.name)).setText("Name: " + site.getName());
        ((TextView) convertView.findViewById(R.id.date)).setText("Date: " + site.getDate());
        ((TextView) convertView.findViewById(R.id.time)).setText("Time: " + site.getTime());
        ShapeableImageView imageView = convertView.findViewById(R.id.image);
        Picasso.get().load(site.getImageUrl()).into(imageView);
        String address = site.getAddress();
        if (address.length() > 20) {
            address = address.substring(0, 20) + "...";
        }
        ((TextView) convertView.findViewById(R.id.address)).setText("Address: " + address);

        return convertView;
    }
}
