package site.dunhanson.aliyun.tablestore.entity;

import lombok.Data;


@Data
public class SubDocument {

    /**
     * 子项目名称
     */
    private String subProjectName;

    /**
     * 子项目编码
     */
    private String subProjectCode;

    /**
     * 预算价格
     */
    private String biddingBudget;

    /**
     * 中标人
     */
    private String winTenderer;

    /**
     * 中标价格
     */
    private String winBidPrice;

    /**
     * 中标管理人
     */
    private String winTendererManager;

    /**
     * 中标管理人电话
     */
    private String winTendererPhone;

    /**
     * 第二候选人
     */
    private String secondTenderer;

    /**
     * 第二候选人价格
     */
    private String secondBidPrice;

    /**
     * 第二候选人管理人
     */
    private String secondTendererManager;

    /**
     * 第二候选人管理人电话
     */
    private String secondTendererPhone;

    /**
     * 第三候选人
     */
    private String thirdTenderer;

    /**
     * 第三候选人价格
     */
    private String thirdBidPrice;

    /**
     * 第三候选人管理人
     */
    private String thirdTendererManager;

    /**
     * 第三候选人管理人电话
     */
    private String thirdTendererPhone;


}
