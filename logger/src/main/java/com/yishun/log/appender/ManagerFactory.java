package com.yishun.log.appender;


public interface ManagerFactory<M, T> {


  M createManager(String name, T data);
}

