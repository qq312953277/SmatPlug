package com.phicomm.smartplug.modules.personal.personalmain;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.phicomm.smartplug.base.BasePresenter;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.base.BaseView;

public class PersonalFragmentContract {
    public interface View extends BaseView {
        //解析http response
        void analysisResponseBean(BaseResponseBean t);

        //显示头像图片
        void showAvatar(Bitmap bitmap);

        //刷新UI
        void refreshDataInUI();
    }

    public interface Presenter extends BasePresenter {
        //显示头像
        void setImageAvatarByUrl(String imgPath, ImageView imageView);

        //从云端获取accountinfo
        void getPersonInfoFromServer();
    }
}
