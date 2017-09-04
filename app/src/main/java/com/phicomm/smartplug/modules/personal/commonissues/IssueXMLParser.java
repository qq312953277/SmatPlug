package com.phicomm.smartplug.modules.personal.commonissues;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yun.wang on 2017/8/18 0018.
 */

public class IssueXMLParser {

    /**
     * 解析输入流 得到IssueItem对象集合
     *
     * @param is
     * @return
     * @throws Exception
     */

    public List<IssueItem> parse(InputStream is) throws Exception {
        List<IssueItem> IssueItems = null;
        IssueItem IssueItem = null;

//      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//      XmlPullParser parser = factory.newPullParser();

        XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例
        parser.setInput(is, "UTF-8");               //设置输入流 并指明编码方式

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    IssueItems = new ArrayList<IssueItem>();
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("issue")) {
                        IssueItem = new IssueItem();
                    } else if (parser.getName().equals("id")) {
                        eventType = parser.next();
                        IssueItem.setId(Integer.parseInt(parser.getText()));
                    } else if (parser.getName().equals("title")) {
                        eventType = parser.next();
                        IssueItem.setTitle(parser.getText());
                    } else if (parser.getName().equals("content")) {
                        eventType = parser.next();
                        IssueItem.setContent(parser.getText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("issue")) {
                        IssueItems.add(IssueItem);
                        IssueItem = null;
                    }
                    break;
            }
            eventType = parser.next();
        }
        return IssueItems;
    }
}
