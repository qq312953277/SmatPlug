package com.phicomm.smartplug.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yun.wang
 * Date :2017/6/30
 * Description: ***
 * Version: 1.0.0
 */
public class StringUtils {

    public static boolean isNull(String s) {
        if (s == null || "".equals(s) || "null".equals(s)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 16进制的字符数组
     */
    private static final String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
            "e", "f"};

    /**
     * 字符串是否为空
     *
     * @param txt
     * @return
     */
    public static boolean isBlank(String txt) {
        return TextUtils.isEmpty(txt);
    }

    /***
     * 时间戳格式化时间串
     *
     * @param seconds 时间戳,以秒为单位
     * @return 格式化后的字符串
     */
    public static String timestamp2FormatBySeconds(long seconds) {
        return timestamp2FormatByMilliSeconds(seconds * 1000);
    }

    /***
     * 时间戳格式化时间串
     *
     * @param ms 时间戳,以毫秒为单位
     * @return 格式化后的字符串
     */
    @SuppressLint("SimpleDateFormat")
    public static String timestamp2FormatByMilliSeconds(long ms) {

        Date date = new Date(ms);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    /**
     * 时间戳格式化，仅保留年月日
     *
     * @param millis
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String timeFomateDate(long millis) {
        return new SimpleDateFormat("yyyy-MM-dd").format(millis);
    }

    /**
     * 格式化时间转长整型时间戳
     *
     * @param format
     * @param time
     * @return 时间戳数值
     */
    @SuppressLint("SimpleDateFormat")
    public static long timeFormat2Timestamp(String format, String time) {
        long ret = 0;
        try {
            ret = new SimpleDateFormat(format).parse(time).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 格式化时间转长整型时间戳 默认时间格式为：yyyy-MM-dd HH:mm:ss,其他格式请使用
     * {@link}转换
     *
     * @param time 格式化时间
     * @return 时间戳数值
     */
    public static long timeFormat2Timestamp(String time) {
        return timeFormat2Timestamp("yyyy-MM-dd HH:mm:ss", time);
    }

    /**
     * 将整形时间长度格式化为字符串HH:mm:ss的形式 当HH为0时，显示为mm:ss的形式
     *
     * @param timeLength
     * @return
     */
    public static String formatTimeLength(int timeLength) {
        StringBuffer voiceLength = new StringBuffer("");
        int divideResult;
        int modResult;

        // 计算HH
        divideResult = timeLength / 3600;
        modResult = timeLength % 3600;
        if (divideResult > 0) {
            voiceLength.append(divideResult + ":");
        }

        // 计算mm
        divideResult = modResult / 60;
        modResult = modResult % 60;
        if (divideResult / 10 > 0) {
            voiceLength.append(divideResult + ":");
        } else {
            voiceLength.append("0").append(divideResult).append(":");
        }

        // 计算ss
        modResult = modResult % 60;
        if (modResult / 10 > 0) {
            voiceLength.append(modResult);
        } else {
            voiceLength.append("0").append(modResult);
        }
        return voiceLength.toString();
    }

    /**
     * 把链接中的中文转换成urlencode编码字符
     *
     * @param url 带有中文信息的链接地址
     * @return 转换后的链接地址
     */
    @SuppressWarnings("checkstyle:AvoidEscapedUnicodeCharacters")
    public static String converChineseToUrlEncode(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            String[] values = path.split("/");
            //CHECKSTYLE:OFF
            Pattern p = Pattern.compile("([\u4e00-\u9fa5]+)");
            //CHECKSTYLE:ON
            StringBuffer strPath = new StringBuffer('/');

            for (String value : values) {
                Matcher m = p.matcher(value);
                if (m.find()) {
                    value = URLEncoder.encode(value, "UTF-8");
                }
                strPath.append(value).append('/');
            }
            strPath.deleteCharAt(strPath.length() - 1);

            url = url.replace(path, strPath.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 判断参数内容是否是手机号码，目前只认13* 14* 15* 17* 18*开头的手机号
     *
     * @param content
     * @return true or false
     */
    public static boolean isPhoneNum(String content) {
        boolean ret = false;
        if (!TextUtils.isEmpty(content)) {
            ret = content.matches("^1[3|4|5|7|8][0-9]\\d{8}$");
        }
        return ret;
    }

    /**
     * 判断是否是中国电信的号段<br>
     * 2G/3G号段（CDMA2000网络）133、153、180、181、189<br>
     * 4G号段 177
     *
     * @param phone
     * @return
     */
    public static boolean isTelecomNumber(String phone) {
        return islegal("133|153|177|178|180|181|189", phone.substring(0, 3));
    }

    /**
     * 判断是否是中国移动的号段<br>
     * 2G号段（GSM网络）有134x（0-8）、135、136、137、138、139、150、151、152、158、159、182、183、184
     * 。<br>
     * 3G号段（TD-SCDMA网络）有157、187、188<br>
     * 3G上网卡 147<br>
     * 4G号段 178<br>
     *
     * @param phone
     * @return
     */
    public static boolean isMobileNumber(String phone) {
        return islegal("135|136|137|138|139|147|150|151|152|157|158|159|170|171|173|178|182|183|184|187|188", phone.substring(0, 3))
                || islegal("134[0-8]", phone.substring(0, 4));
    }

    /**
     * 判断是否是中国联通的号段<br>
     * 2G号段（GSM网络）130、131、132、155、156<br>
     * 3G上网卡145<br>
     * 3G号段（WCDMA网络）185、186<br>
     * 4G号段 176、185<br>
     *
     * @param phone
     * @return
     */
    public static boolean isUnicomNumber(String phone) {
        return islegal("130|131|132|145|155|156|176|178|185|186|189", phone.substring(0, 3));
    }

    /**
     * 判断是否是虚拟号段<br>
     * 170号段为虚拟运营商专属号段，170号段的 11 位手机号前四位来区分基础运营商，其中 “1700” 为中国电信的转售号码标识，“1705”
     * 为中国移动，“1709” 为中国联通。
     *
     * @param phone
     * @return
     */
    public static boolean isVirtualNumber(String phone) {
        return islegal("170[0|5|9]", phone.substring(0, 4));
    }

    /**
     * 判断字符串是否是邮箱地址
     *
     * @param content
     * @return true or false
     */
    public static boolean isEmail(String content) {
        boolean ret = false;
        if (!TextUtils.isEmpty(content)) {
            ret = content.matches("^(?=\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$).{6,30}$");
        }
        return ret;
    }

    /**
     * 判断字符串是否是邮箱地址
     *
     * @param content
     * @return true or false
     */
    public static boolean isRouterEmail(String content) {
        boolean ret = false;
        if (!TextUtils.isEmpty(content)) {
            ret = content.matches("^(?=\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$).{6,64}$");
        }
        return ret;
    }

    /**
     * 单位转化，并保留1位小数
     *
     * @param bt
     * @return
     */
    public static double byteToMb(int bt) {
        double pDouble = bt / 1024 / 1024.0;
        BigDecimal bd = new BigDecimal(pDouble);
        BigDecimal bd1 = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
        pDouble = bd1.doubleValue();

        return pDouble;
    }

    public static String htmlEncode(String s) {
        StringBuilder sb = new StringBuilder();
        char c;
        if (!TextUtils.isEmpty(s)) {
            for (int i = 0; i < s.length(); i++) {
                c = s.charAt(i);
                switch (c) {
                    case '<':
                        sb.append("&lt;"); //$NON-NLS-1$
                        break;
                    case '>':
                        sb.append("&gt;"); //$NON-NLS-1$
                        break;
                    case '&':
                        sb.append("&amp;"); //$NON-NLS-1$
                        break;
                    case '\'':
                        // http://www.w3.org/TR/xhtml1
                        // The named character reference &apos; (the apostrophe,
                        // U+0027) was introduced in
                        // XML 1.0 but does not appear in HTML. Authors should
                        // therefore use &#39; instead
                        // of &apos; to work as expected in HTML 4 user agents.
                        sb.append("&#39;"); //$NON-NLS-1$
                        break;
                    case '"':
                        sb.append("&quot;"); //$NON-NLS-1$
                        break;
                    default:
                        sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    public static String htmlDecode(String s) {
        try {
            s = s.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").replace("&#39;", "\'").replace("&quot;", "\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String getPrice(int price) {
        int cent = price % 100;
        int yuan = price / 100;

        StringBuffer sb = new StringBuffer(yuan + ".");
        if (cent < 10 && cent >= 0) {
            sb.append("0").append(cent);
        } else {
            sb.append(cent);
        }

        return sb.toString();
    }

    public static String formatSize(long size) {
        String sSzie;
        size = size >> 10; // size /= 1024 kb
        if (size < 1024) {
            sSzie = String.format("%d Kb", size);
        } else {
            if (size >> 10 < 1024) { // MB
                sSzie = String.format("%.2f M", size / 1024d);// mb
            } else {
                sSzie = String.format("%.2f GB", size / 2048d);// gb
            }
        }
        return sSzie;
    }

    // 单位转换(万、亿)
    public static String formatCount(int count) {
        String sCount;
        if (count < 10000) {
            sCount = String.valueOf(count);
        } else {
            if (count < 100000000) {
                sCount = String.valueOf(count / 10000) + "万";
            } else {
                sCount = String.valueOf(count / 100000000) + "亿";
            }
        }
        return sCount;
    }

    /**
     * @param patternString 判断条件，为正则表达式
     * @param input         需要进行判断的字符串
     * @return true，符合给定条件；false，不符合给定条件
     */
    public static boolean islegal(String patternString, String input) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取IP地址中每段的数值,也可用于对判断IP地址是否合法，如果返回值为null，则表示是非法的IP地址
     *
     * @param input
     * @return
     */
    public static int[] getIpSplit(String input) {
        if (input == null) {
            return null;
        }
        // if ( input.startsWith(".") || input.endsWith(".") )
        // {
        // return null;
        // }

        String[] numbers = input.split("\\.");
        int length = numbers.length;
        if (length != 4) {
            return null;
        }

        int[] ipSplits = new int[4];

        for (int i = 0; i < length; i++) {
            try {
                ipSplits[i] = Integer.parseInt(numbers[i]);
                if (ipSplits[i] < 0 || ipSplits[i] > 255) {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }

        return ipSplits;
    }

    /**
     * 判断给定字符串是否符合四段式网址规范
     *
     * @param input
     * @return
     */
    public static boolean isIpLegal(String input) {
        if (input == null) {
            return false;
        }
        if (input.startsWith(".") || input.endsWith(".")) {
            return false;
        }

        String[] numbers = input.split("\\.");
        int length = numbers.length;
        if (length != 4) {
            return false;
        }

        String reg = "\\d|[1-9][0-9]|1[0-9][0-9]|2(([0-4][0-9])|(5[0-5]))";

        for (int i = 0; i < length; i++) {
            boolean isIlleagle = islegal(reg, numbers[i]);
            if (!isIlleagle) {
                return false;
            }
        }

        return true;
    }

    public static boolean isLegalSubnet(String input) {
        int[] splits = getIpSplit(input);
        if (splits == null) {
            return false;
        }

        if (splits[0] != 255) {
            return false;
        }

        if (splits[1] != 255) {
            if (splits[2] == 0 && splits[3] == 0
                    && (splits[1] == 128 || splits[1] == 0 || splits[1] == 192 || splits[1] == 224 || splits[1] == 240 || splits[1] == 248 || splits[1] == 252 || splits[1] == 254)) {
                return true;
            } else {
                return false;
            }
        }

        if (splits[2] != 255) {
            if (splits[3] == 0 && (splits[2] == 128 || splits[2] == 0 || splits[2] == 192 || splits[2] == 224 || splits[2] == 240 || splits[2] == 248 || splits[2] == 252 || splits[2] == 254)) {
                return true;
            } else {
                return false;
            }

        }

        if (splits[3] == 128 || splits[3] == 0 || splits[3] == 192 || splits[3] == 224 || splits[3] == 240 || splits[3] == 248 || splits[3] == 252 || splits[3] == 254) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 字符串是否包含大写字母
     *
     * @param input
     * @return
     */
    public static boolean isContainCaptalLetter(String input) {
        Pattern p = Pattern.compile("[\\x41-\\x5a]");// [\\x00-\\x7f]
        // 判断ascii码
        Matcher m = p.matcher(input);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 字符串是否包含小写字母
     *
     * @param input
     * @return
     */
    public static boolean isContainLowerCapital(String input) {
        Pattern p = Pattern.compile("[\\x61-\\x7a]");// [\\x00-\\x7f]
        // 判断ascii码
        Matcher m = p.matcher(input);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 字符串是否包含数字
     *
     * @param input
     * @return
     */
    public static boolean isContainNumber(String input) {
        Pattern p = Pattern.compile("[\\x30-\\x39]");// [\\x00-\\x7f]
        // 判断ascii码
        Matcher m = p.matcher(input);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 字符串是否包含Ascii字母
     *
     * @param input
     * @return
     */
    public static boolean isContainAscii(String input) {
        Pattern p = Pattern.compile("[\\x11-\\x2f]|[\\x3b-\\x40]|[\\x5b-\\x60]|[\\x7a-\\x7e]");// [\\x00-\\x7f]
        // 判断ascii码
        Matcher m = p.matcher(input);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 字符串是否包含非Ascii码编码33-126的字符
     *
     * @param input
     * @return
     */
    public static boolean isContainNonAscii(String input) {
        Pattern p = Pattern.compile("[^\\x11-\\x7e]|[\\s]");// [\\x00-\\x7f]
        // 判断ascii码
        Matcher m = p.matcher(input);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 是否包含非指定的字符   这里指的是云账户密码防呆，a-z,A-Z,0-9,以及_!#$*+-./:;=?@[]^`|
     *
     * @param input
     * @return
     */
    public static boolean isContainNoSpecialStr(String input) {
        Pattern p = Pattern.compile("[^[[\\x41-\\x5a]|[\\x61-\\x7a]|[\\x30-\\x39]|[\\x21]|[\\x5f]" +
                "|[\\x23]|[\\x24]|[\\x2a]|[\\x2b]|[\\x2d]|[\\x2e]|[\\x2f]|[\\x3a]|[\\x3b]|[\\x3d]" +
                "|[\\x3f]|[\\x40]|[\\x5b]|[\\x5d]|[\\x5e]|[\\x60]|[\\x7c]]]");
        Matcher m = p.matcher(input);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 是否包含非指定的字符   这里指的是云账户昵称防呆，中文及a-z,A-Z,0-9,以及_!#$*+-./:;=?@[]^`|
     *
     * @param input
     * @return
     */
    public static boolean isContainNoSpecialNickStr(String input) {
        Pattern p = Pattern.compile("[^[[\u4e00-\u9fa5]|[\\x41-\\x5a]|[\\x61-\\x7a]|[\\x30-\\x39]|[\\x5f]|[\\x2d]]]");
        Matcher m = p.matcher(input);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 字符串是否包含空格等字符
     *
     * @param input
     * @return
     */
    public static boolean isContainBlankSpace(String input) {
        Pattern p = Pattern.compile("[\\s]");// [\\x00-\\x7f]
        // 判断ascii码
        Matcher m = p.matcher(input);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * @param input
     * @return
     */
    public static String getStringRemoveChinese(String input) {
        StringBuffer inputRemoveChinese = new StringBuffer("");

        String pattern = "[\u4e00-\u9fa5]"; // 中文
        int inputLength = input.length();
        String temp = "";
        for (int i = 0; i < inputLength; i++) {
            temp = String.valueOf(input.charAt(i));
            if (!temp.matches(pattern)) {
                inputRemoveChinese.append(temp);
            }
        }

        return inputRemoveChinese.toString();

    }

    /**
     * 获取字符串中中文字符的长度
     *
     * @param input
     * @return
     */
    public static int getChineseLength(String input) {
        String pattern = "[\u4e00-\u9fa5]"; // 中文

        int chineseCount = 0;
        String temp;
        for (int i = 0; i < input.length(); i++) {
            temp = String.valueOf(input.charAt(i));
            if (temp.matches(pattern)) {
                chineseCount++;
            }
        }

        return chineseCount;
    }

    public static boolean isContainChinese(String str) {
        //CHECKSTYLE:OFF
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        //CHECKSTYLE:ON
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 是否包含特殊字符\'"&<>()%_~等(适用通用字符列表,不支持)
     *
     * @param input
     * @return
     */
    public static boolean isContainSpecialCharSet4(String input) {
        Pattern p = Pattern.compile("[\\\\\"\'_~&<()>%{}]"); // [\\x00-\\x7f]
        // 判断ascii码
        Matcher m = p.matcher(input);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * Compares two version strings.
     * Use this instead of String.compareTo() for a non-lexicographical
     * Comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     *
     * @param str1
     * @param str2
     * @return
     */
    public static int versionCompare(String str1, String str2) {
        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.signum(vals1.length - vals2.length);
    }

    /**
     * 获取md5加密后的结果
     *
     * @param content
     * @return
     */
    public static String getMD5(String content) {
        return MD5Encode(content, true);
    }

    /**
     * @param source    需要加密的原字符串
     * @param uppercase 是否转为大写字符串
     * @return
     */
    public static String MD5Encode(String source, boolean uppercase) {
        String result = null;
        try {
            result = source;
            // 获得MD5摘要对象
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 使用指定的字节数组更新摘要信息
            messageDigest.update(result.getBytes());
            // messageDigest.digest()获得16位长度
            result = byteArrayToHexString(messageDigest.digest());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return uppercase ? result.toUpperCase() : result;
    }

    /**
     * 转换字节数组为16进制字符串
     *
     * @param bytes 字节数组
     * @return
     */
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte tem : bytes) {
            stringBuilder.append(byteToHexString(tem));
        }
        return stringBuilder.toString();
    }

    /**
     * 转换byte到16进制
     *
     * @param b 要转换的byte
     * @return 16进制对应的字符
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 去掉字符串中的特殊字符
     *
     * @param string
     * @return
     */
    public static String removeSpecialCharacters(String string) {
        String result;
        result = string.replaceAll("[-+.^:,@*&%$!#]", "");
        return result;
    }

    /**
     * 去掉所有空格
     *
     * @param str
     * @return
     */
    public static String removeAllSpace(String str) {
        String result;
        result = str.replace(" ", "");
        return result;
    }

    /**
     * 将数组转化为16进制
     *
     * @param bArray
     * @return
     */
    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转化为数组
     *
     * @param data
     * @return
     */
    public static byte[] stringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int intCh;  // 两位16进制数转化后的10进制数
            char hexChar1 = hexString.charAt(i); ////两位16进制数中的第一位(高位*16)
            int intCh1;
            if (hexChar1 >= '0' && hexChar1 <= '9') {
                intCh1 = (hexChar1 - 48) * 16;   //// 0 的Ascll - 48
            } else if (hexChar1 >= 'A' && hexChar1 <= 'F') {
                intCh1 = (hexChar1 - 55) * 16; //// A 的Ascll - 65
            } else {
                return null;
            }
            i++;
            char hexChar2 = hexString.charAt(i); ///两位16进制数中的第二位(低位)
            int intCh2;
            if (hexChar2 >= '0' && hexChar2 <= '9') {
                intCh2 = (hexChar2 - 48); //// 0 的Ascll - 48
            } else if (hexChar2 >= 'A' && hexChar2 <= 'F') {
                intCh2 = hexChar2 - 55; //// A 的Ascll - 65
            } else {
                return null;
            }
            intCh = intCh1 + intCh2;
            retData[i / 2] = (byte) intCh;//将转化后的数放入Byte里
        }
        return retData;
    }
}
