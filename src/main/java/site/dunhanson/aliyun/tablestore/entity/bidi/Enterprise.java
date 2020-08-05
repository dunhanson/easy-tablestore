package site.dunhanson.aliyun.tablestore.entity.bidi;

import lombok.Data;

/**
 * ots.Enterprise 实体
 */
@Data
public class Enterprise {

    /**
     * 比地id(即：企业在我们这边的一个唯一标识)（也是表的主键）
     */
    private Long bidiId;

    /**
     * 名称
     */
    private String name;

    /**
     * 昵称
     */
    private String nicknames;

    /**
     * 区域
     */
    private String area;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 总的招投标数量 = 招标数量 + 代理数量 + 中标数量 + 投标数量
     */
    private Integer bidNumber;

    /**
     * 招标数量
     */
    private Integer zhaoBiaoNumber;

    /**
     * 代理数量
     */
    private Integer daiLiNumber;

    /**
     * 中标数量
     */
    private Integer zhongBiaoNumber;

    /**
     * 投标数量
     */
    private Integer touBiaoNumber;

    /**
     * 注册资金
     */
    private String regCapital;

    /**
     * 注册时间
     */
    private String estiblishTime;

    // -------------
    /**
     * 天眼查id
     */
    private Long tycId;

    /**
     * 企业地址
     */
    private String address;

    /**
     * 法人
     */
    private String legalPerson;

    /**
     * 资质数量
     */
    private Integer qualificationsNumber;

    /**
     * 行政许可数量
     */
    private Integer adminLicensesNumber;

}
