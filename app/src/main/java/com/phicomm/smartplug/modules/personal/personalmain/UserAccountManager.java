package com.phicomm.smartplug.modules.personal.personalmain;

import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.account.AccountDetailBean;

import java.io.Serializable;

/**
 * Created by yun.wang
 * Date :2017/7/3
 * Description: ***
 * Version: 1.0.0
 */

public class UserAccountManager implements Serializable {
    private AccountDetailBean bean;

    private static UserAccountManager userAccountManager;

    private UserAccountManager() {

    }

    public static UserAccountManager getInstance() {
        if (userAccountManager == null) {
            userAccountManager = new UserAccountManager();
        }

        return userAccountManager;
    }

    public synchronized AccountDetailBean getAccountDetailBean() {
        bean = (AccountDetailBean) DataRepository.getInstance().getAccountDetailInfo();
        return bean;
    }

    public synchronized void setAccountAndSave(AccountDetailBean bean) {
        this.bean = bean;
        DataRepository.getInstance().setAccountDetailInfo(this.bean);
    }
}
