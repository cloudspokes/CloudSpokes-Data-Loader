package com.cloudspokes.process;

import com.sforce.async.OperationEnum;

public interface DataLoaderProcess {
  
  String getSobject();
  String getSql();
  String getCsvFile();
  OperationEnum getOperation();
  String getExternalIdFieldName();

}
