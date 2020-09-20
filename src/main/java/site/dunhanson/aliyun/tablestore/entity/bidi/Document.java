package site.dunhanson.aliyun.tablestore.entity.bidi;

import lombok.Data;

import java.util.List;

/**
 * document 表的实体
 */
@Data
public class Document {
//    注意：前面3+32=35个字段不能变顺序，如需加字段加在后面
//    partitionkey,docid,status,page_time,docchannel,doctitle,dochtmlcon,area,province,city,district,
//    docstatus,doccontent,dockeywords,cruser,crtime,hitscount,opertime,operuser,publishtime,downscount,auditstatus,
//    auditdesc,audittime,uuid,web_source_no,web_source_name,info_source,info_type,industry,page_attachments,qstatus,qcodes,qtype,qrange
    /**
     * 分区键[1,500]只用于分区
     */
    private Long partitionkey;

    /**
     * docid
     */
    private Long docid;

    /**
     * 数据状态 1代表待要素提取 2代表已在maxCompute做完要素提取 3代表成品数据
     */
    private Long status = 1L;

    /**
     * 页面时间
     */
    private String pageTime;

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
     * 数据来源
     */
    private String infoSource;

    /**
     * 行业分类（中类）
     */
    private String infoType;

    /**
     * 行业分类（小类）
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




    // 提取出的 11 + 1
    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目编码
     */
    private String projectCode;

    /**
     * 项目地址
     */
    private String projectAddr;

    /**
     * 招标人
     */
    private String tenderee;

    /**
     * 招标人地址
     */
    private String tendereeAddr;

    /**
     * 招标人电话
     */
    private String tendereePhone;

    /**
     * 招标人联系人
     */
    private String tendereeContact;

    /**
     * 代理人
     */
    private String agency;

    /**
     * 代理人电话
     */
    private String agencyPhone;

    /**
     * 代理人联系人
     */
    private String agencyContact;

    /**
     * 是否废标（为中标公告此属性才有意义 为 "true" 说明是废标）
     */
    private String abandonedTender;

    /**
     * 子标段集合
     */
    private List<SubDocument> subDocsJson;

}
