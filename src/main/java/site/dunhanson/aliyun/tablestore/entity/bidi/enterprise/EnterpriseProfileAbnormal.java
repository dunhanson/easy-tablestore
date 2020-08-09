package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/24 9:50
 * @description 经营异常
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileAbnormal {
    private Integer abnormalNumber;
    private List<EnterpriseProfileAbnormalItem> abnormals;
}

