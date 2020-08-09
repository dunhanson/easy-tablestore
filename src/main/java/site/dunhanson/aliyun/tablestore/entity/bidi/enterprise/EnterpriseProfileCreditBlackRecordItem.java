package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/6/5 9:38
 * @description 黑名单记录信息
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileCreditBlackRecordItem {
    //黑名单记录编号
    private String creditBlackRecordCode;
    //黑名单记录主体
    private String actor;
    //移除黑名单日期
    private String removeDate;
    //列入决定机关名称
    private String judgingInstitution;
    //列入经营异常名录原因类型名称
    private String recordingCause;
    //设立日期
    private String sentenceDate;

}
