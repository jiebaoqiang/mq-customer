package com.jhh.mq.common;

/**
 * 2018/11/16.
 */
public class MqConstant {

    // 死信的交换机名
    public final static String DEAD_LETTER_EXCHANGE="retryBorrowQuery_dead_exchange";

    //死信路由
    public final static String DEAD_LETTER_ROUTING="retryBorrowQuery_dead_routing";

    //同步合同重试路由
    public final static String RETRY_LETTER_ROUTING="retryBorrowQuery_routing";

    public final static String BORROW_LETTER_ROUTING = "borrowQuery";
}
