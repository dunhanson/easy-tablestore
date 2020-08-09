package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;


import lombok.Data;

/**
 * 合作案例
 * @author cjr
 * @date 2019/10/14
 */
@Data
public class AgentEventInfo {

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 委托单位
     */
    private String entrustUnit;

    /**
     * 采购文件编号
     */
    private String bidcalNo;

    /**
     * 中标单位
     */
    private String bidconfirmUnit;

    /**
     * 通知发出时间
     */
    private String noticeDate;

    /**
     * 金额
     */
    private String bidconfirmAmount;

}
