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
public class EnterpriseProfileCopyrightOfWorksItem{
    //毫秒数	创建时间
    private Long createTime;
    //登记日期
    private String regtime;
    //著作权人姓名/名称
    private String authorNationality;
    //发布日期
    private String publishtime;
    //登记号
    private String regnum;
    //创作完成日期
    private String finishTime;
    //作品类别
    private String type;
    //作品名称
    private String fullname;
}
