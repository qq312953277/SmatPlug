package com.phicomm.smartplug.modules.data.remote.beans.account;

import com.phicomm.smartplug.base.BaseResponseBean;

import java.io.Serializable;

/**
 * Created by yun.wang
 * Date :2017/6/28
 * Description: ***
 * Version: 1.0.0
 */

public class AccountDetailBean extends BaseResponseBean implements Serializable {

    public AccountData data;
    public String token_status;
}
