package com.cloudspokes.process;

import com.cloudspokes.process.DataLoaderProcess;

import com.sforce.async.OperationEnum;

public class SubmissionOther implements DataLoaderProcess {
  
  private String sobject;
  private String sql;
  private OperationEnum operation;
  private String externalIdFieldName;
  private String csvDirectory;
  
  public SubmissionOther(String csvDirectory) {
    this.csvDirectory = csvDirectory;
    sobject = "Challenge_Submission__c";
    sql = "select (CAST(i.contest_image_id AS varchar)+'-'+CAST(cr.contest_registration_id AS varchar)+'-other') as unique_id__c, " +
    		"i.preview_image_url as 'URL__c', 'Other' as 'Type__c', cr.contest_registration_id as 'Challenge_Participant__r.sql_id__c', " +
    		"i.contest_id as 'Challenge__r.sql_id__c', i.image_comments as 'Comments__c', " +
    		"i.isDeleted as 'Deleted__c' from contest_image i inner join contest_registration cr " +
    		"on (cr.contest_id = i.contest_id and i.userid = cr.userid) " +
    		"where i.contest_id IN (select contest_id from contest) and i.preview_image_url IS NOT NULL " +
    		"and preview_image_url != ''";
    operation = OperationEnum.upsert;
    externalIdFieldName = "Unique_ID__c";
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
