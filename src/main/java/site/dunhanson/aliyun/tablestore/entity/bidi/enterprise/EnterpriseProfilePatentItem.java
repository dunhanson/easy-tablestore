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
@Setter
@Getter
@ToString
public class EnterpriseProfilePatentItem {
    //主分类号
    private String mainCatNum;
    //毫秒数	创建时间
    private String createTime;
    //申请公布号
    private String pubnumber;
    //申请号
    private String appnumber;
    //tyc对应表id
    private String tycId;
    //名称
    private String title;
    //专利名称
    private String patentName;
    //跳转到天眼查链接
    private List<String> connList;
    //申请日
    private String applicationTime;
    //申请人
    private String applicantname;
    //专利类型
    private String patentType;
    //公开公告日
    private String pubDate;
    //代理机构
    private String agency;
    //唯一标识符
    private String uni;
    //发明人
    private String inventor;
    //代理人
    private String agent;
    //申请公布日
    private String applicationPublishTime;
    //申请号/专利号
    private String patentNum;
    //发明人
    private String imgUrl;
    //全部分类号
    private String allCatNum;
    //摘要
    private String abstracts;
    //地址
    private String address;
    //uuid
    private String tycUuid;
    //申请人
    private String applicantName;
}
