package com.karim.ater.myexpenses.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.Helpers.IconsUtility;
import com.karim.ater.myexpenses.Helpers.Utils;
import com.karim.ater.myexpenses.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

public class IconsFragment extends Fragment {
    View view;
    RecyclerView iconsRv;
    FragmentActivity activity;
    ArrayList<String> iconNames;
    Fragment parentFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = getActivity();
        parentFragment = activity.getSupportFragmentManager().findFragmentByTag("CategoryAdderFragment");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_icons, container, false);
            iconsRv = view.findViewById(R.id.iconsRv);
            iconsRv.setLayoutManager(new GridLayoutManager(getContext(), Utils.calculateNoOfColumns(getContext(), 100)));
            iconsRv.setAdapter(new IconsAdapter());
        }
        return view;
    }

    public class IconsAdapter extends RecyclerView.Adapter<IconsViewHolder> {

        public IconsAdapter() {
            iconNames = new ArrayList<>();
            listAssetFiles("icons");

        }

        @NonNull
        @Override
        public IconsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.icon_item, parent, false);
            return new IconsFragment.IconsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull IconsViewHolder holder, final int position) {
            IconsUtility iconsUtility = new IconsUtility(activity);
            final Drawable drawable = iconsUtility.getIcon(iconNames.get(position));
            holder.iconIv.setImageDrawable(drawable);
            holder.iconIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CategoryAdderFragment) parentFragment).changeImage(drawable);
                    ((CategoryAdderFragment) parentFragment).changeImageName(iconNames.get(position));
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
            });
        }

        @Override
        public int getItemCount() {
            return iconNames.size();
        }
    }

    private class IconsViewHolder extends RecyclerView.ViewHolder {
        ImageView iconIv;

        IconsViewHolder(View itemView) {
            super(itemView);
            iconIv = itemView.findViewById(R.id.iconIv);
        }
    }

    private boolean listAssetFiles(String path) {

        String[] list;
        try {
            list = activity.getAssets().list(path);
            if (list != null) {
                if (list.length > 0) {
                    // This is a folder
                    for (String file : list) {
                        if (!listAssetFiles(path + "/" + file))
                            return false;
                        else {
                            // This is a file
                            // TODO: add file name to an array list
                            iconNames.add(file);
                        }
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }


}
