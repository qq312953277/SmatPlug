package com.phicomm.smartplug.modules.personal.personalnfo;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.phicomm.smartplug.base.BasePresenter;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.base.BaseView;

public class ModifyPersonInfoContract {
    public interface View extends BaseView {

        //解析http response
        void analysisResponseBean(BaseResponseBean t);

        //显示头像图片
        void showAvatar(Bitmap bitmap);

        //显示性别
        void showGender(String gender);

        void refreshDataInUI();

    }

    public interface Presenter extends BasePresenter {
        //上传用户信息
        void uploadPersonalInfo(String key, String value);

        //显示头像
        void setImageAvatarByUrl(String imgPath, ImageView imageView);

        void uploadAvatarToServer(String imgPath);

        void modifyNickName(String nickname);

        void modifyGender(String sex);

        void modifyBirthday(String nickName);

        void getPersonInfoFromServer();
    }
}
