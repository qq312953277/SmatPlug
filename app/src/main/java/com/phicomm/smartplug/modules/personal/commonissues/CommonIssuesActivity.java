package com.phicomm.smartplug.modules.personal.commonissues;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.utils.LogUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yun.wang
 * Date :2017/6/30
 * Description: ***
 * Version: 1.0.0
 */

public class CommonIssuesActivity extends BaseActivity {

    @BindView(R.id.issues_layout)
    LinearLayout issuesLayout;

    List<IssueItem> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_issues_layout);

        initTitleView();

        initView();
    }

    private void initTitleView() {
        TitlebarUtils.initTitleBar(this, R.string.common_issues);
    }

    private void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(myActivity);

        initData();

        if (dataList == null) {
            return;
        }
        for (IssueItem issue : dataList) {
            //inflate itemview
            View itemView = layoutInflater.inflate(R.layout.issues_item_layout, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.bottomMargin = 10;
            itemView.setLayoutParams(layoutParams);

            //set data
            final View issues_layout = itemView.findViewById(R.id.issues_layout);
            final TextView issue_title = (TextView) itemView.findViewById(R.id.issue_title);
            final TextView issue_content = (TextView) itemView.findViewById(R.id.issue_content);
            final ImageView issue_arrow_icon = (ImageView) itemView.findViewById(R.id.issue_arrow_icon);
            issue_title.setText(issue.title);
            issue_content.setText(issue.content.
                    replace("\n", "").
                    replace("\t", "").
                    replace(" ", "").
                    replace("\\hhhhh", "\n").
                    replace("\\sssss", "    ").
                    replace(".", ". "));
            issue_content.setVisibility(View.GONE);
            issues_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    issue_content.setVisibility(issue_content.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    issue_arrow_icon.setImageResource(issue_content.getVisibility() == View.GONE ?
                            R.drawable.icon_arrow_blue_down : R.drawable.icon_arrow_blue_up);
                }
            });

            //add itemview to layout
            issuesLayout.addView(itemView);
        }
    }

    private void initData() {
        try {
            InputStream is = getAssets().open("issues.xml");
            IssueXMLParser parser = new IssueXMLParser();
            dataList = parser.parse(is);
        } catch (Exception e) {
            LogUtils.d(TAG, e.toString());
        }
    }
}
