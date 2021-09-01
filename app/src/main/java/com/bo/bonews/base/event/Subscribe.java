package com.bo.bonews.base.event;

import com.bo.bonews.base.event.inner.ThreadMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: 接收事件注解，必须在接收事件地方定制该注解
 *
 * @date: 2016-12-29 19:03
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {
    ThreadMode threadMode() default ThreadMode.MAIN_THREAD;
}
