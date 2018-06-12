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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SpannableString 工具
 *
 * @author whamu2
 * @date 2018/6/11
 */
public class SpannableStringManager {
    private static final String TAG = SpannableStringManager.class.getSimpleName();

    public static class PatternRegex {
        /**
         * Number
         */
        static final String NUM_REGEX = "^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$";
        /**
         * Expression
         * 表情[大笑]
         */
        static final String EXPRESSION_PATTERN = "\\[[^\\]]+\\]";
        /**
         * web site
         */
        static final String URL_PATTERN = "(([hH]ttp[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";
    }

    /**
     * 点击关键文字事件监听
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
     * @return SpannableString
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
     * @return SpannableString
     */
    public static SpannableString getKeyWordSpanArray(@NonNull String content, HashMap<String, Integer> map) {
        SpannableString builder = new SpannableString(content);

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
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
     * @param l       点击事件 {@link SpanClickListener}
     * @return SpannableString
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
     * @param spanClickListener 点击事件 {@link SpanClickListener}
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
     * @param text   文本内容
     * @param key    关键字
     * @param bColor 背景
     * @param tColor 字体颜色
     * @return
     */
    public static SpannableStringBuilder getSpanTextBackground(@NonNull String text, @NonNull String key, @ColorInt int bColor, @ColorInt int tColor) {
        int start = text.indexOf(key);
        int end = start + key.length();
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        builder.setSpan(new BackgroundColorSpan(bColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 背景
        builder.setSpan(new ForegroundColorSpan(tColor), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE); // 颜色
        return builder;
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
     * 判断网址是否合法
     *
     * @param val website
     * @return true/false
     */
    public static boolean isLegalWebSite(String val) {
        Pattern pattern = Pattern.compile(PatternRegex.URL_PATTERN);
        return pattern.matcher(val).matches();
    }
}
