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
@ToString
@Getter
@Setter
public class EnterpriseProfileChangeInfoItem {
    //变更事项
    private String changeItem;
    //创建时间
    private String createTime;
    //mediumtext	变更前
    private String contentBefore;
    //mediumtext	变更后
    private String contentAfter;
    //变更时间
    private String changeTime;
}
