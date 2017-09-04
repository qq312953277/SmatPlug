package com.phicomm.smartplug.modules.personal.personalnfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.event.LogoutEvent;
import com.phicomm.smartplug.modules.data.local.file.FileConfig;
import com.phicomm.smartplug.modules.data.remote.beans.account.AccountDetailBean;
import com.phicomm.smartplug.modules.data.remote.beans.account.ModifypropertyResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.account.UploadAvatarBean;
import com.phicomm.smartplug.modules.data.remote.http.HttpErrorCode;
import com.phicomm.smartplug.modules.personal.modifypassword.ModifyPasswordActivity;
import com.phicomm.smartplug.modules.personal.personalmain.AvatarHelper;
import com.phicomm.smartplug.modules.personal.personalmain.UserAccountManager;
import com.phicomm.smartplug.utils.BitmapUtils;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.FileUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.smartplug.utils.UriUtils;
import com.phicomm.widgets.alertdialog.PhiGuideDialog;
import com.phicomm.widgets.birthday.PhiBirthdayPopupWindow;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yun.wang
 * Date :2017/6/30
 * Description: ***
 * Version: 1.0.0
 */

public class ModifyPersonInfoActivity extends BaseActivity implements ModifyPersonInfoContract.View, AvatarHelper.OnItemSelectListener {

    private static final int REQUEST_CODE_TAKE_PHOTO = 1000;
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private static final int REQUEST_CODE_CLIP_IMAGE = 1002;
    private static final int REQUEST_CODE_MODIFY_USERINFO = 1003;

    @BindView(R.id.nickname_modify)
    TextView mNicknameEdt;
    @BindView(R.id.birthday_modify)
    TextView mBirthdayTv;
    @BindView(R.id.old_avatar)
    ImageView mAvatar;
    @BindView(R.id.avatar_area)
    View mAvatarArea;

    @BindView(R.id.sex_radio_group)
    RadioGroup sexRadioGroup;
    private int currentCheckedBtnId;

    private ModifyPersonInfoContract.Presenter myPresenter;
    private AvatarHelper avatarHelper;
    File mTempFile;
    private UserAccountManager userAccountManager;

    private String mAvatarClipLocalPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personinfo_modify);
        myPresenter = new ModifyPersonInfoPresenter(this);
        initTitleView();
        initViews();

        refreshDataInUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //阻止EditText获取焦点，自动弹出软件盘
        mAvatar.setFocusableInTouchMode(true);
        mAvatar.requestFocus();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("tempFile", mTempFile);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreInstance(savedInstanceState);
    }

    private void restoreInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("tempFile")) {
            mTempFile = (File) savedInstanceState.getSerializable("tempFile");
        } else {
            mTempFile = new File(FileUtils.checkDirPath(FileConfig.IMAGE_LOCATION), System.currentTimeMillis() + ".jpg");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                avatarHelper.startActivityForClipImage(this, REQUEST_CODE_CLIP_IMAGE, UriUtils.getUriFromFile(myActivity, mTempFile));
            }
        } else if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                }
                avatarHelper.startActivityForClipImage(this, REQUEST_CODE_CLIP_IMAGE, data.getData());
            }
        } else if (requestCode == REQUEST_CODE_CLIP_IMAGE) {
            if (data == null) {
                //myActivity.showToast(R.string.clip_image_error);
                return;
            }

            Bitmap photo = data.getExtras().getParcelable("data");
            String urlpath = FileUtils.checkDirPath(FileConfig.CLIP_IMAGE_LOCATION) + System.currentTimeMillis() + "_clip.png";
            if (BitmapUtils.writeBitmtpToFile(photo, urlpath)) {

                myActivity.showLoadingDialog(R.string.being_processed);
                myPresenter.uploadAvatarToServer(urlpath);//
                mAvatarClipLocalPath = urlpath;
            }
        } else if (requestCode == REQUEST_CODE_MODIFY_USERINFO) {
            if (resultCode == RESULT_OK) {
                refreshDataInUI();
            }
        }
    }

    private void initTitleView() {
        TitlebarUtils.initTitleBar(this, R.string.person_info);
    }

    public void initViews() {
        avatarHelper = new AvatarHelper(this);
        avatarHelper.setOnItemSelectListener(this);

        sexRadioGroup = (RadioGroup) this.findViewById(R.id.sex_radio_group);
    }

    @Override
    public void refreshDataInUI() {
        UserAccountManager userAccountManager = UserAccountManager.getInstance();
        AccountDetailBean mAccount = userAccountManager.getAccountDetailBean();
        if (mAccount == null) {
            return;
        }

        //头像
        if (!TextUtils.isEmpty(mAccount.data.img)) {
            myPresenter.setImageAvatarByUrl(mAccount.data.img, mAvatar);
        } else {
            mAvatar.setImageResource(R.drawable.default_avatar);
        }

        //昵称
        String nickName = mAccount.data.nickname;
        if (TextUtils.isEmpty(nickName)) {
            String phonenumber = mAccount.data.phonenumber;
            if (!TextUtils.isEmpty(phonenumber)) {
                mNicknameEdt.setText(phonenumber);
            } else {
                mNicknameEdt.setText(null);
            }
        } else {
            mNicknameEdt.setText(nickName);
        }

        //性别
        String gender = mAccount.data.sex;
        if (!TextUtils.isEmpty(gender)) {
            currentCheckedBtnId = gender.equals("1") ? R.id.sex_radio_btn_man : R.id.sex_radio_btn_woman;
            sexRadioGroup.check(currentCheckedBtnId);
        }

        //register checklistener after first set
        sexRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                int checkedBtnId = sexRadioGroup.getCheckedRadioButtonId();
                if (currentCheckedBtnId == checkedBtnId) {
                    return;
                }
                currentCheckedBtnId = checkedBtnId;

                String sex = "0";
                if (checkedBtnId == R.id.sex_radio_btn_man) {
                    sex = "1";
                    myActivity.showLoadingDialog(R.string.being_processed);
                    myPresenter.modifyGender(sex);
                } else if (checkedBtnId == R.id.sex_radio_btn_woman) {
                    sex = "2";
                    myActivity.showLoadingDialog(R.string.being_processed);
                    myPresenter.modifyGender(sex);
                }
            }
        });

        //生日
        String birthday = mAccount.data.birthday;
        if (!TextUtils.isEmpty(birthday)) {
//            birthday = birthday.split(" ")[0];
            mBirthdayTv.setText(birthday);
        } else {
            mBirthdayTv.setText(null);
        }
    }

    @OnClick(R.id.birhdary_modify_layout)
    public void clickBirthdayModify() {
        CommonUtils.hideSoftInputMethod(this);
        String currentValue = mBirthdayTv.getText().toString();


        final PhiBirthdayPopupWindow birthdayPopupWindow = new PhiBirthdayPopupWindow(this);
        birthdayPopupWindow.setBirthdayValue(currentValue);
        birthdayPopupWindow.setOnBirthdaySelectedListener(new PhiBirthdayPopupWindow.OnBirthdaySelectedListener() {
            @Override
            public void onBirthdaySelected(String birthday) {
                birthdayPopupWindow.dismiss();
                myActivity.showLoadingDialog(R.string.being_processed);
                myPresenter.modifyBirthday(birthday);
            }
        });
        birthdayPopupWindow.showAtBottom();
    }

    @OnClick({R.id.old_avatar, R.id.update_avatar})
    public void clickAvatarModify() {
        avatarHelper.showImageSelectorDialog();
    }

    @OnClick(R.id.password_modify_layout)
    public void clickModifyPassword() {
        myActivity.startActiityByExtra(null, ModifyPasswordActivity.class);
    }

    @OnClick(R.id.nickname_modify_layout)
    public void clickModifyNickNameInfo() {
        Intent it = new Intent(this, NickNameModifyActvity.class);
        it.putExtra(NickNameModifyActvity.MODIFY_TYPE_NICKNAME, mNicknameEdt.getText());
        startActivityForResult(it, REQUEST_CODE_MODIFY_USERINFO);
    }

    @OnClick(R.id.bt_logout)
    public void clickLogout() {
        final PhiGuideDialog deleteDialog = new PhiGuideDialog(this);
        deleteDialog.setTitle(getResources().getString(R.string.account_exit));
        deleteDialog.setMessage(getResources().getString(R.string.account_exit_msg));
        deleteDialog.setLeftGuideOnclickListener(getResources().getString(R.string.cancel), R.color.weight_line_color, new PhiGuideDialog.onLeftGuideOnclickListener() {
            @Override
            public void onLeftGuideClick() {
                deleteDialog.dismiss();
            }

        });
        deleteDialog.setRightGuideOnclickListener(getResources().getString(R.string.ok), R.color.syn_text_color, new PhiGuideDialog.onRightGuideOnclickListener() {
            @Override
            public void onRightGuideClick() {
                deleteDialog.dismiss();
                //send logout event
                EventBus.getDefault().post(new LogoutEvent());
            }
        });
        deleteDialog.show();
    }

    @Override
    public void onCaptureImageItemSelected() {
        if (mTempFile == null) {
            mTempFile = new File(FileUtils.checkDirPath(FileConfig.IMAGE_LOCATION), System.currentTimeMillis() + ".jpg");
        }

        AvatarHelper.startActivityForCaptureImage(this, REQUEST_CODE_TAKE_PHOTO, UriUtils.getUriFromFile(myActivity, mTempFile));
    }

    @Override
    public void onPickImageItemSelected() {
        AvatarHelper.startActivityForPickImage(this, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void showGender(String gender) {
        sexRadioGroup.check(gender.equals("1") ? R.id.sex_radio_btn_man : R.id.sex_radio_btn_woman);
    }

    @Override
    public void showAvatar(Bitmap bitmap) {
        if (bitmap != null) {
            mAvatar.setImageBitmap(bitmap);
        }
    }

    @Override
    public void analysisResponseBean(BaseResponseBean t) {
        if (t instanceof ModifypropertyResponseBean) {
            ModifypropertyResponseBean bean = (ModifypropertyResponseBean) t;
            if (bean.error.equals(HttpErrorCode.success)) {
                //need update user account detail info
                myPresenter.getPersonInfoFromServer();

                showToast(R.string.set_success);
            }
        } else if (t instanceof UploadAvatarBean) {
            UploadAvatarBean bean = (UploadAvatarBean) t;
            if (bean.error.equals(HttpErrorCode.success)) {
                //need update user account detail info
                myPresenter.getPersonInfoFromServer();

                showToast(R.string.avatar_switch_success);
            }
        } else if (t instanceof AccountDetailBean) {
            myActivity.cancelLoadingDialog();
            AccountDetailBean bean = (AccountDetailBean) t;
            if (bean.error.equals(HttpErrorCode.success)) {
                //save account info
                userAccountManager = UserAccountManager.getInstance();
                userAccountManager.setAccountAndSave(bean);
                refreshDataInUI();
            }
        }
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }
}
