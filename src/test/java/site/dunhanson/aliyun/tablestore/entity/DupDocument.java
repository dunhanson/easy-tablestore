package site.dunhanson.aliyun.tablestore.entity;
import lombok.Data;

/**
 * document_extract 表的实体
 */
@Data
public class DupDocument {

    /**
     * uuid主键
     */
    private String uuid;

    /**
     * docid
     */
    private Long docid;

    /**
     * 与此公告形成重复的公告集合，格式 1,2,3
     */
    private String dupDocid;

}
