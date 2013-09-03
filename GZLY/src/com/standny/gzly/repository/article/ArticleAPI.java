package com.standny.gzly.repository.article;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.standny.gzly.models.APIResultModel;
import com.standny.gzly.models.article.ArticleCategory;
import com.standny.gzly.models.article.ArticleListModel;
import com.standny.gzly.repository.APICallback;
import com.standny.gzly.repository.APIConfig;
import com.standny.gzly.repository.JsonAPICallback;
import com.standny.gzly.repository.JsonAPIClient;

public class ArticleAPI {
    private Integer pageSize;
    private APICallback<List<ArticleCategory>> getArticleCategoryListCallback;
    private APICallback<List<ArticleListModel>> getArticleListCallback;

    public ArticleAPI() {
        pageSize = 10;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void getArtcleCategories(Integer parentCategoryId, String key,
            APICallback<List<ArticleCategory>> callback) {
        getArticleCategoryListCallback = callback;
        JsonAPIClient jc = new JsonAPIClient(new JsonAPICallback() {
            @Override
            public void onCompleted(JSONObject jsonObj) {
                APIResultModel<List<ArticleCategory>> result = new APIResultModel<List<ArticleCategory>>();
                result.setSuc(jsonObj.optBoolean("Suc", false));
                result.setMsg(jsonObj.optString("Msg", ""));
                if (result.getSuc()) {
                    JSONArray itemArray = jsonObj.optJSONArray("Items");
                    try {
                        if (itemArray != null) {
                            List<ArticleCategory> items = new ArrayList<ArticleCategory>();
                            for (int i = 0; i < itemArray.length(); i++) {
                                try {
                                    JSONObject jSONObject = ((JSONObject) itemArray.opt(i));
                                    ArticleCategory item = new ArticleCategory();
                                    item.setTitle(jSONObject.getString("title"));
                                    item.setId(jSONObject.getInt("id"));
                                    items.add(item);
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            result.setItems(items);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (getArticleCategoryListCallback != null) {
                    getArticleCategoryListCallback.onCompleted(result);
                }
            }

            @Override
            public void onError(String msg) {
                if (getArticleCategoryListCallback != null) {
                    getArticleCategoryListCallback.onError(msg);
                }
            }

        });

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", parentCategoryId.toString()));
        params.add(new BasicNameValuePair("key", key));
        jc.BeginPost(APIConfig.ApiUrl + "GetArticleClass", params);
    }
    
    public void getArticleList(Integer typeId, Integer pageIndex, 
            APICallback<List<ArticleListModel>> callback) {
        getArticleListCallback = callback;
        
        JsonAPIClient jc=new JsonAPIClient(new JsonAPICallback(){

            @Override
            public void onCompleted(JSONObject jsonObj) {
               APIResultModel<List<ArticleListModel>> result=new APIResultModel<List<ArticleListModel>>();
               result.setSuc(jsonObj.optBoolean("Suc",false));
               result.setMsg(jsonObj.optString("Msg", ""));
               result.setCurrentPageIndex(jsonObj.optInt("PageIndex"));
               result.setTotalItemCount(jsonObj.optInt("TotalItemCount"));
               result.setTotalPageCount(jsonObj.optInt("TotalPageCount"));
               List<ArticleListModel> items=new ArrayList<ArticleListModel>();
               if (result.getSuc()){
                   JSONArray itemArray = jsonObj.optJSONArray("Items"); 
                   if (items!=null){
                      for(int i=0;i<itemArray.length();i++){
                         try{
                         JSONObject json1 = ((JSONObject)itemArray.opt(i));
                         ArticleListModel item = new ArticleListModel();
                         item.setId(json1.getInt("id"));
                         item.setSubject(json1.getString("subject"));
                         item.setCoverPic(json1.getString("coverPic"));
                         item.setSummary(json1.getString("summary"));
                         item.setPostTime(json1.getString("postTime"));
                         items.add(item);
                         }
                         catch(Exception e){
                            e.printStackTrace();
                         }
                      }
                   }
               }
               result.setItems(items);
               if (getArticleListCallback != null) {
                   getArticleListCallback.onCompleted(result);
               }
            }

            @Override
            public void onError(String msg) {
               if (getArticleListCallback != null) {
                   getArticleListCallback.onError(msg);
               }
            }
            
         });

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("typeId", typeId.toString()));
        params.add(new BasicNameValuePair("pageIndex", pageIndex.toString()));
        params.add(new BasicNameValuePair("pageSize", pageSize.toString()));
        jc.BeginPost(APIConfig.ApiUrl + "GetArticleList", params);
    }

}
