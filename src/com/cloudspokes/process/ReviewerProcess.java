package com.cloudspokes.process;

import com.cloudspokes.process.DataLoaderProcess;

import com.sforce.async.OperationEnum;

public class ReviewerProcess implements DataLoaderProcess {
  
  private String sobject;
  private String sql;
  private OperationEnum operation;
  private String externalIdFieldName;
  private String csvDirectory;
  
  public ReviewerProcess(String csvDirectory) {
    this.csvDirectory = csvDirectory;
    sobject = "Challenge_Reviewer__c";
    sql = "select c.contest_reviewer_id as sql_id__c, c.contest_id as 'Challenge__r.sql_id__c'," +
    		"u.username as 'Member__r.username__c' from contest_reviewer c inner join aspnet_users u on c.userid = u.userid";
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
