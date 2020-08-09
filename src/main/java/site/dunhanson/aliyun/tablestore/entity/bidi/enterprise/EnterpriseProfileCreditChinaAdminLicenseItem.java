package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/24 10:53
 * @description 行政许可--信息中国信息（信用中国）
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileCreditChinaAdminLicenseItem {
    //行政许可决定书文号
    private String licenseNum;
    //审核类型
    private String licenseType;
    //企业法人
    private String legalPerson;
    //内容许可
    private String licenseContent;
    //许可有效期(STRING|yyyy-mm-dd)
    private String licenseValidPeriod;
    //许可决定日期(STRING|yyyy-mm-dd)
    private String licensingDate;
    //许可截止日期(STRING|yyyy-mm-dd)
    private String expiryDate;
    //地方编码
    private String localCode;
    //许可机关
    private String licensingInstitution;
    //数据更新日期(STRING|yyyy-mm-dd)
    private String updateDate;
}

