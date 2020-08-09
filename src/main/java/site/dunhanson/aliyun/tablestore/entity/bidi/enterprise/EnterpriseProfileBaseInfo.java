package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/24 11:43
 * @description 企业基本信息（含联系方式）
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileBaseInfo {

    //天眼查数据库更新时间(STRING|yyyy-mm-dd)
    private Long tycUpdateTime;
    //人员规模
    private String staffNumRange;
    //经营开始时间
    private Long fromTime;
    //万分制	行业分数
    private Integer categoryScore;
    //股票名
    private String bondName;
    //是否是小微企业 0不是 1是
    private Integer isMicroEnt;
    //股票曾用名
    private String usedBondName;
    //注册号
    private Long regNumber;
    //登记机关
    private String regInstitute;
    //注册地址
    private String regLocation;
    //行业
    private String industry;
    //核准时间
    private Long approvedTime;
    //参保人数
    private Long socialStaffNum;
    //企业标签
    private String tags;
    //logo（不建议使用）
    private String logo;
    //纳税人识别号
    private String taxNumber;
    //经营范围
    private String businessScope;
    //英文名
    private String property3;
    //简称
    private String alias;
    //组织机构代码
    private String orgNumber;
    //企业状态
    private String regStatus;
    //股票类型
    private String bondType;
    //法人
    private String legalPersonName;
    //经营结束时间
    private Long toTime;
    //法人id
    private Long legalPersonId;
    //数据来源标志
    private String sourceFlag;
    //实收注册资金
    private String actualCapital;
    //新公司名id
    private String tycCorrectCompanyId;
    //企业类型
    private String companyOrgType;
    //抓取数据的时间
    private Long tycUpdateTimes;
    //统一社会信用代码
    private String creditCode;
    //companyId
    private Long tycCompanyId;
    //曾用名
    private String historyNames;
    //股票号
    private String bondNum;
    //注册资本币种 人民币 美元 欧元 等
    private String regCapitalCurrency;
    //实收注册资本币种 人民币 美元 欧元 等
    private String actualCapitalCurrency;
    //邮箱
    private String email;
    //网址
    private String websiteList;
    //联系方式
    private String phoneNumber;
    //吊销日期
    private Long revokeDate;
    //吊销原因
    private String revokeReason;
    //注销日期
    private Long cancelDate;
    //注销原因
    private String cancelReason;
}
