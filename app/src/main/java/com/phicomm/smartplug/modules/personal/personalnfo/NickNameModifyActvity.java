package com.phicomm.smartplug.modules.personal.personalnfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.base.BaseView;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.account.AccountData;
import com.phicomm.smartplug.modules.data.remote.beans.account.AccountDetailBean;
import com.phicomm.smartplug.modules.data.remote.beans.account.ModifypropertyResponseBean;
import com.phicomm.smartplug.modules.data.remote.http.CustomSubscriber;
import com.phicomm.smartplug.modules.data.remote.http.HttpErrorCode;
import com.phicomm.smartplug.modules.personal.personalmain.UserAccountManager;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.LogUtils;
import com.phicomm.smartplug.utils.NetworkManagerUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.widgets.PhiTitleBar;

import java.lang.reflect.Field;

import butterknife.BindView;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by yun.wang on 2017/7/5.
 */

public class NickNameModifyActvity extends BaseActivity implements BaseView {

    public static String MODIFY_TYPE_NICKNAME = "nickname";

    @BindView(R.id.nick_edit)
    EditText nickNameEdit;

    private String defaultNickName = "'";
    private String newNickName = "'";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nick_layout);

        initIntentData();

        initTitleView();

        initView();
    }

    private void initIntentData() {
        Intent intent = this.getIntent();
        if (intent != null) {
            defaultNickName = intent.getExtras().getString(NickNameModifyActvity.MODIFY_TYPE_NICKNAME, "");
            nickNameEdit.setText(defaultNickName);
            nickNameEdit.setSelection(defaultNickName.length());
        }
    }


    private void initTitleView() {
        PhiTitleBar phiTitleBar = (PhiTitleBar) this.findViewById(R.id.title_bar);
        TitlebarUtils.initTitleBar(this, R.string.modify_nickname);
        phiTitleBar.setActionTextColor(R.color.white);
        phiTitleBar.addAction(new PhiTitleBar.TextAction(getString(R.string.complete)) {
            @Override
            public void performAction(View view) {
                uploadPersonalInfo();
            }
        });
    }

    private void initView() {
        nickNameEdit.setFilters(new InputFilter[]{new MaxTextLengthFilter(12)});
    }

    class MaxTextLengthFilter implements InputFilter {

        private int mMaxLength;

        public MaxTextLengthFilter(int max) {
            mMaxLength = max - 1;
        }

        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            LogUtils.d(TAG, "source=" + source);
            LogUtils.d(TAG, "start=" + start);
            LogUtils.d(TAG, "end=" + end);
            LogUtils.d(TAG, "dest=" + dest);
            LogUtils.d(TAG, "dstart=" + dstart);
            LogUtils.d(TAG, "dend=" + dend);

            int keep = mMaxLength - (dest.length() - (dend - dstart));//剩余可以输入的长度
            LogUtils.d(TAG, "keep=" + keep);
            if (keep < (end - start)) {
                myActivity.showToast(R.string.input_limit);
            }
            if (keep <= 0) {
                return "";
            } else if (keep >= end - start) {
                return null;
            } else {
                return source.subSequence(start, start + keep);
            }
        }
    }

    public void uploadPersonalInfo() {

        String nickname = nickNameEdit.getText().toString();
        if (TextUtils.isEmpty(nickname)) {
            myActivity.showToast(R.string.nickname_empty_tip);
            return;
        }
        if (nickname.trim().length() < 1 || nickname.trim().length() > 11) {
            myActivity.showToast(R.string.nickname_length_tip);
            return;
        }
        if (!CommonUtils.isNickname(nickname)) {
            myActivity.showToast(R.string.nickname_text_tip);
            return;
        }
        if (NetworkManagerUtils.instance().networkError()) {
            myActivity.showToast(R.string.net_connect_fail);
            return;
        }
        if (defaultNickName.equals(nickname)) {
            finish();
            return;
        }

        newNickName = nickname;
        String access_token = DataRepository.getInstance().getAccessToken();

        AccountDetailBean detailBean = new AccountDetailBean();
        detailBean.data = new AccountData();
        try {
            Field field = AccountData.class.getDeclaredField("nickname");
            field.set(detailBean.data, newNickName);
        } catch (Exception ex) {
            return;
        }


        myActivity.showLoadingDialog(R.string.being_processed);
        Gson gson = new Gson();
        String strEntity = gson.toJson(detailBean.data);
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        RequestBody body = new FormBody.Builder()
                .add("data", strEntity)
                .build();

        DataRepository.getInstance().modifyProperty(this.getRxLifeCycleObj(),
                new CustomSubscriber<ModifypropertyResponseBean>() {
                    @Override
                    public void onCustomNext(ModifypropertyResponseBean modifypropertyResponseBean) {
                        analysisResponseBean(modifypropertyResponseBean);
                    }
                }, access_token, body);
    }

    public void analysisResponseBean(BaseResponseBean t) {
        if (t instanceof ModifypropertyResponseBean) {
            myActivity.cancelLoadingDialog();
            ModifypropertyResponseBean bean = (ModifypropertyResponseBean) t;
            if (bean.error.equals(HttpErrorCode.success)) {
                showToast(R.string.set_success);

                //need update user account detail info
                UserAccountManager userAccountManager = UserAccountManager.getInstance();
                AccountDetailBean mAccount = userAccountManager.getAccountDetailBean();
                mAccount.data.nickname = newNickName;
                userAccountManager.setAccountAndSave(mAccount);

                setResult(RESULT_OK);
                this.finish();
            }
        }
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }
}
