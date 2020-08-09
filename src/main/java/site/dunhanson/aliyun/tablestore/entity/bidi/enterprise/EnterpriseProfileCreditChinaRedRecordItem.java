package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/24 10:53
 * @description 守信红名单--信息中国信息（信用中国）
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileCreditChinaRedRecordItem {
    //纳税人信用等级（共有A、B、M、C、D五级）
    private String taxpayerCreditLevel;
    //数据来源
    private String infoSource;
    //序号
    private String recordId;
    //纳税人名称
    private String taxpayerName;
    //评价年度
    private String evaluationYear;
    //文件名
    private String docName;
    //数据更新日期(STRING|yyyy-mm-dd)
    private String updateDate;
}

