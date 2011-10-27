package com.cloudspokes.process;

import com.cloudspokes.process.DataLoaderProcess;

import com.sforce.async.OperationEnum;

public class MemberProcess implements DataLoaderProcess {
  
  private String sobject;
  private String sql;
  private OperationEnum operation;
  private String externalIdFieldName;
  private String csvDirectory;
  
  public MemberProcess(String csvDirectory) {
    this.csvDirectory = csvDirectory;
    sobject = "Member__c";
    sql = "select u.username as Name, u.username as Username__c, p.first_name as First_Name__c, " +
    		"p.last_name as Last_Name__c, m.Email as Email__c, " +
    		"p.company as Company__c, p.school as School__c, p.short_bio as Summary_Bio__c, p.website as Website__c, " +
    		"LEFT(p.quote,255) as Quote__c, p.Social_Network_2_id as Facebook__c, p.social_network_3_id as LinkedIn__c, p.twitter_handle as Twitter__c, " +
    		"p.gender as Gender__c, p.Years_Of_Experience as Years_of_Experience__c, p.shirt_size as Shirt_Size__c, p.age_range as Age_Range__c, " +
    		"p.address_line_1 as Address_Line1__c, p.address_line_2 as Address_Line2__c, p.city as City__c, p.state as State__c, p.zip as Zip__c, " +
    		"p.home_phone as Phone_Home__c, p.mobile_phone as Phone_Mobile__c, p.Work_Phone as Phone_Work__c, c.country_desc as Country__c, " + 
    		"t.time_zone_desc + ' -- ' + t.gmt_desc as Time_Zone__c, ws.work_status_desc as Work_Status__c  " + 
    		"from aspnet_users u inner join profile p on p.userid = u.userid " +
    		"inner join aspnet_membership m on p.userid = m.userid " +
    		"left join lk_country c on c.country_id = p.country_id " +
    		"left join lk_time_zone t on t.time_zone_id = p.time_zone_id " +
    		"left join lk_work_status ws on ws.work_status_id = p.work_status_id";
    operation = OperationEnum.upsert;
    externalIdFieldName = "Username__c";
  }
  
  public String getSobject() {
    return sobject;
  }
  public String getSql() {
    return sql;
  }
  public String getCsvFile() {
    return csvDirectory + sobject + ".csv";
  }

  public OperationEnum getOperation() {
    return operation;
  }
  public String getExternalIdFieldName() {
    return externalIdFieldName;
  }
  
}
