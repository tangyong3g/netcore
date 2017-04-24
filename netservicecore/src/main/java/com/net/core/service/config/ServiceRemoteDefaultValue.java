package com.net.core.service.config;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.net.core.BuildConfig;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by tyler.tang on 2017/4/13.
 * <p>
 * <p>
 * 读取xml文件值
 */
public class ServiceRemoteDefaultValue {


    private static final String TAG = "ServiceConfig";
    // We don't use namespaces
    private static final String ns = null;

    /**
     * 从xml文件中读取默认值
     *
     * @param resId
     */
    public void setDefaultValue(int resId, Context context) throws XmlPullParserException, IOException {

        InputStream is = context.getAssets().open("default_value.xml");
        /*
        TODO 这个方法不行，目前找不到原因
        XmlPullParser parser = mContext.getResources().getXml(R.xml.default_value_2);
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        if (parser != null) {
            parser.nextTag();
        }
        List list = readFeed(parser);
        */
        parse(is);
    }

    protected List setDefaultValueFromFile(String fileName, int flag, Context context) throws XmlPullParserException, IOException {
        if(BuildConfig.DEBUG){
            Log.i(TAG,"start read default value from "+fileName);
        }
        InputStream is = context.getAssets().open(fileName);
        return parse(is);
    }

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        if(BuildConfig.DEBUG){
            Log.i(TAG,"start read  default file readFeed");
        }
        List entries = new ArrayList();
        parser.require(XmlPullParser.START_TAG, ns, "defaultsMap");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("entry")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }

        if(BuildConfig.DEBUG){
            Log.i(TAG,"read default file finish  size is:\t"+entries.size());
        }
        return entries;
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String key = null;
        String value = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("key")) {
                key = readKey(parser);
            } else if (name.equals("value")) {
                value = readValue(parser);
            } else {
                skip(parser);
            }
        }
        Entry entry = new Entry(key, value);
        Log.i("tyler.tang", entry.toString());
        return entry;
    }

    // Processes title tags in the feed.
    private String readKey(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "key");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "key");
        return title;
    }

    // Processes title tags in the feed.
    private String readValue(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "value");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "value");
        return title;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }


    /**
     * 健 值  Entry
     */
    class Entry {

        String mKey;
        String mValue;

        Entry(String key, String value) {
            this.mKey = key;
            this.mValue = value;
        }

        public String toString() {
            return mValue + ":\t" + mKey;
        }
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
