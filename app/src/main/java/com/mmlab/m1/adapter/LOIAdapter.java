package com.mmlab.m1.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mmlab.m1.LOIActivity;
import com.mmlab.m1.R;
import com.mmlab.m1.model.LOIModel;
import com.mmlab.m1.network.ProxyService;
import com.mmlab.m1.service.HttpAsyncTask;

import java.util.ArrayList;


public class LOIAdapter extends RecyclerView.Adapter<LOIAdapter.LOIViewHolder> {

	private Context mContext;
	private final LayoutInflater mInflater;
	private final ArrayList<LOIModel> mModels;
	private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
	private TextDrawable.IBuilder mDrawableBuilder;
	private ProxyService proxyService;
	private LOIModel myLOIModel;
	private String choose_ID;

	public LOIAdapter(Context context, ArrayList<LOIModel> models,ProxyService server) {
		mInflater = LayoutInflater.from(context);
		mModels = new ArrayList<>(models);
		mContext = context;
		proxyService = server;
	}

	@Override
	public LOIViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		return new LOIViewHolder(LayoutInflater.from(parent.getContext()).inflate(
				R.layout.list_item, parent, false));
	}

	@Override
	public void onBindViewHolder(LOIViewHolder holder, final int position) {
		final LOIModel model = mModels.get(position);
		mDrawableBuilder = TextDrawable.builder()
				.round();
//		TextDrawable drawable = mDrawableBuilder.build(String.valueOf(model.getLOIName().charAt(0)), mColorGenerator.getColor(position+1));
		String identifier;
		TextDrawable drawable,drawable1;
		if(model.getIdentifier().equals("expert")) {
			identifier = mContext.getString(R.string.expert);
			drawable = mDrawableBuilder.build(String.valueOf(identifier.charAt(0)), mContext.getResources().getColor(R.color.md_indigo_500));

		}
		else if(model.getIdentifier().equals("user")) {
			identifier = mContext.getString(R.string.user);
			drawable = mDrawableBuilder.build(String.valueOf(identifier.charAt(0)), mContext.getResources().getColor(R.color.md_orange_500));

		}
		else if(model.getIdentifier().equals("docent")) {
			identifier = mContext.getString(R.string.narrator);
			drawable = mDrawableBuilder.build(String.valueOf(identifier.charAt(0)), mContext.getResources().getColor(R.color.md_purple_500));
		}
		else {
			drawable = mDrawableBuilder.build(String.valueOf(model.getLOIName().charAt(0)), mColorGenerator.getColor(model.getLOIName().charAt(0)));
		}
        drawable1 = mDrawableBuilder.build(String.valueOf("線"), mContext.getResources().getColor(R.color.md_light_blue_A700));

		holder.imageView.setImageDrawable(drawable);
        holder.imageView1.setImageDrawable(drawable1);
		/*holder.mIcon.setImageDrawable(new IconicsDrawable(mContext)
				.icon(FontAwesome.Icon.faw_clock_o)
				.color(mContext.getResources().getColor(R.color.md_blue_grey_500))
				.sizeDp(18));*/
		holder.mName.setText(model.getLOIName());
		holder.mDuration.setText(model.getLOIDuration());
		holder.mInfo.setText(model.getLOIInfo());

		holder.listItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d("model title",model.getmPOIs().get(0).getPOIName());
				proxyService.setLOImodel(model);
				choose_ID = model.getLOIId();
				/*Intent intent = new Intent(view.getContext(), LOIActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable("LOI-Content",model);
				intent.putExtras(bundle);
				view.getContext().startActivity(intent);*/
			}
		});

	}

	public String getposition(){
		return choose_ID;
	}

	public void setLOIModel(LOIModel l){myLOIModel = l;}

	public LOIModel getLOIModel(){return myLOIModel;}

	public ArrayList<LOIModel> getmModels() {return mModels;}

	@Override
	public int getItemCount() {
		return mModels.size();
	}


	public void animateTo(ArrayList<LOIModel> models) {
		applyAndAnimateRemovals(models);
		applyAndAnimateAdditions(models);
		applyAndAnimateMovedItems(models);
	}

	private void applyAndAnimateRemovals(ArrayList<LOIModel> newModels) {
		for (int i = mModels.size() - 1; i >= 0; i--) {
			final LOIModel model = mModels.get(i);
			if (!newModels.contains(model)) {
				removeItem(i);
			}
		}
	}

	private void applyAndAnimateAdditions(ArrayList<LOIModel> newModels) {
		for (int i = 0, count = newModels.size(); i < count; i++) {
			final LOIModel model = newModels.get(i);
			if (!mModels.contains(model)) {
				addItem(i, model);
			}
		}
	}

	private void applyAndAnimateMovedItems(ArrayList<LOIModel> newModels) {
		for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
			final LOIModel model = newModels.get(toPosition);
			final int fromPosition = mModels.indexOf(model);
			if (fromPosition >= 0 && fromPosition != toPosition) {
				moveItem(fromPosition, toPosition);
			}
		}
	}

	public LOIModel removeItem(int position) {
		final LOIModel model = mModels.remove(position);
		notifyItemRemoved(position);
		return model;
	}

	public void addItem(int position, LOIModel model) {
		mModels.add(position, model);
		notifyItemInserted(position);
	}

	public void moveItem(int fromPosition, int toPosition) {
		final LOIModel model = mModels.remove(fromPosition);
		mModels.add(toPosition, model);
		notifyItemMoved(fromPosition, toPosition);
	}



	public static class LOIViewHolder extends RecyclerView.ViewHolder {

		private final ImageView mIcon;
		private TextView mDuration;
		private TextView mName;
		private LinearLayout listItem;
		private ImageView imageView,imageView1;
		private TextView mInfo;

		public LOIViewHolder(final View itemView) {
			super(itemView);
			listItem = (LinearLayout) itemView.findViewById(R.id.list_item);
			mName = (TextView) itemView.findViewById(R.id.titleTextView);
			mInfo = (TextView) itemView.findViewById(R.id.infoTextView);
			mDuration = (TextView) itemView.findViewById(R.id.captionTextView);
			imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView1 = (ImageView) itemView.findViewById(R.id.imageView1);
			mIcon = (ImageView) itemView.findViewById(R.id.captionIcon4);
		}

	}

}
