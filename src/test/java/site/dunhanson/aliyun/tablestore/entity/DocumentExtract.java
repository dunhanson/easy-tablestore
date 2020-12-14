package site.dunhanson.aliyun.tablestore.entity;

import lombok.Data;

@Data
public class DocumentExtract {

    private Long partitionkey;
    private Long docid;
    private Long status;
    private String pageTime;
    private String extractJson;
    private String industryJson;
    private String otherJson;
    private String dochtmlcon;
    private String save;
    private String dupDocid;

}
