package com.perfection.newkeyboard.Helpers;

import com.perfection.newkeyboard.Model.UserModel;


/**
 * Created by mrhic on 8/16/2017.
 */

public class APIManager {
    static APIManager instance = null;

    APIManager() {

    }

    public static APIManager GetInstance() {
        if (instance == null)
        {
            instance = new APIManager();
        }
        return instance;
    }

   /* public async Task<object> signUpAsync(String userName, String password, String email, String country, String birthday, String gender)
    {
        try
        {
            String param = "{UserName: \"" + userName + "\", Password:\"" + password + "\", Email:\"" + email + "\", Country:\"" + country + "\", Birthdate:\"" + birthday + "\", Gender:\"" + gender + "\"}";
            return await base.ProcessPostRequestAsync<List<UserModel>>("/api/user/process.ashx?action=register", param);
        }
        catch (Exception ex)
        {
            System.Diagnostics.Debug.WriteLine("ProcessPostRequestAsync Error : " + ex.Message);
            return ex.Message;
        }
    }

    public async Task<object> loadCategories(int type, int records)
    {
        try
        {
            String param = "{" +
                    "Type: " + type + "," +
                    "Records: " + records + "}";
            return await base.ProcessPostRequestAsync<List<CategoryModel>>("/api/categories/process.ashx?action=load_categories", param);
        }
        catch (Exception ex)
        {
            System.Diagnostics.Debug.WriteLine("ProcessPostRequestAsync Error : " + ex.Message);
            return ex.Message;
        }
    }

    public async Task<object> getISTUserInfoByToken(String token)
    {
        try
        {
            return await base.ProcessGetRequestAsync<ISTModel>("https://api.instagram.com/v1/users/self?access_token=" + token);
        }
        catch (Exception ex)
        {
            System.Diagnostics.Debug.WriteLine("ProcessPostRequestAsync Error : " + ex.Message);
            return ex.Message;
        }
    }

    public async Task<object> getFBUserInfoByToken(String token)
    {
        try
        {
            return await base.ProcessGetRequestAsync<FBModel>("https://graph.facebook.com/me?access_token=" + token);
        }
        catch (Exception ex)
        {
            System.Diagnostics.Debug.WriteLine("ProcessPostRequestAsync Error : " + ex.Message);
            return ex.Message;
        }
    }

    public async Task<object> getFBUserInfoById(String token, String id, String field)
    {
        try
        {
            return await base.ProcessGetRequestAsync<FBModel>("https://graph.facebook.com/" + id + "?fields=" + field + "&access_token=" + token);
        }
        catch (Exception ex)
        {
            System.Diagnostics.Debug.WriteLine("ProcessPostRequestAsync Error : " + ex.Message);
            return ex.Message;
        }
    }

    public async Task<object> get_appsettings(String userid)
    {
        try
        {
            String param = "";
            var result = await base.ProcessPostRequestJsonAsync("/api/user/process.ashx?action=get_appsettings&userid=" + userid, param);
            return Newtonsoft.Json.JsonConvert.DeserializeObject<List<SettingModel>>(result.ToString());
        }
        catch (Exception ex)
        {
            System.Diagnostics.Debug.WriteLine("ProcessPostRequestAsync Error : " + ex.Message);
            return ex.Message;
        }
    }

    public async Task<object> save_appsettings()
    {
        try
        {
            //String param = Newtonsoft.Json.JsonConvert.SerializeObject(Singleton.GetInstance().settings);
            String param = "{" +
                    "username: \"" + Singleton.GetInstance().user.UserName + "\"," +
                    "IsAuthorizedIST: \"" + ((Singleton.GetInstance().settings.IsAuthorizedIST == 1) ? "True" : "False") + "\"," +
                    "IsEnableDownload: \"" + ((Singleton.GetInstance().settings.IsEnableDownload == 1) ? "True" : "False") + "\"," +
                    "IsEnableUpload: \"" + ((Singleton.GetInstance().settings.IsEnableUpload == 1) ? "True" : "False") + "\"," +
                    "IsEnableExternal: \"" + ((Singleton.GetInstance().settings.IsEnableExternal == 1) ? "True" : "False") + "\"," +
                    "IsEnableDelete: \"" + ((Singleton.GetInstance().settings.IsEnableDelete == 1) ? "True" : "False") + "\"," +
                    "downloadOnlyOnWifi: \"" + ((Singleton.GetInstance().settings.downloadOnlyOnWifi) ? "True" : "False") + "\"," +
                    "uploadOnlyOnWifi: \"" + ((Singleton.GetInstance().settings.uploadOnlyOnWifi) ? "True" : "False") + "\"," +
                    "suggestOnlyOnWifi: \"" + ((Singleton.GetInstance().settings.suggestOnlyOnWifi) ? "True" : "False") + "\"," +
                    "automaticDeleteOnUnlike: \"" + ((Singleton.GetInstance().settings.automaticDeleteOnUnlike) ? "True" : "False") + "\"," +
                    "deleteVideoOnUnlike: \"" + ((Singleton.GetInstance().settings.deleteVideoOnUnlike) ? "True" : "False") + "\"," +
                    "deletePhotoOnUnlike: \"" + ((Singleton.GetInstance().settings.deletePhotoOnUnlike) ? "True" : "False") + "\"," +
                    "deleteAudioOnUnlike: \"" + ((Singleton.GetInstance().settings.deleteAudioOnUnlike) ? "True" : "False") + "\"," +
                    "enableKeyboard: \"" + ((Singleton.GetInstance().settings.enableKeyboard) ? "True" : "False") + "\"," +
                    "externalInternal: \"" + ((Singleton.GetInstance().settings.externalInternal) ? "True" : "False") + "\"}";
            var result = await base.ProcessPostRequestJsonAsync("/api/user/process.ashx?action=update_appsettings", param);
            return result;//Newtonsoft.Json.JsonConvert.DeserializeObject<List<SettingModel>>(result.ToString());
        }
        catch (Exception ex)
        {
            System.Diagnostics.Debug.WriteLine("ProcessPostRequestAsync Error : " + ex.Message);
            return ex.Message;
        }
    }

    public async Task<object> AddInstagramInfo(long userid, String InstagramUsername, String AccessToken, String InstagramUserID)
    {
        try
        {
            String param = "{" +
                    "userid: " + userid + "," +
                    "InstagramUsername: \"" + InstagramUsername + "\"," +
                    "AccessToken: " + AccessToken + "," +
                    "InstagramUserID: \"" + InstagramUserID + "\"" + "," + "}";
            return await base.ProcessPostRequestAsync<List<SettingModel>>("/api/user/process.ashx?action=AddInstagramInfo", param);
        }
        catch (Exception ex)
        {
            System.Diagnostics.Debug.WriteLine("ProcessPostRequestAsync Error : " + ex.Message);
            return ex.Message;
        }
    }

    public async Task<object> GetInstagramInfo(long userid)
    {
        try
        {
            String param = "{" +
                    "userid: " + userid + "," +
                    "}";
            return await base.ProcessPostRequestAsync<List<SettingModel>>("/api/user/process.ashx?action=GetInstagramInfo", param);
        }
        catch (Exception ex)
        {
            System.Diagnostics.Debug.WriteLine("ProcessPostRequestAsync Error : " + ex.Message);
            return ex.Message;
        }
    }

    public async Task<object> DeleteInstagramUser(String userid)
    {
        try
        {
            String param = "{" +
                    "userid: " + userid + "," +
                    "}";
            return await base.ProcessPostRequestAsync<List<SettingModel>>("/api/user/process.ashx?action=DeleteInstagramUser", param);
        }
        catch (Exception ex)
        {
            System.Diagnostics.Debug.WriteLine("ProcessPostRequestAsync Error : " + ex.Message);
            return ex.Message;
        }
    }*/
}