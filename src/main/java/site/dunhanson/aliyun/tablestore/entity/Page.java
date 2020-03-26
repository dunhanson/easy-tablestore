package site.dunhanson.aliyun.tablestore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

/**
 * @author dunhanson
 * @date 2020.03.26
 * @description TableStore分页对象
 */
@Data
@AllArgsConstructor
public class Page<T> {
    /**偏移数**/
    private Integer limit;
    /**分页数**/
    private Integer offset;
    /**总页数**/
    private Long totalPage;
    /**总记录**/
    private Long totalCount;
    /**分页记录**/
    private List<T> list;

    public Page(int limit, long totalCount, List<T> list) {
        this.limit = limit;
        this.totalCount = totalCount;
        this.list = list;
        //计算总页数
        totalPage = totalCount / limit;
        totalPage = totalCount % limit == 0 ? totalPage : totalPage + 1;
    }
}
