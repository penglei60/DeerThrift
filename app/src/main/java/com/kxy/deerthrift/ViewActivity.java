package com.kxy.deerthrift;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kxy.deerthrift.base.BaseActivity;
import com.kxy.deerthrift.utils.DeviceUuidFactory;
import com.kxy.deerthrift.utils.ToastUtil;
import com.kxy.deerthrift.view.CustomizeDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ViewActivity extends BaseActivity {
    private static final String TAG = "ViewActivity";

    @BindView(R.id.iv_image1)
    ImageView mIvImage1;
    @BindView(R.id.iv_image2)
    ImageView mIvImage2;
    @BindView(R.id.iv_image3)
    ImageView mIvImage3;
    @BindView(R.id.iv_image4)
    ImageView mIvImage4;

    private static String url = "https://download1234.oss-cn-shanghai.aliyuncs.com/fuxingqp-3.apk";

    private static String cUrl = "http://51fxqp.com/";

    @Override
    protected int getContentViewId() {
        // 隐藏状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_view;
    }

    @Override
    public void initView() {
        getPermissions();  // 权限管理
        getUUID(); // 获取手机唯一标识码

    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.iv_image1, R.id.iv_image2, R.id.iv_image3, R.id.iv_image4})
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.iv_image1:
            case R.id.iv_image2:
            case R.id.iv_image3:
                Uri uri = Uri.parse(url);
                Log.e("hhh", "url====" + url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;

            case R.id.iv_image4:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(cUrl);
                setDialog();
                break;
        }
    }


    /**
     * 弹框
     */
    private void setDialog() {
        final CustomizeDialog dialog = new CustomizeDialog(ViewActivity.this, R.style.DialogUtilStyle);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);

                btn_ok.setOnClickListener(v -> {
                    dialog.dismiss();
                });

            }
        });
        dialog.showDialog(R.layout.dialog_content_one);
    }


    /**
     * 获取android手机唯一标识码 uuid
     */
    private void getUUID() {
        String udid = DeviceUuidFactory.getInstance(this).getDeviceUuid().toString();
        Log.e("uuid", "=====" + udid.toString());
    }

    /**
     * 使点击回退按钮不会直接退出整个应用程序而是返回上一个页面
     */
    // 再按一次退出
    private long mExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtil.showShort(getResources().getString(R.string.s_btn_exit));
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);//退出整个应用程序
    }


    /**
     * 权限管理
     */
    private void getPermissions() {
        // 权限管理（设置需要打开的所有权限）
        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        PermissionsUtils.showSystemSetting = false;//是否支持显示系统设置权限设置窗口跳转
        //这里的this不是上下文，是Activity对象！
        PermissionsUtils.getInstance().chekPermissions(this, permissions, permissionsResult);
    }

    //创建监听权限的接口对象（权限设置）
    PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
//            Toast.makeText(MainActivity.this, "权限通过，可以做其他事情!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void forbitPermissons() {
//            finish();
//            Toast.makeText(MainActivity.this, "权限不通过!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //就多一个参数this
        PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
