package com.phicomm.smartplug.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.StringUtils;

/**
 * Created by feilong.yang on 2017/8/14.
 */

public class CustomEditText extends EditText {

    private final int DEFAULT_LIMIT_NUMBER = 5;//限制数字默认5个
    private int mLimitNumber;

    private LimitCallback mLimitCallback;
    private Context mContext;

    public CustomEditText(Context paramContext) {
        super(paramContext);
        mContext = paramContext;
        init(null);
    }

    public CustomEditText(Context paramContext, AttributeSet attrs) {
        super(paramContext, attrs);
        mContext = paramContext;
        init(attrs);
    }

    public CustomEditText(Context paramContext, AttributeSet attrs, int paramInt) {
        super(paramContext, attrs, paramInt);
        mContext = paramContext;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = attrs == null ? null : getContext().obtainStyledAttributes(attrs, R.styleable.CustomEditText);
        if (ta != null) {
            mLimitNumber = ta.getInteger(R.styleable.CustomEditText_number_limit, DEFAULT_LIMIT_NUMBER);
            ta.recycle();
        }
        initEditText();
    }

    private void initEditText() {
        addTextChangedListener(mTextWatcher);
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;
        private String specialCharacters = "";

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count > 0) {
                String add = s.toString().substring(start, start + count);
                if (CommonUtils.hasSpecialCharacters(add)) {
                    specialCharacters = add;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = getSelectionStart();
            editEnd = getSelectionEnd();
            if (!StringUtils.isNull(specialCharacters)) {
                String editable = s.toString();
                int index = editable.indexOf(specialCharacters);
                if (index >= 0) {
                    s.delete(index, index + specialCharacters.length());
                    CommonUtils.showShortToast(mContext.getString(R.string.device_name_input_rule));
                    return;
                }
            }
            if (temp.length() > mLimitNumber) {
                if (null != mLimitCallback) {
                    mLimitCallback.limitCallback();
                    s.delete(editStart - 1, editEnd);
                }
            }
        }
    };

    public interface LimitCallback {
        void limitCallback();
    }

    public void setCallback(LimitCallback callback) {
        mLimitCallback = callback;
    }
}