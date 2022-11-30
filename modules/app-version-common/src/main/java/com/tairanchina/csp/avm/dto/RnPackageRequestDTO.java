package com.tairanchina.csp.avm.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class RnPackageRequestDTO {
//    @Schema(description= "主键ID，非必填，可能部分编辑操作时需要")
//    private String id;
    @Schema(description= "模块名称（约定）")
    private String rnName;
    @Schema(description= "RN包模块通用昵称")
    private String rnNickName;
    @Schema(description= "RN包类型1或2，1 android ; 2 ios")
    private Integer rnType;
    @Schema(description= "RN包资源地址")
    private String resourceUrl;
    @Schema(description= "RN包大小(KB)")
    private Integer rnSize;
    @Schema(description= "RN包版本号")
    private String rnVersion;
    @Schema(description= "包更新日志")
    private String rnUpdateLog;
    @Schema(description= "RN包状态（0-关闭；1-线上开启；2-测试需要）")
    private Integer rnStatus;
    @Schema(description= "最小版本")
    private String versionMin;
    @Schema(description= "最大版本")
    private String versionMax;

    public String getVersionMin() {
        return versionMin;
    }

    public void setVersionMin(String versionMin) {
        this.versionMin = versionMin;
    }

    public String getVersionMax() {
        return versionMax;
    }

    public void setVersionMax(String versionMax) {
        this.versionMax = versionMax;
    }

    public String getRnName() {
        return rnName;
    }

    public void setRnName(String rnName) {
        this.rnName = rnName;
    }

    public String getRnNickName() {
        return rnNickName;
    }

    public void setRnNickName(String rnNickName) {
        this.rnNickName = rnNickName;
    }

    public Integer getRnType() {
        return rnType;
    }

    public void setRnType(Integer rnType) {
        this.rnType = rnType;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public Integer getRnSize() {
        return rnSize;
    }

    public void setRnSize(Integer rnSize) {
        this.rnSize = rnSize;
    }

    public String getRnVersion() {
        return rnVersion;
    }

    public void setRnVersion(String rnVersion) {
        this.rnVersion = rnVersion;
    }

    public String getRnUpdateLog() {
        return rnUpdateLog;
    }

    public void setRnUpdateLog(String rnUpdateLog) {
        this.rnUpdateLog = rnUpdateLog;
    }

    public Integer getRnStatus() {
        return rnStatus;
    }

    public void setRnStatus(Integer rnStatus) {
        this.rnStatus = rnStatus;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
}
