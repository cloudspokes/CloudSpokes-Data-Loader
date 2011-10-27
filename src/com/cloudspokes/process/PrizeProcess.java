package com.cloudspokes.process;

import com.cloudspokes.process.DataLoaderProcess;

import com.sforce.async.OperationEnum;

public class PrizeProcess implements DataLoaderProcess {
  
  private String sobject;
  private String sql;
  private OperationEnum operation;
  private String externalIdFieldName;
  private String csvDirectory;
  
  public PrizeProcess(String csvDirectory) {
    this.csvDirectory = csvDirectory;
    sobject = "Challenge_Prize__c";
    sql = "select contest_prize_id as SQL_Id__c, contest_id as 'Challenge__r.SQL_Id__c', prize as Prize__c, " +
    		"prize_point as Points__c, prize_value as Value__c, place as Place__c, " +
    		"CONVERT(varchar,contest_id) + '-' + CONVERT(varchar,prize_value) as Unique_Category__c from contest_prize";
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
