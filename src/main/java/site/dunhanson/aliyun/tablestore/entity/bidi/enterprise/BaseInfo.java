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
@Getter
@Setter
@ToString
public class BaseInfo{
    //担保范围
    private String scope;
    //状态
    private String status;
    //备注
    private String remark;
    //登记日期
    private String regDate;
    //被担保债权种类
    private String type;
    //注销原因
    private String cancelReason;
    //表id
    private Long tycId;
    //被担保债权数额
    private String amount;
    //登记机关
    private String regDepartment;
    //登记编号
    private String regNum;
    //债务人履行债务的期限
    private String term;
    //省份
    private String base;
    //注销日期
    private Long cancelDate;
    //公示日期
    private Long publishDate;

}
