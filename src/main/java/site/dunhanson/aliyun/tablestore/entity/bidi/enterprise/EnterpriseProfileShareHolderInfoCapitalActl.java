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
public class EnterpriseProfileShareHolderInfoCapitalActl{
    //出资金额
    private String amomon;
    //出资时间
    private String time;
    //占比
    private String percent;
    //实缴方式
    private String paymet;
}
