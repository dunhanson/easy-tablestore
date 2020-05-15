package site.dunhanson.aliyun.tablestore.entity;

import lombok.Data;

/**
 * document_temp 表的实体
 */
@Data
public class DocumentTemp {

    // 33
    /**
     * 页面时间
     */
    private String pageTime;

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
     * 文档html内容
     */
    private String dochtmlcon;

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
     * 文档状态(1：草稿;20：已发布；10：带审核；21：未通过)
     */
    private Long docstatus;

    /**
     * 文档内容（200字的摘要）
     */
    private String doccontent;


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
     * 网站名称
     */
    private String webSourceName;

    /**
     * 行业分类（中类）
     */
    private String infoSource;

    /**
     * 行业分类（小类）
     */
    private String infoType;

    /**
     * 行业标签
     */
    private String industry;

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
