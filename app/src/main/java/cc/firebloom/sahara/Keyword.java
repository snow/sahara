package cc.firebloom.sahara;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by snowhs on 20141220.
 */
public class Keyword {
    private static final String PLACEHOLDER_REQ_LINK = "__REQ_LN__";
    private static final Pattern LINK_PATTERN = Pattern.compile("\\+?[\\d-\\s]{5," +
            "" + "}|[a-z0-9-]+\\.[a-z]{2,3}\\b");
//    private static final String LIST_FILE = "keyword_black_list.txt";

    private static Keyword sInst;

    private Context mContext;
    private ArrayList<String> mBlackList;
    private ArrayList<String> mWhiteList;

    private Keyword(Context context) {
        mContext = context;
    }

    public static Keyword getInst(Context context) {
        if (null == sInst) {
            sInst = new Keyword(context);
        }

        return sInst;
    }

    private ArrayList<String> list() {
        if (null == mBlackList) {
            mBlackList = new ArrayList<>();
        }

        if (0 == mBlackList.size()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(mContext
                    .getResources().openRawResource(R.raw.keyword_black_list)))) {
                for (String line; (line = br.readLine()) != null; ) {
                    mBlackList.add(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mBlackList;
    }

    private ArrayList<String> getWhiteList() {
        if (null == mWhiteList) {
            mWhiteList = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(mContext
                    .getResources().openRawResource(R.raw.keyword_white_list)))) {
                for (String line; (line = br.readLine()) != null; ) {
                    mWhiteList.add(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mWhiteList;
    }

    public String match(String text) {
        // this line break url pattern
        //text = text.replaceAll("\\s+", "").replaceAll("\\p{Punct}", "").
        //    replaceAll(REGEX_ZHCN_PUNCT, "");
        text = text.toLowerCase();

        for (String kw : getWhiteList()) {
            Matcher m = Pattern.compile(kw).matcher(text);
            if (m.find()) {
                return null;
            }
        }

        for (String kw : list()) {
            boolean isLinkRequired = kw.contains(PLACEHOLDER_REQ_LINK);
            Pattern p;
            if (isLinkRequired) {
                p = Pattern.compile(kw.replace(PLACEHOLDER_REQ_LINK, ""));
            } else {
                p = Pattern.compile(kw);
            }

            Matcher m = p.matcher(text);
            if (m.find()) {
                if (isLinkRequired) {
                    Matcher linkMatcher = LINK_PATTERN.matcher(text);
                    if (linkMatcher.find()) {
                        return kw;
                    }
                } else {
                    return kw;
                }
            }
        }

        return null;
    }
}
