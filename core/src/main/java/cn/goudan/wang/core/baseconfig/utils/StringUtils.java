package cn.goudan.wang.core.baseconfig.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.regex.Pattern;

public class StringUtils {

    public static final String UTF8 = "UTF-8";
    public static final String GB2312 = "GB2312";
    public static final String GBK = "GBK";

    public enum CaseMode {
        Capitalize, // 首字母大写
        Lower, // 小写
        Upper, // 大写
    }

    /**
     * 判断是否中文
     *
     * @param val
     * @return
     */
    public static Boolean isChinese(String val) {

        return Pattern.compile("[\u4e00-\u9fa5]").matcher(val).find();
    }

    /**
     * 字符串转换成拼音
     *
     * @param val
     * @return
     */
    public static String getPinyin(String val) {

        return getPinyin(val, CaseMode.Capitalize, " ");
    }

    /**
     * 字符串转换成拼音
     *
     * @param val
     * @param mode
     * @param separator
     * @return
     */
    public static String getPinyin(String val, CaseMode mode, String separator) {

        StringBuilder sb = new StringBuilder();

        if (val != null) {

            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            char[] chars = val.toCharArray();

            for (char c : chars) {

                try {
                    String[] results = PinyinHelper.toHanyuPinyinStringArray(c, format);

                    if (results == null || results.length <= 0) {
                        sb.append(c);
                    } else {
                        sb.append(mode == CaseMode.Upper ? results[0].toUpperCase() : mode == CaseMode.Lower ? results[0].toLowerCase() : org.apache.commons.lang3.StringUtils.capitalize(results[0])).append(separator);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
            }
        }

        return sb.toString().trim();
    }

    /**
     * 得到字符串拼音首字符
     *
     * @param val
     * @return
     */
    public static String getPinyinInitial(String val) {

        return getPinyinInitial(val, CaseMode.Upper);
    }

    /**
     * 得到字符串拼音首字符
     *
     * @param val
     * @param mode
     * @return
     */
    public static String getPinyinInitial(String val, CaseMode mode) {

        String result = "";

        if (val != null) {

            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            char[] chars = val.toCharArray();

            try {
                String[] results = PinyinHelper.toHanyuPinyinStringArray(chars[0], format);
                result = String.valueOf(results == null || results.length <= 0 ? chars[0] : results[0].toCharArray()[0]);
                result = mode == CaseMode.Lower ? result.toLowerCase() : result.toUpperCase();
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 解决字符串中转义双引号的问题
     *
     * @param val
     * @return
     */
    public static String escapeQuotes(String val) {

        return val == null ? null : org.apache.commons.lang3.StringUtils.replace(val, "\"", "“");
    }

    public static String formatId(Integer id) {

        if (null == id) return "";

        return String.format("%08d", id);
    }

    /**
     * 将驼峰式命名的字符串转换为下划线大写方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。</br>
     * 例如：HelloWorld->HELLO_WORLD
     * @param name 转换前的驼峰式命名的字符串
     * @return 转换后下划线大写方式命名的字符串
     */
    public static String underscoreName(String name) {
        StringBuilder result = new StringBuilder();
        if (name != null && name.length() > 0) {
            // 将第一个字符处理成大写
            result.append(name.substring(0, 1).toUpperCase());
            // 循环处理其余字符
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                // 在大写字母前添加下划线
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_");
                }
                // 其他字符直接转成大写
                result.append(s.toUpperCase());
            }
        }
        return result.toString();
    }

    /**
     * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。</br>
     * 例如：HELLO_WORLD->HelloWorld
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String camelName(String name) {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (name == null || name.isEmpty()) {
            // 没必要转换
            return "";
        } else if (!name.contains("_")) {
            // 不含下划线，仅将首字母小写
            return name.substring(0, 1).toLowerCase() + name.substring(1);
        }
        // 用下划线将原始字符串分割
        String camels[] = name.split("_");
        for (String camel :  camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty()) {
                continue;
            }
            // 处理真正的驼峰片段
            if (result.length() == 0) {
                // 第一个驼峰片段，全部字母都小写
                result.append(camel.toLowerCase());
            } else {
                // 其他的驼峰片段，首字母大写
                result.append(camel.substring(0, 1).toUpperCase());
                result.append(camel.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }
}
