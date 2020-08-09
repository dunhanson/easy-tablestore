package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/28 11:15
 * @description TODO
 */
@Setter
@Getter
@ToString
public class EquityCompanyList{
    //公司id
    private String tycId;
    //公司名称
    private String name;
}
