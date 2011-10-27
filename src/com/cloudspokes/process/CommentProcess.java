package com.cloudspokes.process;

import com.cloudspokes.process.DataLoaderProcess;

import com.sforce.async.OperationEnum;

public class CommentProcess implements DataLoaderProcess {
  
  private String sobject;
  private String sql;
  private OperationEnum operation;
  private String externalIdFieldName;
  private String csvDirectory;
  
  public CommentProcess(String csvDirectory) {
    this.csvDirectory = csvDirectory;
    sobject = "Challenge_Comment__c";
    sql = "select c.contest_discussion_id as sql_id__c, c.contest_id as 'Challenge__r.sql_id__c', " +
    		"c.message as 'Comment__c', u.username as 'Member__r.username__c', " +
    		"CASE WHEN c.parent_message_id = 0 THEN NULL ELSE c.parent_message_id END as 'Reply_To__r.SQL_ID__c' " +
    		"from contest_discussion c inner join aspnet_users u on c.userid = u.userid where is_active = 1";
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
