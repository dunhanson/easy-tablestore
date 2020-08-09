package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/6/5 9:41
 * @description 变更记录
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileQuaChangeLogItem {
    //变更日期
    private String  logDate;
    //变更内容
    private String  logContent;
}
