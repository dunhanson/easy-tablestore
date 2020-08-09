package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/28 11:15
 * @description TODO
 */
@Getter
@Setter
@ToString

public class EnterpriseProfileCreditChinaItem{
    //处罚结果
    private String result;
    //区域
    private String areaName;
    //行政处罚决定书文号
    private String punishNumber;
    //处罚事由
    private String reason;
    //处罚类别2
    private String typeSecond;
    //处罚依据
    private String evidence;
    //处罚状态
    private String punishStatus;
    //作出行政处罚决定日期
    private String decisionDate;
    //处罚类别
    private String type;
    //作出行政处罚决定机关名称
    private String departmentName;
    //处罚名称
    private String punishName;

}
