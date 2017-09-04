package com.phicomm.smartplug.modules.personal.personalnfo;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.gson.Gson;
import com.phicomm.smartplug.R;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.account.AccountData;
import com.phicomm.smartplug.modules.data.remote.beans.account.AccountDetailBean;
import com.phicomm.smartplug.modules.data.remote.beans.account.ModifypropertyResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.account.UploadAvatarBean;
import com.phicomm.smartplug.modules.data.remote.http.CustomSubscriber;

import java.io.File;
import java.lang.reflect.Field;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by yun.wang
 * Date :2017/7/4
 * Description: ***
 * Version: 1.0.0
 */

public class ModifyPersonInfoPresenter implements ModifyPersonInfoContract.Presenter {

    private ModifyPersonInfoContract.View myView;


    public ModifyPersonInfoPresenter(ModifyPersonInfoContract.View myView) {
        this.myView = myView;
    }

    @Override
    public void uploadPersonalInfo(String item, String value) {
        String access_token = DataRepository.getInstance().getAccessToken();


        AccountDetailBean detailBean = new AccountDetailBean();
        detailBean.data = new AccountData();
        try {
            Field field = AccountData.class.getDeclaredField(item);
            field.set(detailBean.data, value);
        } catch (Exception ex) {
            return;
        }


        Gson gson = new Gson();
        String strEntity = gson.toJson(detailBean.data);

//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        RequestBody body = new FormBody.Builder()
                .add("data", strEntity)
                .build();

        DataRepository.getInstance().modifyProperty(myView.getRxLifeCycleObj(),
                new CustomSubscriber<ModifypropertyResponseBean>() {
                    @Override
                    public void onCustomNext(ModifypropertyResponseBean modifypropertyResponseBean) {
                        myView.analysisResponseBean(modifypropertyResponseBean);
                    }
                }, access_token, body);
    }

    @Override
    public void modifyNickName(String nickname) {
        uploadPersonalInfo("nickname", nickname);
    }

    @Override
    public void modifyGender(String sex) {
        uploadPersonalInfo("sex", sex);
    }

    @Override
    public void modifyBirthday(String birthday) {
        uploadPersonalInfo("birthday", birthday);
    }

    @Override
    public void setImageAvatarByUrl(String imgPath, ImageView imageView) {
        Glide.with((ModifyPersonInfoActivity) myView).load(imgPath).dontAnimate()
                .error(R.drawable.default_avatar).placeholder(R.drawable.icon_photo_loading).priority(Priority.HIGH).into(imageView);
    }

    @Override
    public void uploadAvatarToServer(String imgPath) {
        File imageFile = new File(imgPath);
        if (imageFile == null || !imageFile.exists()) {
            return;
        }
        String accessToke = DataRepository.getInstance().getAccessToken();
        DataRepository.getInstance().uploadAvatar(myView.getRxLifeCycleObj(),
                new CustomSubscriber<UploadAvatarBean>() {
                    @Override
                    public void onCustomNext(UploadAvatarBean uploadAvatarBean) {
                        myView.analysisResponseBean(uploadAvatarBean);
                    }
                }, accessToke, imageFile, "1");
    }

    @Override
    public void getPersonInfoFromServer() {
        String access_token = DataRepository.getInstance().getAccessToken();
        DataRepository.getInstance().accountDetail(myView.getRxLifeCycleObj(),
                new CustomSubscriber<AccountDetailBean>() {
                    @Override
                    public void onCustomNext(AccountDetailBean accountDetailBean) {
                        myView.analysisResponseBean(accountDetailBean);
                    }
                }, access_token);
    }
}
