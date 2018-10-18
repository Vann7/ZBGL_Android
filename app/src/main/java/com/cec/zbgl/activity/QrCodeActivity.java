package com.cec.zbgl.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cec.zbgl.R;
import com.cec.zbgl.thirdLibs.zxing.activity.CaptureActivity;
import com.cec.zbgl.thirdLibs.zxing.encoding.EncodingUtils;
import com.cec.zbgl.utils.LogUtil;

/**
 * 二维码测试activity
 */
public class QrCodeActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTvResult;
    private EditText mInput;
    private ImageView mResult;
    private CheckBox mLogo;
    private Button scan_btn;
    private Button qc_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        initView();
    }

    public void initView(){
        mTvResult = (TextView) findViewById(R.id.tv_1);
        mInput = (EditText) findViewById(R.id.et_qc);
        mResult = (ImageView) findViewById(R.id.iv_qc);
        mLogo = (CheckBox) findViewById(R.id.cb_logo);
        scan_btn = (Button) findViewById(R.id.scan_btn);
        qc_btn = (Button) findViewById(R.id.qc_btn);
        scan_btn.setOnClickListener(this);
        qc_btn.setOnClickListener(this);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String result = bundle.getString("result");
            mTvResult.setText(result );
        }
    }


    @Override
    public void onClick(View v) {
        LogUtil.d("id",String.valueOf(v.getId()));
        switch (v.getId()) {
            case R.id.scan_btn:
                if (Build.VERSION.SDK_INT>22){
                    if (ContextCompat.checkSelfPermission(QrCodeActivity.this,
                            android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                        //先判断有没有权限 ，没有就在这里进行权限的申请
                        ActivityCompat.requestPermissions(QrCodeActivity.this,
                                new String[]{android.Manifest.permission.CAMERA},1);

                        if (ContextCompat.checkSelfPermission(QrCodeActivity.this,
                                android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {
                            startActivityForResult(new Intent(QrCodeActivity.this,
                                    CaptureActivity.class), 1);
                        }else {
                            Toast.makeText(QrCodeActivity.this,"开启摄像头权限失败",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        //说明已经获取到摄像头权限了 想干嘛干嘛
                        startActivityForResult(new Intent(QrCodeActivity.this,
                                CaptureActivity.class), 1);
                    }
                }else {
                    //这个说明系统版本在6.0之下，不需要动态获取权限。
                    startActivityForResult(new Intent(QrCodeActivity.this,
                            CaptureActivity.class), 1);
                }



                break;
            case R.id.qc_btn:
                String input = mInput.getText().toString();
                if (input.equals("")) {
                    Toast.makeText(QrCodeActivity.this,"输入不能为空",Toast.LENGTH_LONG).show();
                }else {
                    Bitmap bitmap = EncodingUtils.createQRCode(input,600, 600, mLogo.isChecked() ? BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_round) : null);
                    mResult.setImageBitmap(bitmap);
                }
                break;
            default:
        }
    }
}
