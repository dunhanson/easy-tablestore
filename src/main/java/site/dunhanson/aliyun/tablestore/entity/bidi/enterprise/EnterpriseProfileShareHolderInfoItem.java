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

public class EnterpriseProfileShareHolderInfoItem{
    //tycId
    private Long tycId;
    //拥有公司个数
    private Long toco;
    //logo
    private String logo;
    //股东名
    private String name;
    //简称
    private String alias;
    //实缴
    private List<EnterpriseProfileShareHolderInfoCapitalActl> capitalActl;
    //	1-公司 2-人
    private Long type;
    //实缴
    private List<EnterpriseProfileShareHolderInfoCapital> capital;
}
