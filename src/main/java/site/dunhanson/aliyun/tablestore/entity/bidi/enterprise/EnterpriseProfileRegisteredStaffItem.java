package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/6/5 9:12
 * @description 工程人员列表
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileRegisteredStaffItem {
    // 姓名
    private String name;
    // 身份证号
    private String identityCode;
    // 注册类别
    private String registrationType;
    // 注册号（执业印章号）
    private String registrationCode;
    // 注册专业
    private String registrationSpeciality;

}
