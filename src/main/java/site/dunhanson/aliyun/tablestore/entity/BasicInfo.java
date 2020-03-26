package site.dunhanson.aliyun.tablestore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * @author dunhanson
 * @date 2020.03.20
 * @description TableStore基础信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicInfo {
    /**endPoint**/
    private String endPoint;
    /**accessKeyId**/
    private String accessKeyId;
    /**accessKeySecret**/
    private String accessKeySecret;
    /**instanceName**/
    private String instanceName;
    /**表名**/
    private String tableName;
    /**索引名List集合**/
    private List<String> indexName;
    /**忽略字段List集合**/
    private List<String> ignoreColumn;
    /**分页限制数**/
    private Integer limit;
}
