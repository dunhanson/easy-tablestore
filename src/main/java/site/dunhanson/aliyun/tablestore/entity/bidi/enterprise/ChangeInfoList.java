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
public class ChangeInfoList{
    //毫秒数	变更日期
    private Long changeDate;
    //变更内容
    private String changeContent;
}
