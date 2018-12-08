package me.nereo.multi_image_selector.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorFragment;
import me.nereo.multi_image_selector.R;
import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.bean.Video;
import me.nereo.multi_image_selector.utils.FileUtils;

/**
 * 视频Adapter
 * Created by Van on 2018/11/29.
 */
public class VideoGridAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private Context mContext;

    private LayoutInflater mInflater;
    private boolean showSelectIndicator = true;

    private List<Video> mVideos = new ArrayList<>();
    private List<Video> mSelectedVideos = new ArrayList<>();

    final int mGridWidth;

    public VideoGridAdapter(Context context,  int column){
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        }else{
            width = wm.getDefaultDisplay().getWidth();
        }
        mGridWidth = width / column;
    }
    /**
     * 显示选择指示器
     * @param b
     */
    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }

    public void setShowCamera(boolean b){
        notifyDataSetChanged();
    }


    /**
     * 选择某个图片，改变选择状态
     * @param video
     */
    public void select(Video video) {
        if(mSelectedVideos.contains(video)){
            mSelectedVideos.remove(video);
        }else{
            mSelectedVideos.add(video);
        }
        notifyDataSetChanged();
    }

    /**
     * 通过图片路径设置默认选择
     * @param resultList
     */
    public void setDefaultSelected(ArrayList<String> resultList) {
        for(String path : resultList){
            Video video = getVideoByPath(path);
            if(video != null){
                mSelectedVideos.add(video);
            }
        }
        if(mSelectedVideos.size() > 0){
            notifyDataSetChanged();
        }
    }

    private Video getVideoByPath(String path){
        if(mVideos != null && mVideos.size()>0){
            for(Video video : mVideos){
                if(video.path.equalsIgnoreCase(path)){
                    return video;
                }
            }
        }
        return null;
    }

    /**
     * 设置数据集
     * @param videos
     */
    public void setData(List<Video> videos) {
        mSelectedVideos.clear();

        if(videos != null && videos.size()>0){
            mVideos = videos;
        }else{
            mVideos.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {

        return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return  mVideos.size();
    }

    @Override
    public Video getItem(int i) {
        return mVideos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if(view == null){
            view = mInflater.inflate(R.layout.mis_list_item_image, viewGroup, false);
            holder = new ViewHolder(view);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        if(holder != null) {
            holder.bindData(getItem(i));
        }

        return view;
    }

    class ViewHolder {
        ImageView image;
        ImageView indicator;
        View mask;

        ViewHolder(View view){
            image = (ImageView) view.findViewById(R.id.image);
            indicator = (ImageView) view.findViewById(R.id.checkmark);
            mask = view.findViewById(R.id.mask);
            view.setTag(this);
        }

        void bindData(final Video video){
            if(video == null) return;
            // 处理单选和多选状态
            if(showSelectIndicator){
                indicator.setVisibility(View.VISIBLE);
                if(mSelectedVideos.contains(video)){
                    // 设置选中状态
                    indicator.setImageResource(R.drawable.mis_btn_selected);
                    mask.setVisibility(View.VISIBLE);
                }else{
                    // 未选择
                    indicator.setImageResource(R.drawable.mis_btn_unselected);
                    mask.setVisibility(View.GONE);
                }
            }else{
                indicator.setVisibility(View.GONE);
            }

//                Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(),
//                        video.getId(), MediaStore.Video.Thumbnails.MINI_KIND, null);
            if (video.getThumbPath() != null) {
                image.setImageBitmap(video.getBitmap());
                // 显示图片
                Picasso.with(mContext)
//                        .load(FileUtils.bitmap2File(MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(),
//                        video.getId(), MediaStore.Video.Thumbnails.MINI_KIND, null),mContext))
                        .load(FileUtils.bitmap2File(video.getBitmap(),mContext))
//                        .load(video.getThumbPath())
//                        .load(video.getThumbPath())
                        .placeholder(R.drawable.mis_default_error)
                        .tag(MultiImageSelectorFragment.TAG)
                        .resize(mGridWidth, mGridWidth)
                        .centerCrop()
                        .into(image);
            }else{
                image.setImageResource(R.drawable.mis_default_error);
            }


        }


        class ImageTask extends AsyncTask<Video, Void, Void> {


            @Override
            protected Void doInBackground(Video... videos) {
                Video video = videos[0];

                Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(),
                        video.getId(), MediaStore.Video.Thumbnails.MINI_KIND, null);
                if (bitmap != null) {
                    // 显示图片
                    Picasso.with(mContext)
                            .load(FileUtils.bitmap2File(bitmap,mContext))
                            .placeholder(R.drawable.mis_default_error)
                            .tag(MultiImageSelectorFragment.TAG)
                            .resize(mGridWidth, mGridWidth)
                            .centerCrop()
                            .into(image);
                }else{
                    image.setImageResource(R.drawable.mis_default_error);
                }
                return null;
            }
        }


    }



}
