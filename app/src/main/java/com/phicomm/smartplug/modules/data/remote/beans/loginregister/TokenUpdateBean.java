package com.phicomm.smartplug.modules.data.remote.beans.loginregister;

import com.phicomm.smartplug.base.BaseResponseBean;

/**
 * Created by yun.wang
 * Date :2017/6/28
 * Description: ***
 * Version: 1.0.0
 */

public class TokenUpdateBean extends BaseResponseBean {

    public String access_token;
    public String access_token_expire;
    public String scope;
    public String token_status;
    public String token_type;
    public String uid;
}
