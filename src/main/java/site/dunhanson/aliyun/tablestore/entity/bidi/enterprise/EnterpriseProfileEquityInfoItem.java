package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/28 11:15
 * @description TODO
 */
@Setter
@Getter
@ToString

public class EnterpriseProfileEquityInfoItem{
    //出质股权数额
    private String equityAmount;
    //登记编号
    private String regNumber;
    //质权人
    private String pledgee;
    //股权出质设立发布日期
    private Long putDate;
    private String putDateString;
    private List<EquityCompanyList> companyList;
    //跳转到天眼查链接
    private String pledgorStr;
    private List<EquityLedgeeList> pledgeeList;
    //股权出质设立登记日期
    private Long regDate;
    private String regDateString;
    private List<EquityPledgorList> pledgorList;
    //状态
    private String state;
    //出质人
    private String pledgor;
    //质权人证照/证件号码
    private String certifNumberR;
    //证照/证件号码
    private String certifNumber;
    //跳转到天眼查链接
    private String pledgeeStr;
}
