package com.perfection.newkeyboard.Model;

/**
 * Created by mrhic on 8/16/2017.
 */

public class SettingModel {
    public String Id;
    public int IsAuthorizedIST;

    public String getId() {
        return Id;
    }

    public SettingModel setId(String id) {
        Id = id;
        return this;
    }

    public int getIsAuthorizedIST() {
        return IsAuthorizedIST;
    }

    public SettingModel setIsAuthorizedIST(int isAuthorizedIST) {
        IsAuthorizedIST = isAuthorizedIST;
        return this;
    }

    public int getIsEnableDownload() {
        return IsEnableDownload;
    }

    public SettingModel setIsEnableDownload(int isEnableDownload) {
        IsEnableDownload = isEnableDownload;
        return this;
    }

    public int getIsEnableUpload() {
        return IsEnableUpload;
    }

    public SettingModel setIsEnableUpload(int isEnableUpload) {
        IsEnableUpload = isEnableUpload;
        return this;
    }

    public int getIsEnableExternal() {
        return IsEnableExternal;
    }

    public SettingModel setIsEnableExternal(int isEnableExternal) {
        IsEnableExternal = isEnableExternal;
        return this;
    }

    public int getIsEnableDelete() {
        return IsEnableDelete;
    }

    public SettingModel setIsEnableDelete(int isEnableDelete) {
        IsEnableDelete = isEnableDelete;
        return this;
    }

    public String getFBToken() {
        return FBToken;
    }

    public SettingModel setFBToken(String FBToken) {
        this.FBToken = FBToken;
        return this;
    }

    public String getISTToken() {
        return ISTToken;
    }

    public SettingModel setISTToken(String ISTToken) {
        this.ISTToken = ISTToken;
        return this;
    }

    public boolean isDownloadOnlyOnWifi() {
        return downloadOnlyOnWifi;
    }

    public SettingModel setDownloadOnlyOnWifi(boolean downloadOnlyOnWifi) {
        this.downloadOnlyOnWifi = downloadOnlyOnWifi;
        return this;
    }

    public boolean isUploadOnlyOnWifi() {
        return uploadOnlyOnWifi;
    }

    public SettingModel setUploadOnlyOnWifi(boolean uploadOnlyOnWifi) {
        this.uploadOnlyOnWifi = uploadOnlyOnWifi;
        return this;
    }

    public boolean isSuggestOnlyOnWifi() {
        return suggestOnlyOnWifi;
    }

    public SettingModel setSuggestOnlyOnWifi(boolean suggestOnlyOnWifi) {
        this.suggestOnlyOnWifi = suggestOnlyOnWifi;
        return this;
    }

    public boolean isAutomaticDeleteOnUnlike() {
        return automaticDeleteOnUnlike;
    }

    public SettingModel setAutomaticDeleteOnUnlike(boolean automaticDeleteOnUnlike) {
        this.automaticDeleteOnUnlike = automaticDeleteOnUnlike;
        return this;
    }

    public boolean isDeleteVideoOnUnlike() {
        return deleteVideoOnUnlike;
    }

    public SettingModel setDeleteVideoOnUnlike(boolean deleteVideoOnUnlike) {
        this.deleteVideoOnUnlike = deleteVideoOnUnlike;
        return this;
    }

    public boolean isDeletePhotoOnUnlike() {
        return deletePhotoOnUnlike;
    }

    public SettingModel setDeletePhotoOnUnlike(boolean deletePhotoOnUnlike) {
        this.deletePhotoOnUnlike = deletePhotoOnUnlike;
        return this;
    }

    public boolean isDeleteAudioOnUnlike() {
        return deleteAudioOnUnlike;
    }

    public SettingModel setDeleteAudioOnUnlike(boolean deleteAudioOnUnlike) {
        this.deleteAudioOnUnlike = deleteAudioOnUnlike;
        return this;
    }

    public boolean isEnableKeyboard() {
        return enableKeyboard;
    }

    public SettingModel setEnableKeyboard(boolean enableKeyboard) {
        this.enableKeyboard = enableKeyboard;
        return this;
    }

    public boolean isInstalledKeyboard() {
        return installedKeyboard;
    }

    public SettingModel setInstalledKeyboard(boolean installedKeyboard) {
        this.installedKeyboard = installedKeyboard;
        return this;
    }

    public boolean isExternalInternal() {
        return externalInternal;
    }

    public SettingModel setExternalInternal(boolean externalInternal) {
        this.externalInternal = externalInternal;
        return this;
    }

    public boolean isAddFromInstagram() {
        return addFromInstagram;
    }

    public SettingModel setAddFromInstagram(boolean addFromInstagram) {
        this.addFromInstagram = addFromInstagram;
        return this;
    }

    public String getInstagramUsername() {
        return InstagramUsername;
    }

    public SettingModel setInstagramUsername(String instagramUsername) {
        InstagramUsername = instagramUsername;
        return this;
    }

    public String getAccessToken() {
        return AccessToken;
    }

    public SettingModel setAccessToken(String accessToken) {
        AccessToken = accessToken;
        return this;
    }

    public String getInstagramUserID() {
        return InstagramUserID;
    }

    public SettingModel setInstagramUserID(String instagramUserID) {
        InstagramUserID = instagramUserID;
        return this;
    }

    public int IsEnableDownload;
    public int IsEnableUpload;
    public int IsEnableExternal;
    public int IsEnableDelete;
    public String FBToken;
    public String ISTToken;

    public boolean downloadOnlyOnWifi;
    public boolean uploadOnlyOnWifi;
    public boolean suggestOnlyOnWifi;
    public boolean automaticDeleteOnUnlike;
    public boolean deleteVideoOnUnlike;
    public boolean deletePhotoOnUnlike;
    public boolean deleteAudioOnUnlike;
    public boolean enableKeyboard;
    public boolean installedKeyboard;

    public boolean externalInternal;

    public boolean addFromInstagram;

    public String InstagramUsername;
    public String AccessToken;
    public String InstagramUserID;

}