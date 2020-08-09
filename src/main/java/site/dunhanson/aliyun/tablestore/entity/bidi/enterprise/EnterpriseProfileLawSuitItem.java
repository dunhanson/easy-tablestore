package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/28 11:16
 * @description TODO
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileLawSuitItem {
    //相关公司id
    private String SplitGids;
    //原告
    private String plaintiffs;
    //原告id
    private String plaintiffId;
    //法院
    private String court;
    //案由
    private String casereason;
    //原文链接地址
    private String url;
    //案号
    private String caseno;
    //tycId
    private String tycId;
    //标题
    private String title;
    //摘要
    private String abstracts;
    //公司列表
    private List<String> connList;
    //发布时间
    private String submittime;
    //被告id
    private String defendantId;
    //天眼查显示url
    private String lawsuitUrl;
    //案件类型
    private String casetype;
    //uuid
    private String uuid;
    //文书类型
    private String doctype;
    //代理人
    private String agent;
    //第三人
    private String thirdParties;
    //被告
    private String defendants;
}
