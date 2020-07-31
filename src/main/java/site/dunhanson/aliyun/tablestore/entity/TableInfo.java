package site.dunhanson.aliyun.tablestore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * @author dunhanson
 * @date 2020.03.20
 * @description TableStore表的基础信息
 */
@Data
public class TableInfo {
    /**表名**/
    private String tableName;
    /**主键List集合**/
    private List<String> primaryKey;
    /**二级索引List集合**/
    private List<String> secondaryIndex;
    /**索引名List集合**/
    private List<String> indexName;
    /**忽略字段List集合**/
    private List<String> ignoreColumn;
    /**默认分页大小**/
    private Integer limit;
}
