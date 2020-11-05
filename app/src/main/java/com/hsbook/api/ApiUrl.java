package com.hsbook.api;

public class ApiUrl {

    private String BASE_API = "https://moh-dhs.com/";

//    private String BASE_API = "http://hsbooks.bayonlands.com/";


    public String getBASE_API() {
        return BASE_API;
    }

    public String getDataPagination(int offset){
        String mOffset = "app.php?f=" + offset;
        return getBASE_API() + mOffset;
    }

    public String getSearchApi(){
        String SEARCH_API = "app.php?app_kw_kh=";
        return BASE_API + SEARCH_API;
    }

    public String getCoverApi(){
        return BASE_API + "docscover/";
    }

    public String getSoftFile() {
        return BASE_API + "docs/";
    }

    public String getAboutApi() {
        return BASE_API + "app.php?action=app_show_about";
    }
}
