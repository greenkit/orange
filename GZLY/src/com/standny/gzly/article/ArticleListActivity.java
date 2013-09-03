package com.standny.gzly.article;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.standny.gzly.DetailActivity;
import com.standny.gzly.MasterActivity;
import com.standny.gzly.R;
import com.standny.gzly.models.APIResultModel;
import com.standny.gzly.models.article.ArticleCategory;
import com.standny.gzly.models.article.ArticleListModel;
import com.standny.gzly.repository.APICallback;
import com.standny.gzly.repository.APIConfig;
import com.standny.gzly.repository.CGSize;
import com.standny.gzly.repository.ImageDownloadCallback;
import com.standny.gzly.repository.ImageManager;
import com.standny.gzly.repository.article.ArticleAPI;
import com.standny.ui.FingerTracker;

public class ArticleListActivity extends MasterActivity implements
        OnScrollListener {

    private Integer parentCategoryId;
    private String parentCatgoryKey;
    
    private ListView listView;
    private ArticleListAdapter mListAdapter;
    private int visibleLastIndex = 0; // 最后的可视项索引
    private int pageIndex;
    private int totalItemCount;
    private boolean isloading;
    private View loadMoreView;
    
    private ArticleAPI mArticleAPI;
    private Integer currentSelectedCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        parentCategoryId = getIntent().getIntExtra("parentCategoryId", -1);
        parentCatgoryKey = getIntent().getStringExtra("parentCatgoryKey");
        
        mArticleAPI = new ArticleAPI();
        
        listView = (ListView) findViewById(R.id.list);
        listView.setOnScrollListener(this);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View listItem,
                    int position, long id) {
                if (listItem == loadMoreView && !loadMoreView.getTag().equals("no")) {
                    if (!isloading) {
                        loadArticleList(currentSelectedCategoryId, ++pageIndex);
                    }
                    return;
                }
                ArticleListModel articleEntry = (ArticleListModel) parent.getAdapter().getItem(position);
                Intent intent = new Intent();
                intent.setClass(ArticleListActivity.this, DetailActivity.class);
                intent.putExtra("title", articleEntry.getSubject());
                intent.putExtra("url", String.format("%sdetail/%d?type=1",APIConfig.ApiUrl, 
                        articleEntry.getId()));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,
                     R.anim.slide_out_left);
            }
        });

        listView.setOnTouchListener(new FingerTracker(this));
        
        initializeAdapter();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int itemsLastIndex = mListAdapter.getCount(); // 数据集最后一项的索引
        if (!isloading && scrollState == OnScrollListener.SCROLL_STATE_IDLE
                && visibleLastIndex == itemsLastIndex
                && itemsLastIndex < this.totalItemCount) {
            pageIndex++;
            loadArticleList(currentSelectedCategoryId, pageIndex);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        // this.visibleItemCount = visibleItemCount;
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
        // 如果所有的记录选项等于数据集的条数，则移除列表底部视图
        if (this.totalItemCount > 0
                && totalItemCount == this.totalItemCount + 1) {
            // removeLoadingRow();
            loadMoreView.setTag("no");
            this.changeLoadingRowText("没有更多了");
            // Toast.makeText(this, "数据全部加载完!", Toast.LENGTH_LONG).show();
        }
    }

    private void initializeAdapter() {
        mListAdapter = new ArticleListAdapter(this);
        addLoadingRow();
        listView.setAdapter(mListAdapter);
        pageIndex = 1;
        totalItemCount = 0;
        //loadArticleList(currentSelectedCategoryId, pageIndex);
    }

    public void addLoadingRow() {
        loadMoreView = getLayoutInflater().inflate(R.layout.table_loading_row,
                null);
        loadMoreView.setTag("yes");
        listView.addFooterView(loadMoreView);
    }

    public void removeLoadingRow() {
        listView.removeFooterView(loadMoreView);
    }

    public void changeLoadingRowText(String txt) {
        TextView lbl = (TextView) loadMoreView.findViewById(R.id.title);
        lbl.setText(txt);
    }
    
    /**
     * Load article category data based on parent category id or key.
     * 
     */
    public void loadArticleCategory() {
        mArticleAPI.getArtcleCategories(parentCategoryId, parentCatgoryKey, 
                new APICallback<List<ArticleCategory>>() {
            @Override
            public void onError(String msg) {
                
            }
            @Override
            public void onCompleted(APIResultModel<List<ArticleCategory>> result) {
                //TODO add category data to bar
            }
        });
    }
    

    /**
     * Load article list based on article category id.
     * 
     * @param articleCategoryId
     * @param pageIndex
     */
    public void loadArticleList(Integer articleCategoryId, Integer pageIndex) {
        if (isloading) {
            return;
        }
        isloading = true;
        changeLoadingRowText("载入中...");
        
        mArticleAPI.getArticleList(articleCategoryId, pageIndex, 
             new APICallback<List<ArticleListModel>>() {
                 @Override
                 public void onCompleted(
                         APIResultModel<List<ArticleListModel>> result) {
                     isloading = false;
                     changeLoadingRowText("查看更多...");
                     if (result.getSuc()) {
                         totalItemCount = result.getTotalItemCount();
                         if (totalItemCount == 0) {
                             changeLoadingRowText("没有符合条件的文章.");
                         } else {
                             List<ArticleListModel> items = result.getItems();
                             mListAdapter.addItem(items);
                         }
                     } else {
                         Toast.makeText(ArticleListActivity.this,
                                 result.getMsg(), Toast.LENGTH_LONG).show();

                     }
                 }

                 @Override
                 public void onError(String msg) {
                     isloading = false;
                     AlertDialog.Builder builder = new Builder(
                             ArticleListActivity.this);
                     builder.setMessage(msg);
                     builder.setTitle("载入数据失败");
                     builder.setPositiveButton("重试", new OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog,
                                 int which) {
                             loadArticleList(currentSelectedCategoryId, ArticleListActivity.this.pageIndex);
                             dialog.dismiss();
                         }
                     });
                     builder.setNegativeButton("取消", new OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog,
                                 int which) {
                             dialog.dismiss();
                         }
                     });
                     try {
                         builder.create().show();
                     }
                     catch (Exception e) {
                         e.printStackTrace();
                     }
                 }

             });
    }

    
    static class ArticleListAdapter extends BaseAdapter {
        private List<ArticleListModel> articleList;
        private ImageManager imgManager;
        private LayoutInflater layoutInflater;
        
        public ArticleListAdapter(Context context) {
            LayoutInflater.from(context);
            articleList = new ArrayList<ArticleListModel>();
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
            imgManager = new ImageManager(new CGSize(
                    (int) Math.round(dm.widthPixels * 0.4),
                    (int) Math.round(dm.widthPixels * 0.4)));
        }

        @Override
        public int getCount() {
            return articleList.size();
        }

        @Override
        public Object getItem(int position) {
            if (position < articleList.size()) {
                return articleList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (view == null || view.getTag() == null) {
                viewHolder = new ViewHolder();
                view = layoutInflater.inflate(R.layout.article_list_item, null);
                viewHolder.ivCover = (ImageView) view.findViewById(R.id.iv_article);
                viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
                viewHolder.tvSummary = (TextView) view.findViewById(R.id.tv_summary);
                viewHolder.tvPostTime = (TextView) view.findViewById(R.id.tv_time);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            
            ArticleListModel item = articleList.get(position);
            
            viewHolder.tvTitle.setText(item.getSubject());
            viewHolder.tvSummary.setText(item.getSummary());
            viewHolder.tvPostTime.setText(item.getPostTime());
            
            if (item.getCoverPic() != null && item.getCoverPic().length() > 0) {
                Bitmap bmp = imgManager.GetImage(item.getCoverPic(),
                        new CGSize(100, 100), R.id.image,
                        new ImageDownloadCallback() {
                            @Override
                            public void onCompleted(Bitmap bmp, int tag) {
                                viewHolder.ivCover.setImageBitmap(bmp);
                            }
                            @Override
                            public void onError(String msg, int tag) {
                            }
                        });
                if (bmp != null) {
                    viewHolder.ivCover.setImageBitmap(bmp);
                } else {
                    viewHolder.ivCover.setImageResource(R.drawable.list_nopic);
                }
            } else {
                viewHolder.ivCover.setImageResource(R.drawable.list_nopic);
            }
            
            return view;
        }

        public void addItem(List<ArticleListModel> items) {
            if (items != null && items.size() > 0) {
                this.articleList.addAll(items);
            }
            notifyDataSetChanged();
        }

        public void addItem(ArticleListModel item) {
            articleList.add(item);
            this.notifyDataSetChanged();
        }
        
    }
    
    static class ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        TextView tvSummary;
        TextView tvPostTime;
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onBackPressed();
    }

}
