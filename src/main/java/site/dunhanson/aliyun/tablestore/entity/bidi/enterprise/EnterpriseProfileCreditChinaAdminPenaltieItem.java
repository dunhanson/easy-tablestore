package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/24 10:53
 * @description 行政处罚--信息中国信息（信用中国）
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileCreditChinaAdminPenaltieItem {
    //决定书文号
    private String penaltyNum;
    //处罚名称
    private String penaltyName;
    //企业法人
    private String legalPerson;
    //处罚类别
    private String penaltyType;
    //处罚结果
    private String penaltyResult;
    //处罚事由
    private String penaltyCause;
    //处罚依据
    private String penaltyBasis;
    //处罚机关
    private String penaltyAuthority;
    //处罚决定日期(STRING|yyyy-mm-dd)
    private String penaltyDate;
    //处罚期限(STRING|yyyy-mm-dd)
    private String penaltyValidPeriod;
    //数据更新日期(STRING|yyyy-mm-dd)
    private String updateDate;
}

