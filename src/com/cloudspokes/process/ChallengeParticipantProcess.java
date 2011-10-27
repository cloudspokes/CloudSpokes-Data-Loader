package com.cloudspokes.process;

import com.cloudspokes.process.DataLoaderProcess;

import com.sforce.async.OperationEnum;

// this is the one that uses the view
public class ChallengeParticipantProcess implements DataLoaderProcess {
  
  private String sobject;
  private String sql;
  private OperationEnum operation;
  private String externalIdFieldName;
  private String csvDirectory;
  
  public ChallengeParticipantProcess(String csvDirectory) {
    this.csvDirectory = csvDirectory;
    sobject = "Challenge_Participant__c";
    sql = "select username as 'Member__r.username__c', " +
    		"contest_registration_id as SQL_Id__c, " + 
    		"contest_id as 'Challenge__r.SQL_Id__c', PrizeAmount as 'Money_Awarded__c', " +
    		"place as Place__c, score as Score__c, points as Points_Awarded__c " +
    		"from vwContestAndParticipants";
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
