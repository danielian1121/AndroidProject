package com.mmlab.m1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mmlab.m1.MainActivity;
import com.mmlab.m1.R;
import com.mmlab.m1.model.POIModel;
import com.mmlab.m1.network.ProxyService;
import com.rey.material.widget.Spinner;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.client.IO;

import java.util.ArrayList;


public class POIAdapter extends RecyclerView.Adapter<POIAdapter.POIViewHolder> {

    private String mStatus;
    private ProxyService proxyService;
    private Context mContext;
    private final LayoutInflater mInflater;
    private ArrayList<POIModel> mModels = new ArrayList<>();
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
    private ArrayList<POIModel> mOriginModels ;
    private int mposition = -1;

    public POIAdapter(Context context, ProxyService service, String status) {
        mInflater = LayoutInflater.from(context);
        proxyService = service;
        mOriginModels = service.getPOIList();
        for(POIModel pm :mOriginModels)
            mModels.add(new POIModel(pm));
        mContext = context;
        mStatus = status;

    }
    public POIAdapter(Context context, ArrayList<POIModel> models,ProxyService server) {
        mInflater = LayoutInflater.from(context);
        mModels = new ArrayList<>(models);
        mContext = context;
        proxyService = server;
    }

    @Override
    public POIViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item, parent, false);
        return new POIViewHolder(view);
        /*return new POIViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item, parent, false));*/
    }
    @Override
    public void onBindViewHolder(POIViewHolder holder, final int position) {
        final POIModel model = mModels.get(position);
        mDrawableBuilder = TextDrawable.builder()
                .round();
        String identifier, status = "";
        TextDrawable drawable = null,drawable1;
        if(mStatus!=null) {
            if (mStatus.equals("MyPOI")) {
                if (model.getOpen().equals("true")) {
                    status = mContext.getResources().getString(R.string.open);
                    drawable = mDrawableBuilder.build(String.valueOf(status.charAt(0)), mContext.getResources().getColor(R.color.md_cyan_700));
                } else {
                    status = mContext.getResources().getString(R.string.close);
                    drawable = mDrawableBuilder.build(String.valueOf(status.charAt(0)), mContext.getResources().getColor(R.color.md_orange_400));
                }
            }
            else {

                if (model.getIdentifier().equals("expert")) {
                    identifier = mContext.getString(R.string.expert);
                    drawable = mDrawableBuilder.build(String.valueOf(identifier.charAt(0)), mContext.getResources().getColor(R.color.md_indigo_500));

                } else if (model.getIdentifier().equals("user")) {
                    identifier = mContext.getString(R.string.user);
                    drawable = mDrawableBuilder.build(String.valueOf(identifier.charAt(0)), mContext.getResources().getColor(R.color.md_orange_500));

                } else {
                    identifier = mContext.getString(R.string.narrator);
                    drawable = mDrawableBuilder.build(String.valueOf(identifier.charAt(0)), mContext.getResources().getColor(R.color.md_purple_500));
                }
            }
        }
        else {

            if (model.getIdentifier().equals("expert")) {
                identifier = mContext.getString(R.string.expert);
                drawable = mDrawableBuilder.build(String.valueOf(identifier.charAt(0)), mContext.getResources().getColor(R.color.md_indigo_500));

            } else if (model.getIdentifier().equals("user")) {
                identifier = mContext.getString(R.string.user);
                drawable = mDrawableBuilder.build(String.valueOf(identifier.charAt(0)), mContext.getResources().getColor(R.color.md_orange_500));

            } else {
                identifier = mContext.getString(R.string.narrator);
                drawable = mDrawableBuilder.build(String.valueOf(identifier.charAt(0)), mContext.getResources().getColor(R.color.md_purple_500));
            }
        }
        drawable1 = mDrawableBuilder.build(String.valueOf("點"), mContext.getResources().getColor(R.color.md_light_blue_A700));


        holder.imageView.setImageDrawable(drawable);
        holder.imageView1.setImageDrawable(drawable1);
        holder.mIcon1.setVisibility(View.GONE);
        holder.mIcon2.setVisibility(View.GONE);
        holder.mIcon3.setVisibility(View.GONE);
        holder.mIcon4.setVisibility(View.GONE);

        if(!model.getMedia().isEmpty()) {
            for (String media_format : model.getMedia()) {
                switch (media_format) {
                    case "photo":
                        holder.mIcon1.setVisibility(View.VISIBLE);
                        holder.mIcon1.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(GoogleMaterial.Icon.gmd_image)
                                .color(mContext.getResources().getColor(R.color.md_blue_grey_500))
                                .sizeDp(18));
                        break;
                    case "audio":
                        holder.mIcon2.setVisibility(View.VISIBLE);
                        holder.mIcon2.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(GoogleMaterial.Icon.gmd_volume_up)
                                .color(mContext.getResources().getColor(R.color.md_blue_grey_500))
                                .sizeDp(18));
                        break;
                    case "movie":
                        holder.mIcon3.setVisibility(View.VISIBLE);
                        holder.mIcon3.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(GoogleMaterial.Icon.gmd_videocam)
                                .color(mContext.getResources().getColor(R.color.md_blue_grey_500))
                                .sizeDp(18));
                        break;
                    case "audio tour":
                        holder.mIcon4.setVisibility(View.VISIBLE);
                        holder.mIcon4.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(GoogleMaterial.Icon.gmd_record_voice_over)
                                .color(mContext.getResources().getColor(R.color.md_blue_grey_500))
                                .sizeDp(18));
                        break;
                }
            }

        }
        else {
            holder.mIcon4.setVisibility(View.VISIBLE);
            holder.mIcon4.setImageDrawable(new IconicsDrawable(mContext)
                    .icon(GoogleMaterial.Icon.gmd_place)
                    .color(mContext.getResources().getColor(R.color.md_blue_grey_500))
                    .sizeDp(18));
        }

        holder.poiTitle.setText(model.getPOIName());
        holder.poiInfo.setText(model.getPOIInfo());
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (proxyService != null) {
                    //Log.d("Contributor", model.getContributor());
                        proxyService.sendSinglePOI(model);
                        proxyService.setPOImodel(model);
                        mposition = position;
                        //onItemClickListener.OnItemClick(position);
                        /*
                        Intent intent = new Intent(view.getContext(), POIActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("POI-Content", model);
                        bundle.putString("type", "Normal");
                        intent.putExtras(bundle);
                        view.getContext().startActivity(intent);*/

                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public ArrayList<POIModel> getPOIList(){return mModels;}

    public int getposition(){
        return mposition;
    }

    public void setposition(int p){mposition = p;}

    public ArrayList<POIModel> animateTo(ArrayList<POIModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
        return mModels;
    }

    private void applyAndAnimateRemovals(ArrayList<POIModel> newModels) {
        for (int i = mModels.size() - 1; i >= 0; i--) {
            final POIModel model = mModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(ArrayList<POIModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final POIModel model = newModels.get(i);
            if (!mModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(ArrayList<POIModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final POIModel model = newModels.get(toPosition);
            final int fromPosition = mModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public POIModel removeItem(int position) {
        final POIModel model = mModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, POIModel model) {
        mModels.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final POIModel model = mModels.remove(fromPosition);
        mModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }


    public static class POIViewHolder extends RecyclerView.ViewHolder {

        private TextView poiTitle;
        private LinearLayout listItem;
        private ImageView imageView,imageView1;
        private TextView poiDistance;
        private TextView poiInfo;
        private ImageView mIcon1;
        private ImageView mIcon2;
        private ImageView mIcon3;
        private ImageView mIcon4;

        public POIViewHolder(final View itemView) {
            super(itemView);
            listItem = (LinearLayout) itemView.findViewById(R.id.list_item);
            poiTitle = (TextView) itemView.findViewById(R.id.titleTextView);
            poiInfo = (TextView) itemView.findViewById(R.id.infoTextView);
            poiDistance = (TextView) itemView.findViewById(R.id.captionTextView);
            poiDistance.setVisibility(View.GONE);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView1 = (ImageView) itemView.findViewById(R.id.imageView1);
            mIcon1 = (ImageView) itemView.findViewById(R.id.captionIcon1);
            mIcon2 = (ImageView) itemView.findViewById(R.id.captionIcon2);
            mIcon3 = (ImageView) itemView.findViewById(R.id.captionIcon3);
            mIcon4 = (ImageView) itemView.findViewById(R.id.captionIcon4);

        }

    }

}
