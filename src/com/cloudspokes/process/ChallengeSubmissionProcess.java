package com.cloudspokes.process;

import com.cloudspokes.process.DataLoaderProcess;

import com.sforce.async.OperationEnum;

/** NO LONGER BEING USED. SEE ChallengeParticipantProcess INSTEAD **/
public class ChallengeSubmissionProcess implements DataLoaderProcess {
  
  private String sobject;
  private String sql;
  private OperationEnum operation;
  private String externalIdFieldName;
  private String csvDirectory;
  
  public ChallengeSubmissionProcess(String csvDirectory) {
    this.csvDirectory = csvDirectory;
    sobject = "Challenge_Participant__c";
    sql = "select i.image_url as 'Submission_File__c', cr.contest_registration_id as sql_id__c " +
    		"from contest_image i inner join contest_registration cr on " +
    		"(cr.contest_id = i.contest_id and i.userid = cr.userid) " +
    		"where i.contest_id IN (select contest_id from contest)";
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
