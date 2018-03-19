package com.test;

/**
 * Created by bjliuzhanyong on 2018/3/8.
 */

public class AppLogVo {
  private String id;
  // 公共区域
  private String uid;
  private String device;
  private String appCode;
  private String product;
  private String platform;
  private String channel;
  private String logLevel;
  private String appVer;
  private String osVer;

  // 个性化区域
  private String account;
  private String network;
  private String operator;
  private String ip;
  private Long logWriteTime;
  private String logFunction;
  private String log;
  private String location;

  // 后台自身获取数据
  private Long sessionStartTime;
  private String province;
  private String city;
  private String ISP;

  protected AppLogVo() {

  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getDevice() {
    return device;
  }

  public void setDevice(String device) {
    this.device = device;
  }

  public String getAppCode() {
    return appCode;
  }

  public void setAppCode(String appCode) {
    this.appCode = appCode;
  }

  public String getProduct() {
    return product;
  }

  public void setProduct(String product) {
    this.product = product;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getLogLevel() {
    return logLevel;
  }

  public void setLogLevel(String logLevel) {
    this.logLevel = logLevel;
  }

  public String getAppVer() {
    return appVer;
  }

  public void setAppVer(String appVer) {
    this.appVer = appVer;
  }

  public String getOsVer() {
    return osVer;
  }

  public void setOsVer(String osVer) {
    this.osVer = osVer;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getNetwork() {
    return network;
  }

  public void setNetwork(String network) {
    this.network = network;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public Long getLogWriteTime() {
    return logWriteTime;
  }

  public void setLogWriteTime(Long logWriteTime) {
    this.logWriteTime = logWriteTime;
  }

  public String getLogFunction() {
    return logFunction;
  }

  public void setLogFunction(String logFunction) {
    this.logFunction = logFunction;
  }

  public String getLog() {
    return log;
  }

  public void setLog(String log) {
    this.log = log;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Long getSessionStartTime() {
    return sessionStartTime;
  }

  public void setSessionStartTime(Long sessionStartTime) {
    this.sessionStartTime = sessionStartTime;
  }

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getISP() {
    return ISP;
  }

  public void setISP(String ISP) {
    this.ISP = ISP;
  }

  @Override
  public String toString() {
    return String.format("Log content is id %s, uid %s, device %s, appCode %s, product %s, platform %s, " +
            "channel %s, logLevel %s, appVer %s, osVer %s, account %s, network %s, operator %s, ip %s, " +
            "logWriteTime %s, logFunction %s, log %s, location %s", id, uid, device, appCode, product, platform, channel,
        logLevel, appVer, osVer, account, network, operator, ip, logWriteTime, logFunction, log, location);
  }
}
