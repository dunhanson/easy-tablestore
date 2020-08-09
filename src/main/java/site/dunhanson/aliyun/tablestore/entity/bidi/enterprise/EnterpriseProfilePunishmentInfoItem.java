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
@Setter
@Getter
@ToString
public class EnterpriseProfilePunishmentInfoItem {
    //行政处罚内容
    private String  content;
    //行政处罚决定书文号
    private String  punishNumber;
    //注册号
    private String  regNum;
    //描述
    private String  description;
    //公司名称
    private String  name;
    //省份简称
    private String  base;
    //作出行政处罚决定日期
    private String  decisionDate;
    //法定代表人（负责人）姓名
    private String  legalPersonName;
    //违法行为类型
    private String  type;
    //作出行政处罚决定机关名称
    private String  departmentName;
    //公示日期
    private String  publishDate;
}
