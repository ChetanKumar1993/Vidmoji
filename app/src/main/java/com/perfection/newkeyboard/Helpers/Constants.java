package com.perfection.newkeyboard.Helpers;

/**
 * Created by mrhic on 8/16/2017.
 */

public class Constants {

    public static final String BaseUrl = "http://vidmoji.com";

    // Instagram Credentials
    public static final String CLIENT_ID = "3de5a070bbbe4ee7be9a57cc3d9ca014";
    public static final String CLIENT_SECRET = "273e413447ce452fb32573b23d708dcf";
    public static final String CALLBACK_URL = "http://vidmoji.com";

    // API URLS
    public static final String LOGIN_URL = "/api/user/process.ashx?action=login";
    public static final String SIGN_UP_URL = "/api/user/process.ashx?action=register";
    public static final String ADD_INSTAGRAM_INFO_URL = "/api/user/process.ashx?action=AddInstagramInfo";
    public static final String GET_INSTAGRAM_INFO_URL = "/api/user/process.ashx?action=GetInstagramInfo";
    public static final String DELETE_INSTAGRAM_INFO_URL = "/api/user/process.ashx?action=DeleteInstagramUser";
    public static final String GET_APP_SETTINGS = "/api/user/process.ashx?action=get_appsettings";


    public static final String EXTRA_ISEXTERNAL = "ISEXTERNAL";


    // API Key Constants
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String GENDER = "Gender";
    public static final String BIRTHDAY = "Birthdate";
    public static final String EMAIL = "Email";
    public static final String COUNTRY = "Country";
    public static final String SIGNUP_USER_NAME = "UserName";
    public static final String SIGNUP_PASSWORD = "Password";
    public static final String LoginType = "LoginType";
    public static final String KEYBOARD = "Keyboard";    //this is used to let server know provide all fields needed for keyboard
    public static final String USERID = "UserID";


    //##############################################################################################
    //Please pass the userid, you can get the USERID field from when user logs-in or Signs-up
    //Use this api on app settings page
    public static final String REFRESH_APP_SETTINGS = "/api/user/process.ashx?action=get_appsettings&userid=";

    //##############################################################################################
    //Use this appi to refresh the appsettings page once user clicks on Save button
    //the parameters can be found on AppSettingsRequest
    public static final String SAVE_APP_SETTINGS = "/api/user/process.ashx?action=update_appsettings";

    //these are parameters for above API
    //public static final  userid + "," +   Pass the USERID field
    public static final String DOWNLOADONLYONWIFI = "1";  //false
    public static final String UPLOADONLYONWIFI = "1";
    public static final String SUGGESTONLYONWIFI = "1";
    public static final String AUTOMATICDELETEONUNLIKE = "1";
    public static final String EXTERNALINTERNAL = "";
    public static final String ADDFROMINSTAGRAM = "0";   //true
    public static final String ENABLEKEYBOARD = "0";

    //##############################################################################################
    //Add instagram info from app settings page. On settings page, user can link their IG account if they did manual login or facebook login
    public static final String Add_IG_INFO = "/api/user/process.ashx?action=AddInstagramInfo";
    //public static final  userid + "," +   Pass the USERID field ALSO
    public static final String INSTAGRAMUSERNAME = "Instagramusername";
    public static final String ACCESSTOKEN = "AccessToken";
    public static final String INSTAGRAMUSERID = "InstagramUserID";

    //##############################################################################################
    //Please pass the userid, you can get the USERID field from when user logs-in or Signs-up
    //Use this api on app settings page to Refresh IG account
    public static final String REFRESH_IG_INFO = "/api/user/process.ashx?action=GetInstagramInfo";
    //public static final  userid + "," +   Pass the USERID field

    //##############################################################################################
    //Please pass the userid, you can get the USERID field from when user logs-in or Signs-up
    //Use this api on app settings page to UnLInk IG account, this will NOT delete user from website
    public static final String DELETE_IG_INFO = "/api/user/process.ashx?action=DeleteInstagramUser";
    //public static final  userid + "," +   Pass the USERID field

    //##############################################################################################
    //Add instagram info ONCE user authenticates on singup/login page. THis will also update app settings page
    public static final String IG_SignUP_SignIN = "/api/user/process.ashx?action=HandleInstagramLoginRegister";
    //public static final String INSTAGRAMUSERNAME = "Instagramusername";    //pass this field too
    //public static final String ACCESSTOKEN = "AccessToken";   //pass this field too
    //public static final String INSTAGRAMUSERID = "InstagramUserID";   //pass this field too
    // public static final String LoginType = "LoginType";    //pass this field too

    //##############################################################################################
    //Please pass the imageid/photoid, you can get this field from when user logs-in or Signs-up
    //Use this when ever user sends vidmoji to Keyboard, Please send type 2 to photos and types 0 and 1 to videos
    public static final String Update_Num_Of_Times_Used_Photos = "/api/photos/process.ashx?action=Update_Num_of_Times_Used";
    //THis one is for videos/audios, type 0 and type 1
    public static final String Update_Num_Of_Times_Used_Vidoes_audios = "/api/videos/process.ashx?action=Update_Num_of_Times_Used";

    public static final String STATUS = "status";
    public static final String MESSAGE = "message";


    // Date formats
    public static final String DATE_FORMAT_YEAR = "year";
    public static final String DATE_FORMAT_MONTH = "month";
    public static final String DATE_FORMAT_DAY = "day";
    public static final String DATE_FORMAT_MM_DD_YYYY = "MM/dd/yyyy";
    public static final String DATE_FORMAT_DD_MMM_YYYY = "dd MMM, yyyy";
    public static final String DATE_FORMAT = "date_format";
    public static final String DATE_FORMAT_DD_MM_YYYY_SLASH = "dd/MM/yyyy";

    // Countries List
    public static String[] countryArray = {"Afghanistan", "Albania", "Algeria",
            "American Samoa", "Andorra",
            "Angola", "Anguilla", "Antarctica", "Antigua And Barbuda", "Argentina",
            "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
            "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus",
            "Belgium", "Belize", "Benin", "Bermuda", "Bhutan",
            "Bolivia", "Bosnia Hercegovina", "Botswana", "Bouvet Island", "Brazil",
            "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Byelorussian SSR",
            "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands",
            "Central African Republic", "Chad", "Chile", "China", "Christmas Island",
            "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Cook Islands",
            "Costa Rica", "Cote D'Ivoire", "Croatia", "Cuba", "Cyprus",
            "Czech Republic", "Czechoslovakia", "Denmark", "Djibouti", "Dominica",
            "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador",
            "England", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia",
            "Falkland Islands", "Faroe Islands", "Fiji", "Finland", "France",
            "Gabon", "Gambia", "Georgia", "Germany", "Ghana",
            "Gibraltar", "Great Britain", "Greece", "Greenland", "Grenada",
            "Guadeloupe", "Guam", "Guatemela", "Guernsey", "Guiana",
            "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard Islands",
            "Honduras", "Hong Kong", "Hungary", "Iceland", "India",
            "Indonesia", "Iran", "Iraq", "Ireland", "Isle Of Man",
            "Israel", "Italy", "Jamaica", "Japan", "Jersey",
            "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, South",
            "Korea, North", "Kuwait", "Kyrgyzstan", "Lao People's Dem. Rep.", "Latvia",
            "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein",
            "Lithuania", "Luxembourg", "Macau", "Macedonia", "Madagascar",
            "Malawi", "Malaysia", "Maldives", "Mali", "Malta",
            "Mariana Islands", "Marshall Islands", "Martinique", "Mauritania", "Mauritius",
            "Mayotte", "Mexico", "Micronesia", "Moldova", "Monaco",
            "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar",
            "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles",
            "Neutral Zone", "New Caledonia", "New Zealand", "Nicaragua", "Niger",
            "Nigeria", "Niue", "Norfolk Island", "Northern Ireland", "Norway",
            "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea",
            "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland",
            "Polynesia", "Portugal", "Puerto Rico", "Qatar", "Reunion",
            "Romania", "Russian Federation", "Rwanda", "Saint Helena", "Saint Kitts",
            "Saint Lucia", "Saint Pierre", "Saint Vincent", "Samoa", "San Marino",
            "Sao Tome and Principe", "Saudi Arabia", "Scotland", "Senegal", "Seychelles",
            "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands",
            "Somalia", "South Africa", "Spain", "Sri Lanka",
            "Sudan", "Suriname", "Svalbard", "Swaziland", "Sweden",
            "Switzerland", "Syria", "Taiwan", "Tajikista", "Tanzania",
            "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago",
            "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu",
            "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States",
            "Uruguay", "Uzbekistan", "Vanuatu", "Vatican City State", "Venezuela",
            "Vietnam", "Virgin Islands", "Wales", "Western Sahara", "Yemen",
            "Yugoslavia", "Zaire", "Zambia", "Zimbabwe"};

    public static final String IS_DOWNLOAD_WIFI = "downloadOnlyOnWifi";
    public static final String IS_UPLOAD_WIFI = "uploadOnlyOnWifi";
    public static final String SUGGEST_ON_WIFI = "suggestOnlyOnWifi";
    public static final String AUTODELETE_UNLIKE = "automaticDeleteOnUnlike";
    public static final String ENABLE_KEYBOARD = "enableKeyboard";
    public static final String ADD_FROM_INSTAGRAM = "";
    public static final String EXTERNAL_INTERNAL = "externalInternal";
}

