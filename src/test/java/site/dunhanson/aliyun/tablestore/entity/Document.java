package site.dunhanson.aliyun.tablestore.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Document {
    private Long docid;
    private String pageTime;
    private String docchannel;
    private String doctitle;
    private String dochtmlcon;
    private String area;
    private String province;
    private String city;
    private String publishtime;
}
