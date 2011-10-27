package com.cloudspokes.process;

import com.cloudspokes.process.DataLoaderProcess;

import com.sforce.async.OperationEnum;

public class ChallengeCategory implements DataLoaderProcess {
  
  private String sobject;
  private String sql;
  private OperationEnum operation;
  private String externalIdFieldName;
  private String csvDirectory;
  
  public ChallengeCategory(String csvDirectory) {
    this.csvDirectory = csvDirectory;
    sobject = "Challenge_Category__c";
    sql = "select contest_category_id as SQL_Id__c, contest_id as 'Challenge__r.SQL_Id__c', category_id as 'Category__r.SQL_Id__c', CONVERT(varchar,contest_id) + '-' + CONVERT(varchar,category_id) as Unique_Category__c from contest_category";
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
