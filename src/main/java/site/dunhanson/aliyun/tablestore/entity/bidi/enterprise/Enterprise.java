package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;
/**
 * @author jiangzf
 * @date 2020/08/09
 * @description 企业相关信息（ots.enterprise 表的实体）
 */
@Getter
@Setter
@ToString
public class Enterprise {
    /*企业名称*/
    private String name;
    /*企业别名的集合使用英文的逗号分隔*/
    private String nicknames;
    /*比地ID（各个数据库的关联）*/
    private Long bidiId;
    /*区域*/
    private String area;
    /*省份*/
    private String province;
    /*城市*/
    private String city;
    /*区县*/
    private String district;
    /*总的招投标数量 = 招标数量 + 代理数量 + 中标数量 + 投标数量*/
    private Integer bidNumber;
    /*招标数量*/
    private Integer zhaoBiaoNumber;
    /*代理数量*/
    private Integer daiLiNumber;
    /*中标数量*/
    private Integer zhongBiaoNumber;
    /*投标数量*/
    private Integer touBiaoNumber;
    /*这个属性用于旧数据的升级(默认值为 1 )*/
    private Long upgradeStatus = 1L;
    /*天眼查ID*/
    private Long tycId;
    /*机构信用代码*/
    private String creditCode;
    /*法人*/
    private String legalPerson;
    /*企业地址*/
    private String address;
    /*电话*/
    private String phone;
    /*邮箱*/
    private String email;
    /*注册日期*/
    private String foundDate;
    /*注册资金*/
    private String regCapital;
    /*公司类型 1-公司，2-香港公司，3-社会组织，4-律所，5-事业单位，6-基金会(NUMBER),*/
    private Integer companyType;
    /*开业时间*/
    private String estiblishTime;
    /*公司简介*/
    private String description;
    /*天眼查数据库更新时间(STRING|yyyy-mm-dd)*/
    private String tycUpdateTime;
    /*人员规模*/
    private String staffNumRange;
    /*经营开始时间*/
    private Long fromTime;
    /*万分制	行业分数*/
    private Integer categoryScore;
    /*股票名*/
    private String bondName;
    /*是否是小微企业 0不是 1是*/
    private Integer isMicroEnt;
    /*股票曾用名*/
    private String usedBondName;
    /*注册号*/
    private String regNumber;
    /*企业评分*/
    private Long percentileScore;
    /*登记机关*/
    private String regInstitute;
    /*注册地址*/
    private String regLocation;
    /*行业*/
    private String industry;
    /*核准时间*/
    private Long approvedTime;
    /*参保人数*/
    private Long socialStaffNum;
    /*企业标签*/
    private String tags;
    /*logo（不建议使用）*/
    private String logo;
    /*纳税人识别号*/
    private String taxNumber;
    /*经营范围*/
    private String businessScope;
    /*英文名*/
    private String property3;
    /*简称*/
    private String alias;
    /*组织机构代码*/
    private String orgNumber;
    /*企业状态*/
    private String regStatus;
    /*股票类型*/
    private String bondType;
    /*法人（法人优先使用此字段）*/
    private String legalPersonName;
    /*经营结束时间*/
    private Long toTime;
    /*法人id*/
    private Long legalPersonId;
    /*数据来源标志*/
    private String sourceFlag;
    /*实收注册资金*/
    private String actualCapital;
    /*新公司名id*/
    private String tycCorrectCompanyId;
    /*企业类型*/
    private String companyOrgType;
    /*抓取数据的时间*/
    private Long tycUpdateTimes;
    /*companyId*/
    private Long tycCompanyId;
    /*曾用名*/
    private String historyNames;
    /*股票号*/
    private String bondNum;
    /*注册资本币种 人民币 美元 欧元 等*/
    private String regCapitalCurrency;
    /*实收注册资本币种 人民币 美元 欧元 等*/
    private String actualCapitalCurrency;
    /*网址*/
    private String websiteList;
    /*联系方式*/
    private String phoneNumber;
    /*吊销日期*/
    private Long revokeDate;
    /*吊销原因*/
    private String revokeReason;
    /*注销日期*/
    private Long cancelDate;
    /*注销原因*/
    private String cancelReason;
    /*主要人员总数*/
    private Long mainStaffsNumber;


    /*主要人员*/
    private List<EnterpriseProfilePrimaryStaffItem> staffs;
    /*股东总数*/
    private Long mainHoldersNumber;
    /*主要股东*/
    private List<EnterpriseProfileShareHolderInfoItem> holders;
    /*变更总数*/
    private Long changeInfoNumber;
    /*变更记录*/
    private List<EnterpriseProfileChangeInfoItem> changeInfo;
    /*分支机构总数*/
    private Integer branchNumber;
    /*分支机构*/
    private List<EnterpriseProfileBranchItem> branches;
    /*专利数量*/
    private Integer patentsNumber;
    /*专利*/
    private List<EnterpriseProfilePatentItem> patents;
    //著作权数量
    private Integer copyRegWorksNumber;
    /*著作权*/
    private List<EnterpriseProfileCopyrightOfWorksItem> copyRegWorks;
    /*经营异常数量*/
    private Integer abnormalNumber;
    /*经营异常*/
    private List<EnterpriseProfileAbnormalItem> abnormals;
    /*行政处罚数量*/
    private Integer punishmentInfoNumber;
    /*行政处罚*/
    private List<EnterpriseProfilePunishmentInfoItem> punishmentInfo;
    /*行政处罚数量--信用中国(新版)*/
    private Integer creditChinaV2Number;
    /*行政处罚--信用中国(新版)*/
    private List<EnterpriseProfileCreditChinaItem> creditChinaV2;
    /*严重违法数量*/
    private Integer illegalinfoNumber;
    /*严重违法*/
    private List<EnterpriseProfileIllegalInfoItem> illegalinfo;
    /*股权出质数量*/
    private Integer equityInfoNumber;
    /*股权出质*/
    private List<EnterpriseProfileEquityInfoItem> equityInfo;
    /*动产抵押数量*/
    private Integer mortgageInfoNumber;
    /*动产抵押*/
    private List<EnterpriseProfileMortgageInfoItem> mortgageInfo;
    /*欠税公告数量*/
    private Integer ownTaxNumber;
    /*欠税公告*/
    private List<EnterpriseProfileOwnTaxItem> ownTax;
    /*法律诉讼数量*/
    private Integer lawSuitNumber;
    /*法律诉讼*/
    private List<EnterpriseProfileLawSuitItem> lawSuit;
    /*法院公告数量*/
    private Integer courtAnnouncementNumber;
    /*法院公告*/
    private List<EnterpriseProfileCourtAnnouncementItem> courtAnnouncement;
    /*失信人数量*/
    private Integer dishonestNumber;
    /*失信人*/
    private List<EnterpriseProfileDishonestItem> dishonest;
    /*被执行人数量*/
    private Integer zhixinginfoNumber;
    /*被执行人*/
    private List<EnterpriseProfileZhixingInfoItem> zhixinginfo;
    /*资质数量*/
    private Integer qualificationsNumber;
    /*资质更新时间(STRING|yyyy-mm-dd)*/
    private String qualificationsUpdateTime;
    /*资质列表*/
    private List<EnterpriseProfileQualificationItem> qualifications;
    /*工程人员数量*/
    private Integer registeredStaffsNumber;
    /*工程人员*/
    private List<EnterpriseProfileRegisteredStaffItem> registeredStaffs;
    /*工程项目数量*/
    private Integer quaProjectsNumber;
    /*工程项目*/
    private List<EnterpriseProfileQualificationProjectItem> quaProjects;
    /*不良行为数量*/
    private Integer badCreditBehaviorsNumber;
    /*不良行为*/
    private List<EnterpriseProfileBadCreditBehaviorItem> badCreditBehaviors;
    /*良好行为数量*/
    private Integer goodCreditBehaviorsNumber;
    /*良好行为*/
    private List<EnterpriseProfileGoodCreditBehaviorItem> goodCreditBehaviors;
    /*黑名单记录数量*/
    private Integer creditBlackRecordsNumber;
    /*黑名单记录*/
    private List<EnterpriseProfileCreditBlackRecordItem> creditBlackRecords;
    /*变更记录数量*/
    private Integer quaChangeLogsNumber;
    /*变更记录*/
    private List<EnterpriseProfileQuaChangeLogItem> quaChangeLogs;
    /*信用中国数据更新时间(STRING|yyyy-mm-dd),*/
    private String creditInfoUpdateTime;
    /*行政许可数量*/
    private Integer adminLicensesNumber;
    /*行政许可*/
    private List<EnterpriseProfileCreditChinaAdminLicenseItem> adminLicenses;
    /*行政处罚数量*/
    private Integer adminPenaltiesNumber;
    /*行政处罚*/
    private List<EnterpriseProfileCreditChinaAdminPenaltieItem> adminPenalties;
    /*守信红名单数量*/
    private Integer creditRedRecordNumber;
    /*守信红名单*/
    private List<EnterpriseProfileCreditChinaRedRecordItem> creditRedRecord;
    /*招标代理id，百度那些已经收录的使用 agent_detail_info.id 新增的请使用 bidiId*/
    private Long zbdlId;
    /*旧的以 agent_contact_person 为准， 新增以 agent_legal_person 为准*/
    private String contactPerson;
    /*合作案例*/
    private List<AgentEventInfo> agentEventInfos;
}
