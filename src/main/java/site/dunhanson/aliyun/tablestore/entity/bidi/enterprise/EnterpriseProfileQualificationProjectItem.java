package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/24 10:53
 * @description 工程项目信息
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileQualificationProjectItem {
    //项目编码
    private String projectCode;
    //项目名称
    private String projectName;
    //项目属地
    private String projectArea;
    //项目类别
    private String projectType;
    //建设单位
    private String constructionCompany;
}

