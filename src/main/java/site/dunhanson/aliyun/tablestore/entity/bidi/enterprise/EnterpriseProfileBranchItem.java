package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/28 11:14
 * @description TODO
 */
@Setter
@Getter
@ToString
public class EnterpriseProfileBranchItem {
    //拥有公司个数
    private Long toco;
    //logo
    private String logo;
    //简称
    private String alias;
    //企业状态
    private String regStatus;
    //1-公司 2-人
    private Integer  type;
    //开业时间
    private String estiblishTime;
    //评分
    private Long pencertileScore;
    //法人id
    private Long legalPersonId;
    //法人
    private String legalPersonName;
    //tycId
    private Long tycId;
    //行业code
    private String category;
    //注册资金
    private String regCapital;
    //公司名
    private String name;
    //省份
    private String province;
    //法人类型 1-人 2-公司
    private Long personType;
}
