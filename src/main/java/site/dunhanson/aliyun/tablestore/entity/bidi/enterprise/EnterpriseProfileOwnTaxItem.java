package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/28 11:16
 * @description TODO
 */
@Setter
@Getter
@ToString
public class EnterpriseProfileOwnTaxItem {
    //注册类型
    private String regType;
    //证件号码
    private String personIdNumber;
    //法人或负责人名称
    private String legalpersonName;
    //经营地点
    private String location;
    //欠税金额
    private String ownTaxAmount;
    //部门
    private String department;
    //纳税人识别号
    private String taxIdNumber;
    //纳税人类型
    private String type;
    //税务类型
    private String taxCategory;
    //欠税税种
    private String taxpayerType;
    //当前新发生欠税余额
    private String newOwnTaxBalance;
    //欠税余额
    private String ownTaxBalance;
    //纳税人名称
    private String name;
    //法人证件名称
    private String personIdName;
    //发布时间
    private String publishDate;

}
