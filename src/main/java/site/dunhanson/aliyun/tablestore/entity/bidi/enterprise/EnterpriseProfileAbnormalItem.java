package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/28 11:13
 * @description TODO
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileAbnormalItem{
    // 创建时间
    private String createTime;
    // 列入日期
    private String putDate;
    // 移出日期
    private String removeDate;
    // 移出部门
    private String removeDepartment;
    // 移除异常名录原因
    private String removeReason;
    // 列入异常名录原因
    private String putReason;
    // 决定列入异常名录部门(作出决定机关)
    private String putDepartment;

}
