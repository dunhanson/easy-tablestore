package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/28 11:17
 * @description TODO
 */
@Setter
@Getter
@ToString
public class EnterpriseProfilePrimaryStaffItem{
    //拥有公司个数
    private Long toco;
    //人或公司id
    private Long tycPersonId;
    //职位
    private List<String> typeJoin;
    private String logo;
    //人或公司id
    private Long type;
    //主要人员名
    private String name;

}
