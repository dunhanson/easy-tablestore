package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/28 11:17
 * @description TODO
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileZhixingInfoItem {
    //案号
    private String caseCode;
    //执行法院
    private String execCourtName;
    //被执行人名称
    private String pname;
    //身份证号／组织机构代码
    private String partyCardNum;
    //创建时间
    private String caseCreateTime;
    //执行标的
    private String execMoney;
}
