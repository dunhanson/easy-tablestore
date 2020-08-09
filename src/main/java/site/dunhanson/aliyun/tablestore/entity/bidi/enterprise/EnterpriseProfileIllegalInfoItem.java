package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/28 11:16
 * @description TODO
 */
@Setter
@Getter
@ToString
public  class EnterpriseProfileIllegalInfoItem {
    //移除原因
    private String removeReason;
    //决定移除部门
    private String removeDepartment;
    //列入日期
    private Long putDate;
    private String putDateString;
    //列入原因
    private String putReason;
    //决定列入部门(作出决定机关)
    private String putDepartment;
    //毫秒数	移除日期
    private Long removeDate;
    private String removeDateString;

}
