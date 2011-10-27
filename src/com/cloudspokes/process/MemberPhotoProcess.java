package com.cloudspokes.process;

import com.cloudspokes.process.DataLoaderProcess;

import com.sforce.async.OperationEnum;

public class MemberPhotoProcess implements DataLoaderProcess {
  
  private String sobject;
  private String sql;
  private OperationEnum operation;
  private String externalIdFieldName;
  private String csvDirectory;
  
  public MemberPhotoProcess(String csvDirectory) {
    this.csvDirectory = csvDirectory;
    sobject = "Member__c";
    sql = "select u.username as Username__c, pp.image_url as 'Profile_Pic__c' from profile_photo pp " +
    		"inner join aspnet_users u on pp.userid = u.userid";
    operation = OperationEnum.upsert;
    externalIdFieldName = "Username__c";
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
