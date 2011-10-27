package com.cloudspokes.process;

import com.cloudspokes.process.DataLoaderProcess;

import com.sforce.async.OperationEnum;

public class RecommendationProcess implements DataLoaderProcess {
  
  private String sobject;
  private String sql;
  private OperationEnum operation;
  private String externalIdFieldName;
  private String csvDirectory;
  
  public RecommendationProcess(String csvDirectory) {
    this.csvDirectory = csvDirectory;
    sobject = "Recommendation__c";
    sql = "select u.username as 'Member__r.username__c', r.recommendation_id as Sql_id__c, " +
    		"r.quote as Recommendation__c, u2.username as 'Recommendation_From__r.username__c' " +
    		"from recommendation r inner join aspnet_users u on r.userid = u.userid " +
    		"inner join aspnet_users u2 on r.recommended_by_userid = u2.userid";
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
