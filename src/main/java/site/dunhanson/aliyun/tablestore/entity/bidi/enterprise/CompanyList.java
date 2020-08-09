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
@Getter
@Setter
@ToString
public class CompanyList{
    //	1-公司 2-人
    private Integer type;
    //人或公司id
    private String  tycId;
    //人或公司名
    private String  name;
}
