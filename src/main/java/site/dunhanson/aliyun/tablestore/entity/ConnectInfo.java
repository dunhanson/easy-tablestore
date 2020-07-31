package site.dunhanson.aliyun.tablestore.entity;

import lombok.Data;

/**
 * tablestore的连接信息
 */
@Data
public class ConnectInfo {

    /**激活的环境**/
    private String active;
    /**endPoint**/
    private String endPoint;
    /**accessKeyId**/
    private String accessKeyId;
    /**accessKeySecret**/
    private String accessKeySecret;
    /**instanceName**/
    private String instanceName;

}
