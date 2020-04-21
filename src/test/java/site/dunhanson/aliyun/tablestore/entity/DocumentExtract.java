package site.dunhanson.aliyun.tablestore.entity;

import lombok.Data;

/**
 * document_extract 表的实体
 */
@Data
public class DocumentExtract {

    // 1+2+1+33

    /**
     * 状态
     */
    private Long status;

    /**
     * 要素提取返回的json字符串
     */
    private String extractJson;

    /**
     * 行业分类返回的json字符串
     */
    private String industryJson;

    /**
     * 其他要素提取返回的json字符串（公告内容样式清理、关键字、资质、地区信息）
     */
    private String otherExtractJson;


    /**
     * docid
     */
    private Long docid;

    /**
     * 公告类型
     */
    private Long docchannel;

    /**
     * 标题
     */
    private String doctitle;

    /**
     * 文档状态(1：草稿;20：已发布；10：带审核；21：未通过)
     */
    private Long docstatus;

    /**
     * 文档内容（200字的摘要）
     */
    private String doccontent;

    /**
     * 文档html内容
     */
    private String dochtmlcon;

    /**
     * 关键词
     */
    private String dockeywords;

    /**
     * 创建者
     */
    private String cruser;

    /**
     * 创建时间（Date -> String）
     */
    private String crtime;

    /**
     * 文档点击数
     */
    private Long hitscount;

    /**
     * 操作时间（Date -> String）
     */
    private String opertime;

    /**
     * 操作者
     */
    private String operuser;

    /**
     * 发布时间（Date -> String）
     */
    private String publishtime;

    /**
     * 下载数
     */
    private Long downscount;

    /**
     * 审核状态
     */
    private Integer auditstatus;

    /**
     * 审核描述
     */
    private String auditdesc;

    /**
     * 审核时间（Date -> String）
     */
    private String audittime;

    /**
     * 同步数据id（uuid）
     */
    private String uuid;

    /**
     * 同步数据-网站编码
     */
    private String webSourceNo;

    /**
     * 地区
     */
    private String area;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 网站名称
     */
    private String webSourceName;

    /**
     * 数据来源
     */
    private String infoSource;

    /**
     * 数据类型
     */
    private String infoType;

    /**
     * 行业标签
     */
    private String industry;

    /**
     * 页面时间
     */
    private String pageTime;

    /**
     * 页面链接
     */
    private String pageAttachments;

    /**
     * 资质解析状态（01:未解析;10:已匹配;20:未匹配
     */
    private String qstatus;

    /**
     * 资质编号
     */
    private String qcodes;

    /**
     * 资质分类
     */
    private String qtype;

    /**
     * 资质范围
     */
    private String qrange;













}
