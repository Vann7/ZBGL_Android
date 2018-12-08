package com.cec.zbgl.view;

import android.content.Context;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MatrixImageView extends android.support.v7.widget.AppCompatImageView {

    private final static String TAG = "MatrixImageView";

    private Matrix matrix = new Matrix();

    //图片长度
    private float mImageWidth;
    //图片高度
    private float mImageHeight;

    public MatrixImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


}
