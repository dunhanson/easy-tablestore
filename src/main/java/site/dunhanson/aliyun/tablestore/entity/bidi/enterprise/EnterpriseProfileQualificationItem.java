package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/6/5 9:03
 * @description 资质列表
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileQualificationItem {

    //资质类别
    private String qualificationType;
    //资质证书号
    private String qualificationCertificateCode;
    //资质名称
    private String qualificationName;
    //发证日期 yyyy-mm-dd
    private String certificatingDate;
    //证书有效期 yyyy-mm-dd
    private String certificateExpiryDate;
    //发证机关
    private String certificatingInstitution;
}
