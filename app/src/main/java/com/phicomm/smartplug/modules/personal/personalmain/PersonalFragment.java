package com.phicomm.smartplug.modules.personal.personalmain;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseFragment;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackerConfig;
import com.phicomm.smartplug.modules.data.remote.beans.account.AccountDetailBean;
import com.phicomm.smartplug.modules.data.remote.http.HttpErrorCode;
import com.phicomm.smartplug.modules.personal.about.AboutActivity;
import com.phicomm.smartplug.modules.personal.commonissues.CommonIssuesActivity;
import com.phicomm.smartplug.modules.personal.personalnfo.ModifyPersonInfoActivity;
import com.phicomm.smartplug.modules.personal.update.CheckUpdateContract;
import com.phicomm.smartplug.modules.personal.update.CheckUpdatePresenter;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.DialogUtils;
import com.phicomm.smartplug.utils.NetworkManagerUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.widgets.PhiTitleBar;
import com.phicomm.widgets.alertdialog.PhiAlertDialog;
import com.phicomm.widgets.alertdialog.PhiGuideDialog;
import com.tbruyelle.rxpermissions.RxPermissions;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

public class PersonalFragment extends BaseFragment implements PersonalFragmentContract.View, CheckUpdateContract.View {

    public static final int REQUEST_CODE_MODIFY_INFO = 1004;

    @BindView(R.id.account_icon_imageview)
    ImageView account_icon_imageview;

    @BindView(R.id.username_textview)
    TextView username_textview;

    @BindView(R.id.usernickname_textview)
    TextView usernickname_textview;

    @BindView(R.id.new_version_indicator)
    ImageView mNewVersionIndicator;

    @BindView(R.id.version_name)
    TextView mVersionName;

    private PersonalFragmentContract.Presenter myPresenter;
    private UserAccountManager userAccountManager;

    // check update
    private CheckUpdateContract.Presenter checkUpdatePresenter;
    private RxPermissions rxPermissions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(com.phicomm.smartplug.R.layout.fragment_personal_layout, null);
        initTitleView(myView);
        myPresenter = new PersonalFragmentPresenter(this);
        myPresenter.getPersonInfoFromServer();

        // check update
        checkUpdatePresenter = new CheckUpdatePresenter(this);
        return myView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshDataInUI();

        // check permission
        rxPermissions = new RxPermissions(myActivity);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // version
        if (checkUpdatePresenter != null && mNewVersionIndicator != null) {
            if (checkUpdatePresenter.hasNewVersion()) {
                mNewVersionIndicator.setVisibility(View.VISIBLE);
            } else {
                mNewVersionIndicator.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MODIFY_INFO) {
            refreshDataInUI();
        }
    }

    private void initTitleView(View mainView) {
        PhiTitleBar phiTitleBar = (PhiTitleBar) mainView.findViewById(R.id.title_bar);
        TitlebarUtils.initTitleBar(this.getActivity(), phiTitleBar, R.string.personal_center);
    }

    @OnClick({R.id.account_icon_imageview, R.id.account_name_layout, R.id.usermore_imageview})
    public void enterUserInfoActivity() {
        Intent intent = new Intent(myActivity, ModifyPersonInfoActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MODIFY_INFO);
    }

    @OnClick(R.id.version_check_layout)
    public void doVersionCheck() {
        if (!NetworkManagerUtils.isNetworkAvailable(getContext())) {
            showNotification(R.string.network_unavailable);
        } else {
            myActivity.showLoadingDialog(R.string.checking_update);
            checkUpdatePresenter.checkNewVersion();

            // umeng
            DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_UPDATE, "type", "check_update_manually");
        }
    }

    @OnClick(R.id.commonissues_layout)
    public void enterCommonIssuesActiviry() {
        Intent intent = new Intent(myActivity, CommonIssuesActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.about_layout)
    public void enterAboutActiviry() {
        Intent intent = new Intent(myActivity, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void showAvatar(Bitmap bitmap) {
        if (bitmap != null) {
            account_icon_imageview.setImageBitmap(bitmap);
        }
    }

    @Override
    public void refreshDataInUI() {
        userAccountManager = UserAccountManager.getInstance();
        AccountDetailBean mAccount = userAccountManager.getAccountDetailBean();
        if (mAccount == null) {
            return;
        }

        //头像
        if (!TextUtils.isEmpty(mAccount.data.img)) {
            myPresenter.setImageAvatarByUrl(mAccount.data.img, account_icon_imageview);
        } else {
            account_icon_imageview.setImageResource(R.drawable.default_avatar);
        }

        //昵称
        String nickName = mAccount.data.nickname;
        if (TextUtils.isEmpty(nickName)) {
            String phonenumber = mAccount.data.phonenumber;
            if (!TextUtils.isEmpty(phonenumber)) {
                usernickname_textview.setText(phonenumber);
            } else {
                usernickname_textview.setText(null);
            }
        } else {
            usernickname_textview.setText(nickName);
        }

        // version
        mVersionName.setText(CommonUtils.getVersionName());
    }

    @Override
    public void analysisResponseBean(BaseResponseBean t) {
        if (t instanceof AccountDetailBean) {
            AccountDetailBean bean = (AccountDetailBean) t;
            if (bean.error.equals(HttpErrorCode.success)) {
                userAccountManager = UserAccountManager.getInstance();
                userAccountManager.setAccountAndSave(bean);
                refreshDataInUI();
            }
        }
    }

    @Override
    public void dismissLoadingDialog() {
        DialogUtils.cancelLoadingDialog();
    }

    @Override
    public void updateProgress(int progress) {

    }

    @Override
    public void handleNewVersionInfo(final boolean isForceUpdate, String url, String versionName, String versionInfo) {
        // display dialog
        View view = LayoutInflater.from(myActivity).inflate(R.layout.download_apk_dialog, null);
        TextView title = (TextView) view.findViewById(R.id.update_title);
        TextView message = (TextView) view.findViewById(R.id.update_message);
        title.setText(String.format(getString(R.string.version_update_title), versionName));
        message.setText(versionInfo);

        final PhiGuideDialog dialog = new PhiGuideDialog(myActivity);
        dialog.setContentPanel(view);
        if (!isForceUpdate) {
            dialog.setLeftGuideOnclickListener(getResources().getString(R.string.update_later), R.color.syn_text_color, new PhiGuideDialog.onLeftGuideOnclickListener() {
                @Override
                public void onLeftGuideClick() {
                    dialog.dismiss();
                }

            });
        }

        dialog.setRightGuideOnclickListener(getResources().getString(R.string.update_now), R.color.weight_line_color, new PhiGuideDialog.onRightGuideOnclickListener() {
            @Override
            public void onRightGuideClick() {
                if (checkUpdatePresenter.isDownloading()) {
                    showToast(R.string.download_wait);
                    return;
                }

                if (!isForceUpdate) {
                    dialog.dismiss();
                }

                // request permission
                rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (aBoolean) {
                                    checkUpdatePresenter.requestDownloadApk();
                                } else {
                                    CommonUtils.showToastBottom(R.string.read_storage_permission_fail_tips);
                                }
                            }
                        });
            }
        });

        dialog.show();
        if (isForceUpdate) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    @Override
    public void showNetWorkDialog() {
        PhiAlertDialog.Builder updateBuilder = new PhiAlertDialog.Builder(myActivity);
        updateBuilder.setTitle(R.string.version_check)
                .setMessage(R.string.mobile_net_use)
                .setPositiveButton(R.string.download_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkUpdatePresenter.downloadApk();
                    }
                }).show();
    }

    @Override
    public void showNotification(int res) {
        showToast(res);
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }
}
