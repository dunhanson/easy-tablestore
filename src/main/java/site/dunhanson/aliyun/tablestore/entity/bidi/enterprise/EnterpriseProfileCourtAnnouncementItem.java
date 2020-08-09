package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/28 11:14
 * @description TODO
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileCourtAnnouncementItem {
    //id
    private Long tycId;
    //公告id
    private Long announce_id;
    //公告号
    private String  bltnno;
    //公告状态号
    private String  bltnstate;
    //公告类型
    private String   bltntype;
    //公告类型名称
    private String   bltntypename;
    //案件号
    private String  caseno;
    //案件内容
    private String   content;
    //法院名
    private String   courtcode;
    //处理等级
    private String   dealgrade;
    //处理等级名称
    private String   dealgradename;
    //法官
    private String   judge;
    //法官电话
    private String   judgephone;
    //手机号
    private String  mobilephone;
    //原告
    private String  party1;
    //当事人
    private String  party2;
    private List<CompanyList> companyList;
    //跳转到天眼查链接
    private String   party1Str;
    //跳转到天眼查链接
    private String   party2Str;
    //省份
    private String   province;
    //刊登日期
    private String   publishdate;
    //刊登版面
    private String  publishpage;
    //原因
    private String  reason;
}
