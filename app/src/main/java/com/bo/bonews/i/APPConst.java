package com.bo.bonews.i;


public class APPConst {
    private APPConst() {
    }

    // 设置ChannelManager频道管理中的每个item的间隔
    public static final int ITEM_SPACE = 5;

    // 0和1均表示ChannelManager频道管理中的tab不可可编辑
    // 其中tab的type为0时，字体会显示红色， 为1时会显示灰色
    public static final int ITEM_DEFAULT = 0;
    // 1均表示ChannelManager频道管理中的tab不可可编辑
    public static final int ITEM_UNEDIT = 1;
    // 表示ChannelManager频道管理中的tab可编辑
    public static final int ITEM_EDIT = 2;

    // 国内频道
    public static final String TID_GUONEI = "T000000";
    //推荐频道
    public static final String TID_TUIJIAN = "T111111";
    // 电影频道
    public static final String TID_DIANYIN = "T222222";
    //综艺频道
    public static final String TID_ZONGYI = "T333333";
}
