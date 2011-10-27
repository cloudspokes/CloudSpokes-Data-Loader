package com.cloudspokes.process;

import com.cloudspokes.process.DataLoaderProcess;

import com.sforce.async.OperationEnum;

public class ChallengeProcess implements DataLoaderProcess {
  
  private String sobject;
  private String sql;
  private OperationEnum operation;
  private String externalIdFieldName;
  private String csvDirectory;
  
  public ChallengeProcess(String csvDirectory) {
    this.csvDirectory = csvDirectory;
    sobject = "Challenge__c";
    sql = "select contest_id as SQL_Id__c, contest_title as Name, " +
    		"CONVERT(VARCHAR(20), contest_startdate, 126) as Start_Date__c, CONVERT(VARCHAR(20), contest_enddate, 126) as End_Date__c, " +
    		"CONVERT(VARCHAR(20), winner_announced_date, 126) as Winner_Announced__c, usage_details as Usage_Details__c, cs.contest_status_desc as Status__c, " +
    		"submission_details as Submission_Details__c, additional_info as Additional_Info__c, " + 
    		"contest_description as Description__c, contest_requirements as Requirements__c, " +
    		"contest_image_url as Contest_Image__c, contest_logo_url as Contest_Logo__c, prize_type as Prize_Type__c, is_open_source as Release_to_Open_Source__c " + 
    		"from contest c inner join lk_contest_status cs on " +
    		"c.contest_status_id = cs.contest_status_id order by contest_id";
    operation = OperationEnum.upsert;
    externalIdFieldName = "SQL_Id__c";
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
