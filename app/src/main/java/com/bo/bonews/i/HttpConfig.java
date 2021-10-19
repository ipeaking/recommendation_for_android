package com.bo.bonews.i;

public interface HttpConfig {
    /**
     * base url
     */
    String BASE_URL = "http://192.168.1.186:10086/";

    /**
     * 注册
     */
    String REGISTER = "recommendation/register";

    /**
     * 登陆
     */
    String LONGIN = "recommendation/login";

    /**
     * 点赞
     */
    String LIKES = "recommendation/likes";
    String READ = "recommendation/read";

    /**
     * 收藏
     */
    String COLLECTIONS = "recommendation/collections";

    /**
     * 获取点赞列表
     */
    String GET_LIKES = "recommendation/get_likes";

    /**
     * 获取收藏列表
     */
    String GET_COLLECTIONS = "recommendation/get_collections";


    /**
     * 获取推荐列表
     */
    String GET_REC_LIST = "recommendation/get_rec_list";

}