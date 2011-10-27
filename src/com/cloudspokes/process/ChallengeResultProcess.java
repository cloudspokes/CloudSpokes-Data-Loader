package com.cloudspokes.process;

import com.cloudspokes.process.DataLoaderProcess;

import com.sforce.async.OperationEnum;

/** NO LONGER BEING USED. SEE ChallengeParticipantProcess INSTEAD **/
public class ChallengeResultProcess implements DataLoaderProcess {
  
  private String sobject; 
  private String sql;
  private OperationEnum operation;
  private String externalIdFieldName;
  private String csvDirectory;
  
  public ChallengeResultProcess(String csvDirectory) {
    this.csvDirectory = csvDirectory;
    sobject = "Challenge_Participant__c";
    sql = "select r.place as place__c, r.score as score__c, r.points as points_awarded__c, cr.contest_registration_id as sql_id__c from challenge_result r " +
    		"inner join contest_registration cr on (r.contest_id = cr.contest_id and r.userid = cr.userid) " +
    		"where cr.contest_id IN (select contest_id from contest)";
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
