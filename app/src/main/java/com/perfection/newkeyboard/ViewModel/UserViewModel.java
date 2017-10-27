package com.perfection.newkeyboard.ViewModel;

import com.perfection.newkeyboard.Model.UserModel;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mrhic on 8/16/2017.
 */

public class UserViewModel{

    public void main(String[] args)
    {
        //InitCountries();
        //InituserContentLayoutPages();
    }

    UserModel _User;
    public UserModel User;

    public UserModel get_User() {
        return _User;
    }

    public UserViewModel set_User(UserModel _User) {
        this._User = _User;
        return this;
    }

    public List<String> get_Gender() {
        return _Gender;
    }

    public UserViewModel set_Gender(List<String> _Gender) {
        this._Gender = _Gender;
        return this;
    }

    List<String> _Gender = Arrays.asList("Male", "Female");

    List<String> _Countries;

    public List<String> getCountries() {
        return Countries;
    }

    public List<String> Countries;
    
    /*void InitCountries()
    {
        _Countries = Arrays.asList(DBManager.GetInstance().SelectCountry());
        foreach (CountryModel item in countries)
        _Countries.Add(item.Name);
    }

    public async Task UserViewModel(String userName, String userPassword, Action success, Action failure = null)
    {
        if (!Functions.CheckConnectivity())
        {
            App.Current.MainPage.DisplayAlert("Warning", "Sorry but newtork is disconnected now", "OK");
            if (failure != null)
                failure();
            return;
        }

        IsBusy = true;
        var result = await APIManager.GetInstance().signInAsync(userName, userPassword);
        IsBusy = false;
        if (result != null)
        {
            if (result is List<UserModel>)
            {
                Singleton.GetInstance().user = ((List<UserModel>)result)[0];
                Singleton.GetInstance().user.Password = userPassword;
                success();
                return;
            }
                else if (result is Newtonsoft.Json.Linq.JObject)
            {
                await App.Current.MainPage.DisplayAlert("Warning", ((Newtonsoft.Json.Linq.JObject)result)["message"].ToString(), "OK");
            }
                else
            {
                await App.Current.MainPage.DisplayAlert("Warning", result.ToString(), "OK");
            }
        }

        if (failure != null)
            failure();
    }

    public async Task<bool> UserViewModel(String userName, String userPassword, String mail, String country, String birthDate, String gender)
    {
        IsBusy = true;
        var result = (Newtonsoft.Json.Linq.JObject)await APIManager.GetInstance().signUpAsync(userName, userPassword, mail, country, birthDate, gender);
        IsBusy = false;
        if (result != null)
        {
            await App.Current.MainPage.DisplayAlert("Warning", result["message"].ToString(), "OK");
            Singleton.GetInstance().user = new UserModel()
            {
                UserName = userName,
                Password = userPassword,
                Email = mail,
                CountryName = country,
                Gender = gender,
                BirthDate = birthDate
            };
            if (result["status"].Equals("success"))
                return true;
            else
                return false;
        }
        return false;
    }

    public async Task UserViewModel()
    {
        IsBusy = true;
        var result = await APIManager.GetInstance().updateProfile(_User);
        IsBusy = false;
        if (result != null)
        {
            if (result is String)
            {
                await App.Current.MainPage.DisplayAlert("Profile Update", result.ToString(), "OK");
                return;
            }
            var data = (Newtonsoft.Json.Linq.JObject)result;
            await App.Current.MainPage.DisplayAlert("Profile Update", data["message"].ToString(), "OK");
            if (data["status"].Equals("success"))
            {
                Singleton.GetInstance().user = _User;
            }
        }
    }

    public async Task UserViewModel(String email, String password)
    {
        IsBusy = true;
        var result = (Newtonsoft.Json.Linq.JObject)await APIManager.GetInstance().setupEmail(_User.UserName, password, email);
        IsBusy = false;
        if (result != null)
        {
            await App.Current.MainPage.DisplayAlert("Email Verification", result["message"].ToString(), "OK");
            if (result["status"].Equals("success"))
            {
                _User.Email = email;
                Singleton.GetInstance().user = _User;
            }
        }
    }

    public async Task UserViewModel(String oldPassword, String newPassword)
    {
        IsBusy = true;
        var result = (Newtonsoft.Json.Linq.JObject)await APIManager.GetInstance().changePassword(_User.UserName, oldPassword, newPassword);
        IsBusy = false;
        if (result != null)
        {
            await App.Current.MainPage.DisplayAlert("Warning", result["message"].ToString(), "OK");
            if (result["status"].Equals("success"))
            {
                Singleton.GetInstance().user = _User;
            }
        }
    }

    public async Task<object> UserViewModel()
    {
        IsBusy = true;
        var result = await APIManager.GetInstance().GetMyOverviewCounts(Singleton.GetInstance().user.UserName);
        IsBusy = false;
        if (result != null)
        {
            if (result is LikeCountModel)
            {
                var data = (LikeCountModel)result;
                User.Video_Favorited = data.VideoLikeCount;
                User.Count_Audio_Favorites = data.AudioLikeCount;
                User.Photo_Favorited = data.PictureLikeCount;
            }
        }

        return result;
    }

    public async Task UserViewModel()
    {
        IsBusy = true;
        try
        {
            var result = await APIManager.GetInstance().get_appsettings(Singleton.GetInstance().user.UserId);
            IsBusy = false;
            if (result != null)
            {
                if (result is List<SettingModel>)
                {
                    Singleton.GetInstance().settings = ((List<SettingModel>)result)[0];
                    Singleton.GetInstance().settings.Id = Singleton.GetInstance().user.UserId;
                    DBManager.GetInstance().InsertSettings(Singleton.GetInstance().settings);
                    //success();
                }
					else if (result is Newtonsoft.Json.Linq.JObject)
                {
                    await App.Current.MainPage.DisplayAlert("Warning", ((Newtonsoft.Json.Linq.JObject)result)["message"].ToString(), "OK");
                }
					else
                {
                    await App.Current.MainPage.DisplayAlert("Warning", result.ToString(), "OK");
                }
            }
        }
        catch (Exception ex)
        {
        }
        IsBusy = false;
    }

    public async Task UserViewModel()
    {
        IsBusy = true;
        var result = (Newtonsoft.Json.Linq.JObject)await APIManager.GetInstance().save_EmailOptions();
        IsBusy = false;
        if (result != null)
        {
            await App.Current.MainPage.DisplayAlert("Email Options", result["message"].ToString(), "OK");
        }
    }*/

}






