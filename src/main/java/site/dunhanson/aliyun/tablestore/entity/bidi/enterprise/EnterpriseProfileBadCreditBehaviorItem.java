package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/24 10:53
 * @description 不良信息信息
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileBadCreditBehaviorItem {
     //诚信记录编号
     private String creditRecordCode;
    //诚信记录主体
     private String actor;
    //决定内容
     private String recordingCause;
    //实施部门
     private String executiveDept;
    //发布有效期
     private String expiryDate;
}

