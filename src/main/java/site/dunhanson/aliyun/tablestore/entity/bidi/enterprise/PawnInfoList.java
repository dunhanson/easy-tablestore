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
public class PawnInfoList{
    //数量、质量、状况、所在地等情况
    private String detail;
    //所有权归属
    private String ownership;
    //名称
    private String pawnName;
    //备注
    private String remark;
}
