//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not limited to reproduction, retransmission, communication, display, mirror, download, modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.wpf.bluetooth.spp.deviceExport.model;


public class CmmBtDeviceInfo {
    private int battery;
    private int volume;
    private boolean isPlaying;
    private boolean isCharging;
    private boolean isMute;
    private String firmware = "";
    private String SN = "";
    private String deviceName = "";
    private boolean isSuccess;

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isCharging() {
        return isCharging;
    }

    public void setCharging(boolean charging) {
        isCharging = charging;
    }

    public String getFirmware() {
        return firmware;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    @Override
    public String toString() {
        return "CmmBtDeviceInfo{" +
                "battery=" + battery +
                ", volume=" + volume +
                ", isPlaying=" + isPlaying +
                ", isChanging=" + isCharging +
                ", isMute=" + isMute +
                ", firmware='" + firmware + '\'' +
                ", SN='" + SN + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", isSuccess=" + isSuccess +
                '}';
    }
}
