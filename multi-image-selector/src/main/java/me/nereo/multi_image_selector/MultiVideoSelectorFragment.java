package me.nereo.multi_image_selector;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorFragment;
import me.nereo.multi_image_selector.R;
import me.nereo.multi_image_selector.adapter.FolderAdapter;
import me.nereo.multi_image_selector.adapter.ImageGridAdapter;
import me.nereo.multi_image_selector.adapter.VideoGridAdapter;
import me.nereo.multi_image_selector.bean.Folder;
import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.bean.Video;
import me.nereo.multi_image_selector.event.VideoEvent;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class MultiVideoSelectorFragment extends Fragment {


    public static final String TAG = "MultiImageSelectorFragment";

    private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 110;
    private static final int REQUEST_CAMERA = 100;

    private static final String KEY_TEMP_FILE = "key_temp_file";

    // Single choice
    public static final int MODE_SINGLE = 0;
    // Multi choice
    public static final int MODE_MULTI = 1;

    private static final int WRITE_PERMISSION = 0x01;

    /** Max image size，int，*/
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** Select mode，{@link #MODE_MULTI} by default */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /** Whether show camera，true by default */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** Original data set */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    // loaders
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;

    // image result data set
    private ArrayList<String> resultList = new ArrayList<>();
    // folder result data set
    private ArrayList<Folder> mResultFolder = new ArrayList<>();

    private GridView mGridView;
    private Callback mCallback;

    private VideoGridAdapter mVideoAdapter;

    private ListPopupWindow mFolderPopupWindow;

    private TextView mCategoryText;
    private View mPopupAnchorView;

    private boolean hasFolderGened = false;

    private File mTmpFile;

    private final String[] MEDIA_COLUMNS ={
            MediaStore.Files.FileColumns._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Thumbnails.DATA
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (Callback) getActivity();
        }catch (ClassCastException e){
            throw new ClassCastException("The Activity must implement MultiImageSelectorFragment.Callback interface...");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mis_fragment_multi_video, container, false);
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestWritePermission();
        final int mode = selectMode();
        if(mode == MODE_MULTI) {
            ArrayList<String> tmp = getArguments().getStringArrayList(EXTRA_DEFAULT_SELECTED_LIST);
            if(tmp != null && tmp.size()>0) {
                resultList = tmp;
            }
        }
        mVideoAdapter = new VideoGridAdapter(getActivity(),  3);
        mVideoAdapter.showSelectIndicator(mode == MODE_MULTI);

        mGridView = (GridView) view.findViewById(R.id.grid_video);
        mGridView.setAdapter(mVideoAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Video video = (Video) adapterView.getAdapter().getItem(i);
                selectVideoFromGrid(video, mode);
            }
        });
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_FLING) {
                    Picasso.with(view.getContext()).pauseTag(TAG);
                } else {
                    Picasso.with(view.getContext()).resumeTag(TAG);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new LoadTask().execute();


//        mVideoAdapter.setData(videos);
        if(resultList != null && resultList.size()>0){
            mVideoAdapter.setDefaultSelected(resultList);
        }
//        loadData();
        // load image data
//        getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
    }



    private void loadData() {
        Cursor cursor = getActivity().managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                MEDIA_COLUMNS, null, null, null);

        List<Video> videos = new ArrayList<>();
        cursor.moveToFirst();
        do{
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[0]));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[1]));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[2]));
            long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[3]));
            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(getContext().getContentResolver(),
                    id, MediaStore.Video.Thumbnails.MINI_KIND, null);

            String thumbPath = cursor.getString(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[4]));

            if(!fileExist(path)){continue;}
            Video video = null;
            if (!TextUtils.isEmpty(name)) {
                video = new Video(id,path, name,bitmap);
                video.setThumbPath(thumbPath);
                videos.add(video);
            }

        }while(cursor.moveToNext());

        mVideoAdapter.setData(videos);
        if(resultList != null && resultList.size()>0){
            mVideoAdapter.setDefaultSelected(resultList);
        }
    }

    private boolean fileExist(String path){
        if(!TextUtils.isEmpty(path)){
            return new File(path).exists();
        }
        return false;
    }


    private class LoadTask extends AsyncTask<Void, Void, List<Video>> {

        @Override
        protected List<Video> doInBackground(Void... voids) {
            Cursor cursor = getActivity().managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    MEDIA_COLUMNS, null, null, null);

            List<Video> videos = new ArrayList<>();
            cursor.moveToFirst();
            do{
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[0]));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[1]));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[2]));
//                long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[3]));
                Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(getContext().getContentResolver(),
                        id, MediaStore.Video.Thumbnails.MINI_KIND, null);

                String thumbPath = cursor.getString(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[4]));

                if(!fileExist(path)){continue;}
                Video video = null;
                if (!TextUtils.isEmpty(name)) {
//                    video = new Video(id,path, name);
                    video = new Video(id,path, name, bitmap);
                    video.setThumbPath(thumbPath);
                    videos.add(video);
                }
            }while(cursor.moveToNext());

            EventBus.getDefault().post(new VideoEvent(videos));
            return videos;
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] MEDIA_COLUMNS ={
                MediaStore.Files.FileColumns._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Thumbnails.DATA
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = null;
            if (id ==LOADER_ALL) {
                cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        MEDIA_COLUMNS,null,null,null);
            }
            return cursorLoader;
        }

        private boolean fileExist(String path){
            if(!TextUtils.isEmpty(path)){
                return new File(path).exists();
            }
            return false;
        }

        /**
         * 加载视频信息列表
         * @param loader
         * @param cursor
         */
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    List<Video> videos = new ArrayList<>();
                    cursor.moveToFirst();
                    do{
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[0]));
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[1]));
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[2]));
                        long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[3]));
                        Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(getContext().getContentResolver(),
                                id, MediaStore.Video.Thumbnails.MINI_KIND, null);

                       String thumbPath = cursor.getString(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[4]));

                        if(!fileExist(path)){continue;}
                        Video video = null;
                        if (!TextUtils.isEmpty(name)) {
                            video = new Video(id,path, name, bitmap);
                            video.setThumbPath(thumbPath);
                            videos.add(video);
                        }

                    }while(cursor.moveToNext());

                    mVideoAdapter.setData(videos);
                    if(resultList != null && resultList.size()>0){
                        mVideoAdapter.setDefaultSelected(resultList);
                    }

                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };


    /**
     * notify callback
     * @param video  video
     */
    private void selectVideoFromGrid(Video video, int mode) {
        if(video != null) {
            if(mode == MODE_MULTI) {
                if (resultList.contains(video.path + " " + video.getId())) {
                    resultList.remove(video.path + " " + video.getId());
                    if (mCallback != null) {
                        mCallback.onVideoUnselected(video.path + " " + video.getId());
                    }
                } else {
                    if(selectImageCount() == resultList.size()){
                        Toast.makeText(getActivity(), R.string.mis_msg_amount_limit, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    resultList.add(video.path + " " + video.getId());
                    if (mCallback != null) {
                        mCallback.onVideoSelected(video.path + " " + video.getId());
                    }
                }
                mVideoAdapter.select(video);
            }else if(mode == MODE_SINGLE){
                if(mCallback != null){
                    mCallback.onSingleVideoSelected(video.path + " " + video.getId());
                }
            }
        }
    }

    private int selectMode(){
        return getArguments() == null ? MODE_MULTI : getArguments().getInt(EXTRA_SELECT_MODE);
    }

    private int selectImageCount(){
        return getArguments() == null ? 9 : getArguments().getInt(EXTRA_SELECT_COUNT);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_STORAGE_WRITE_ACCESS_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                showCameraAction();
            }
        } else if( requestCode == WRITE_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestWritePermission() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    /**
     * 数据同步完成后刷新界面UI
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(VideoEvent event) {
        mVideoAdapter.setData(event.getList());
        if(resultList != null && resultList.size()>0){
            mVideoAdapter.setDefaultSelected(resultList);
        }
    }


    /**
     * Callback for host activity
     */
    public interface Callback{
        void onSingleVideoSelected(String path);
        void onVideoSelected(String path);
        void onVideoUnselected(String path);
    }
}
