package com.github.whamu2.android.ssm;

import android.annotation.SuppressLint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author whamu2
 * @date 2018/6/11
 */
public class SpannableStringManager {
    private static final String TAG = "SpannableStringMannager";

    public static class PatternRegex {
        /**
         * 数字
         */
        public static final String NUM_REGEX = "^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$";
        /**
         * 表情[大笑]
         */
        public static final String EXPRESSION_PATTERN = "\\[[^\\]]+\\]";
        /**
         * 网址
         */
        public static final String URL_PATTERN = "(([hH]ttp[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";

    }

    /**
     * 点击监听
     */
    public interface SpanClickListener {
        void onSpanClick();
    }

    /**
     * 关键词变色处理
     *
     * @param color   色值
     * @param content 文本内容
     * @param key     关键字
     * @return
     */
    public static SpannableString getKeyWordSpan(@ColorInt int color, @NonNull String content, @NonNull String key) {
        SpannableString spannableString = new SpannableString(content);
        @SuppressLint("WrongConstant") Pattern patten = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
        try {
            dealPattern(color, spannableString, patten, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableString;
    }

    /**
     * 对spanableString进行正则判断，如果符合要求，则将内容变色
     *
     * @param color           色值
     * @param spannableString SpannableString
     * @param patten          正则
     * @param start           起始位置
     * @throws Exception
     */
    private static void dealPattern(@ColorInt int color, SpannableString spannableString, Pattern patten, int start) throws Exception {
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            // 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
            if (matcher.start() < start) {
                continue;
            }
            // 计算该内容的长度，也就是要替换的字符串的长度
            int end = matcher.start() + key.length();
            //设置前景色span
            spannableString.setSpan(new ForegroundColorSpan(color), matcher.start(), end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (end < spannableString.length()) {
                // 如果整个字符串还未验证完，则继续。。
                dealPattern(color, spannableString, patten, end);
            }
            break;
        }
    }

    /**
     * 多处字体设置不同颜色
     *
     * @param content 文本内容
     * @param map     关键字和色值
     * @return
     */
    public static SpannableString getKeyWordSpanArray(@NonNull String content, HashMap<String, Integer> map) {
        SpannableString builder = new SpannableString(content);

        final Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, Integer> entry = iterator.next();
            final String key = entry.getKey();
            final Integer value = entry.getValue();

            @SuppressLint("WrongConstant") Pattern patten = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
            try {
                dealPattern(value, builder, patten, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return builder;
    }

    /**
     * 自动识别关键字并做颜色处理,可点击
     *
     * @param color   色值
     * @param content 文本内容
     * @param key     关键字
     * @param l       点击事件
     * @return
     */
    public static SpannableString getClickSpan(@ColorInt int color, @NonNull String content, @NonNull String key, boolean isUnderline, SpanClickListener l) {
        SpannableString spannableString = new SpannableString(content);
        @SuppressLint("WrongConstant") Pattern patten = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
        dealClick(color, spannableString, patten, 0, isUnderline, l);
        return spannableString;
    }

    /**
     * 对spanableString进行正则判断，如果符合要求，将内容设置可点击
     *
     * @param color             色值
     * @param spannableString   SpannableString
     * @param patten            正则
     * @param start             起始位置
     * @param spanClickListener 点击事件
     */
    private static void dealClick(final int color, SpannableString spannableString, Pattern patten, int start, boolean isUnderline, final SpanClickListener spanClickListener) {
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            if (matcher.start() < start) {
                continue;
            }
            int end = matcher.start() + key.length();
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    spanClickListener.onSpanClick();
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(isUnderline);// 默认有下划线
                    ds.setColor(color);
                }
            }, matcher.start(), end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (end < spannableString.length()) {
                dealClick(color, spannableString, patten, end, isUnderline, spanClickListener);
            }
            break;
        }
    }

    /**
     * 设置文字背景
     *
     * @param content 文本内容
     * @param key     关键字
     * @param bColor  背景
     * @param tColor  字体颜色
     * @return
     */
    public static SpannableStringBuilder getSpanTextBackground(@NonNull String content, @NonNull String key, @ColorInt int bColor, @ColorInt int tColor) {
        int start = content.indexOf(key);
        int end = start + key.length();
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        builder.setSpan(new BackgroundColorSpan(bColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 背景
        builder.setSpan(new ForegroundColorSpan(tColor), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE); // 颜色
        return builder;
    }


    // 定义分割常量 （#在集合中的含义是每个元素的分割，|主要用于map类型的集合用于key与value中的分割）
    private static final String SEP1 = ",";
    private static final String SEP2 = "|";

    /**
     * List转换String
     *
     * @param list 需要转换的List
     * @return String转换后的字符串
     */
    public static String ListToString(List<?> list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null || list.get(i) == "") {
                    continue;
                }
                // 如果值是list类型则调用自己
                if (list.get(i) instanceof List) {
                    sb.append(ListToString((List<?>) list.get(i)));
                    sb.append(SEP1);
                } else if (list.get(i) instanceof Map) {
                    sb.append(MapToString((Map<?, ?>) list.get(i)));
                    sb.append(SEP1);
                } else {
                    sb.append(list.get(i));
                    sb.append(SEP1);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Map转换String
     *
     * @param map 需要转换的Map
     * @return String转换后的字符串
     */
    public static String MapToString(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        // 遍历map
        for (Object obj : map.keySet()) {
            if (obj == null) {
                continue;
            }
            Object value = map.get(obj);
            if (value instanceof List<?>) {
                sb.append(obj.toString()).append(SEP1).append(ListToString((List<?>) value));
                sb.append(SEP2);
            } else if (value instanceof Map<?, ?>) {
                sb.append(obj.toString()).append(SEP1).append(MapToString((Map<?, ?>) value));
                sb.append(SEP2);
            } else {
                sb.append(obj.toString()).append(SEP1).append(value.toString());
                sb.append(SEP2);
            }
        }
        return "M" + sb.toString();
    }

    /**
     * String转换Map
     *
     * @param mapText 需要转换的字符串
     * @return Map<?       ,       ?>
     */
    public static Map<String, Object> StringToMap(String mapText) {

        if (mapText == null || mapText.equals("")) {
            return null;
        }
        mapText = mapText.substring(1);

        Map<String, Object> map = new HashMap<>();
        String[] text = mapText.split("\\" + SEP2); // 转换为数组
        for (String str : text) {
            String[] keyText = str.split(SEP1); // 转换key与value的数组
            if (keyText.length < 1) {
                continue;
            }
            String key = keyText[0]; // key
            String value = keyText[1]; // value
            if (value.charAt(0) == 'M') {
                Map<?, ?> map1 = StringToMap(value);
                map.put(key, map1);
            } else if (value.charAt(0) == 'L') {
                List<?> list = StringToList(value);
                map.put(key, list);
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * String转换List
     *
     * @param listText 需要转换的文本
     * @return List<?>
     */
    public static List<Object> StringToList(String listText) {
        if (listText == null || listText.equals("")) {
            return null;
        }
        listText = listText.substring(1);

        List<Object> list = new ArrayList<>();
        String[] text = listText.split(SEP1);
        for (String str : text) {
            if (str.charAt(0) == 'M') {
                Map<?, ?> map = StringToMap(str);
                list.add(map);
            } else if (str.charAt(0) == 'L') {
                List<?> lists = StringToList(str);
                list.add(lists);
            } else {
                list.add(str);
            }
        }
        return list;
    }


    /**
     * 判断是否是数字
     *
     * @param val 文本
     * @return boolean
     */
    public static boolean isNumeric(String val) {
        Pattern pattern = Pattern.compile(PatternRegex.NUM_REGEX);
        return pattern.matcher(val).matches();
    }

    /**
     * 在文字上画线
     *
     * @param val value
     * @return SpannableString
     */
    public static SpannableString getStrikethroughSpan(String val) {
        SpannableString sp = new SpannableString(val);
        sp.setSpan(new StrikethroughSpan(), 0, val.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }
}
