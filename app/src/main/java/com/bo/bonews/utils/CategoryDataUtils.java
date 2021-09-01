package com.bo.bonews.utils;

import com.bo.bonews.bean.ProjectChannelBean;
import com.bo.bonews.i.APPConst;

import java.util.ArrayList;
import java.util.List;


public class CategoryDataUtils {
    public static List<ProjectChannelBean> getChannelCategoryBeans() {
        List<ProjectChannelBean> beans = new ArrayList<>();
        beans.add(new ProjectChannelBean("推荐", APPConst.TID_TUIJIAN));
        beans.add(new ProjectChannelBean("国内", APPConst.TID_GUONEI));
        beans.add(new ProjectChannelBean("电影", APPConst.TID_DIANYIN));
        beans.add(new ProjectChannelBean("综艺", APPConst.TID_ZONGYI));
        return beans;
    }
}
