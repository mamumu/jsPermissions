package com.mumu.jspermissions;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.mumu.alertdialog.MMAlertDialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * @author : zlf
 * date   : 2019/1/22
 * blog   :https://www.jianshu.com/u/281e9668a5a6
 * 1，在需要动态申请权限的类上添加 @RuntimePermissions 的注解
 */
@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn1)
    Button btn1;
    //是否已经授权
    private boolean isAllowPermissions = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn1)
    public void onViewClicked() {
        showDialog2();
    }

    /**
     * 拨打电话的弹窗
     */
    private void showDialog2() {
        MMAlertDialogUtils.showDialog(this,
                "客服电话",
                "10086",
                "取消",
                "呼叫",
                false,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "取消", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //拨号方法
                        if (!isAllowPermissions) {
                            MainActivityPermissionsDispatcher.callPhoneWithPermissionCheck(MainActivity.this);
                        } else {
                            callPhone();
                        }
                        dialog.dismiss();
                    }
                });
    }

    /**
     * 2，拨打电话需要CALL_PHONE权限，在对应的方法是标明
     */
    @NeedsPermission(Manifest.permission.CALL_PHONE)
    void callPhone() {
        isAllowPermissions = true;
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:10086");
        intent.setData(data);
        startActivity(intent);
    }

    /**
     *,3，对需要该权限的解释
     */
    @OnShowRationale(Manifest.permission.CALL_PHONE)
    void showWhy(final PermissionRequest request) {
        MMAlertDialogUtils.showDialog(this,
                "提示",
                "我们需要拨打电话权限，否则不能拨打电话",
                "取消",
                "授权",
                false,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                        dialog.dismiss();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                        dialog.dismiss();
                    }
                });
    }

    /**
     *,4，当用户拒绝获取权限的提示
     */
    @OnPermissionDenied(Manifest.permission.CALL_PHONE)
    void showDenied() {
        Toast.makeText(MainActivity.this, "无法获得权限", Toast.LENGTH_SHORT).show();
    }

    /**
     *,5，当用户勾选不再提示并且拒绝的时候调用的方法
     */
    @OnNeverAskAgain(Manifest.permission.CALL_PHONE)
    void showNeverAskAgain() {
        MMAlertDialogUtils.showDialog(MainActivity.this,
                "权限设置提示",
                "应用权限被拒绝,为了不影响您的正常使用，请在 权限 中开启对应权限",
                "取消",
                "进入设置",
                false,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //引导用户至设置页手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
    }

    /**
     *,6，权限回调，调用PermissionsDispatcher的回调方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(MainActivity.this, requestCode, grantResults);
    }
}
