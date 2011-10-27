package com.cloudspokes.process;

import com.cloudspokes.process.DataLoaderProcess;

import com.sforce.async.OperationEnum;

/** NO LONGER BEING USED. SEE ChallengeParticipantProcess INSTEAD **/
public class ParticipantProcess implements DataLoaderProcess {
  
  private String sobject;
  private String sql;
  private OperationEnum operation;
  private String externalIdFieldName;
  private String csvDirectory;
  
  public ParticipantProcess(String csvDirectory) {
    this.csvDirectory = csvDirectory;
    sobject = "Challenge_Participant__c";
    sql = "select u.username as 'Member__r.username__c', " +
    		"contest_registration_id as SQL_Id__c, " + 
    		"contest_id as 'Challenge__r.SQL_Id__c' from contest_registration cr inner join " +
    		"aspnet_users u on cr.userid = u.userid where contest_id IN (select contest_id from contest)";
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
