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
public class PeopleInfo{
    //	证照/证件号码
    private String licenseNum;
    //抵押权人名称
    private String peopleName;
    //抵押权人证照/证件类型
    private String liceseType;
}
