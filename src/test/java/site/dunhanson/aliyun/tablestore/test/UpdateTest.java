package site.dunhanson.aliyun.tablestore.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import site.dunhanson.aliyun.tablestore.entity.bidi.DocumentExtract;
import site.dunhanson.aliyun.tablestore.entity.bidi.enterprise.*;
import site.dunhanson.aliyun.tablestore.utils.TableStoreUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class UpdateTest {


    /**
     * 测试获取一行记录
     */
    @Test
    public void testGet() {
        Enterprise enterprise = new Enterprise();
        enterprise.setName("testEnterprise");
        enterprise.setBidiId(1314520L);
        enterprise.setArea("华南");
        enterprise.setBidNumber(10);

        // 1、主要人员（里面的 List<String>）
        List<EnterpriseProfilePrimaryStaffItem> staffs = new ArrayList<>();
        EnterpriseProfilePrimaryStaffItem enterpriseProfilePrimaryStaffItem1 = new EnterpriseProfilePrimaryStaffItem();
        enterpriseProfilePrimaryStaffItem1.setName("staff1");
        enterpriseProfilePrimaryStaffItem1.setTycPersonId(1L);
        enterpriseProfilePrimaryStaffItem1.setTypeJoin(Arrays.asList("a","b"));
        staffs.add(enterpriseProfilePrimaryStaffItem1);
        EnterpriseProfilePrimaryStaffItem enterpriseProfilePrimaryStaffItem2 = new EnterpriseProfilePrimaryStaffItem();
        enterpriseProfilePrimaryStaffItem2.setName("staff2");
        enterpriseProfilePrimaryStaffItem2.setTycPersonId(2L);
        enterpriseProfilePrimaryStaffItem2.setTypeJoin(Arrays.asList("c","d"));
        staffs.add(enterpriseProfilePrimaryStaffItem2);
        enterprise.setStaffs(staffs);

        // 2、法院公告（里面的announce_id List<CompanyList>）
        List<EnterpriseProfileCourtAnnouncementItem> courtAnnouncement = new ArrayList<>();
        EnterpriseProfileCourtAnnouncementItem enterpriseProfileCourtAnnouncementItem1 = new EnterpriseProfileCourtAnnouncementItem();
        enterpriseProfileCourtAnnouncementItem1.setBltnno("bltnno1");
        enterpriseProfileCourtAnnouncementItem1.setAnnounce_id(19L);

        List<CompanyList> companyList = new ArrayList<>();
        CompanyList companyList1 = new CompanyList();
        companyList1.setName("name1");
        companyList.add(companyList1);
        CompanyList companyList2 = new CompanyList();
        companyList2.setName("name2");
        companyList.add(companyList2);
        enterpriseProfileCourtAnnouncementItem1.setCompanyList(companyList);
        courtAnnouncement.add(enterpriseProfileCourtAnnouncementItem1);
        enterprise.setCourtAnnouncement(courtAnnouncement);

        // 3、动产抵押（里面的 BaseInfo）
        List<EnterpriseProfileMortgageInfoItem> mortgageInfo = new ArrayList<>();
        EnterpriseProfileMortgageInfoItem enterpriseProfileMortgageInfoItem = new EnterpriseProfileMortgageInfoItem();
        BaseInfo baseInfo = new BaseInfo();
        baseInfo.setBase("base");
        baseInfo.setCancelDate(12L);
        baseInfo.setRegDepartment("regDepartment");
        enterpriseProfileMortgageInfoItem.setBaseInfo(baseInfo);
        mortgageInfo.add(enterpriseProfileMortgageInfoItem);
        enterprise.setMortgageInfo(mortgageInfo);

        TableStoreUtils.insert(enterprise);

        List<String> columnsToGet = new ArrayList<>();

        columnsToGet.add("area");
        columnsToGet.add("court_announcement");


        Enterprise enterprise1 = TableStoreUtils.get(enterprise, Enterprise.class, columnsToGet);
        System.out.println(enterprise1);
    }

    /**
     * 根据二级索引查找
     */
    @Test
    public void testSearchBysecondaryIndexForGetRange() {
        DocumentExtract extract = new DocumentExtract();
        extract.setStatus(1L);
        log.warn("开始查询，status={}", extract.getStatus());
        List<DocumentExtract> list = TableStoreUtils.searchBysecondaryIndex(extract, DocumentExtract.class, null, 20);
        log.warn("查询结果={}", list.size());
    }


    /**
     * 测试新增
     */
    @Test
    public void testInsert() {
        Enterprise enterprise = new Enterprise();
        enterprise.setBidiId(1314L);
        enterprise.setProvince("广东d2");


        // 更新招投标数量
        enterprise.setBidNumber(40);
        enterprise.setZhaoBiaoNumber(10);
        enterprise.setDaiLiNumber(10);
        enterprise.setZhongBiaoNumber(10);
        enterprise.setTouBiaoNumber(10);

        int num = TableStoreUtils.insert(enterprise);
        log.debug("影响行数={}", num);
    }

}
