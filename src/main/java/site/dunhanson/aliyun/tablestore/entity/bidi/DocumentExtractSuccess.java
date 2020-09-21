package site.dunhanson.aliyun.tablestore.entity.bidi;
import lombok.Data;

/**
 * document_extract_success 表的实体
 */
@Data
public class DocumentExtractSuccess {

    /**
     * 分区键[1,500]只用于分区
     */
    private Long partitionkey;

    /**
     * docid
     */
    private Long docid;

}
