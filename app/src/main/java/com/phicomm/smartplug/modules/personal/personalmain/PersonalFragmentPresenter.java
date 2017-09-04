package com.phicomm.smartplug.modules.personal.personalmain;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.phicomm.smartplug.R;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.account.AccountDetailBean;
import com.phicomm.smartplug.modules.data.remote.http.CustomSubscriber;

/**
 * Created by yun.wang
 * Date :2017/6/30
 * Description: ***
 * Version: 1.0.0
 */

public class PersonalFragmentPresenter implements PersonalFragmentContract.Presenter {

    PersonalFragmentContract.View mView;

    public PersonalFragmentPresenter(PersonalFragmentContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void getPersonInfoFromServer() {
        String access_token = DataRepository.getInstance().getAccessToken();
        DataRepository.getInstance().accountDetail(mView.getRxLifeCycleObj(), new CustomSubscriber<AccountDetailBean>() {
            @Override
            public void onCustomNext(AccountDetailBean accountDetailBean) {
                mView.analysisResponseBean(accountDetailBean);
            }
        }, access_token);
    }

    @Override
    public void setImageAvatarByUrl(String imgPath, ImageView imageView) {
        Glide.with(((PersonalFragment) mView).getActivity()).load(imgPath).dontAnimate()
                .error(R.drawable.default_avatar).placeholder(R.drawable.icon_photo_loading).priority(Priority.HIGH).into(imageView);
    }
}
