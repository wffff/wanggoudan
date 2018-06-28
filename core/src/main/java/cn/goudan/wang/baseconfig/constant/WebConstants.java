package cn.goudan.wang.baseconfig.constant;

/**
 * Created by Adam.yao on 2017/10/25.
 */
public class WebConstants {
    public static final String INDEX_PAGE = "index";
    public static final String FORM_PAGE = "fragments/form";
    public static final String MAIN_PAGE = "main";
    public static final Integer DEF_PAGE = 1;
    public static final Integer DEF_SIZE = 20;

    public static Integer validatePage(Integer page) {
        return page == null || page <= 0 ?DEF_PAGE:page;
    }
    public static Integer validateSize(Integer size) {
        return size == null || size <= 0 ?DEF_SIZE:size;
    }
}
