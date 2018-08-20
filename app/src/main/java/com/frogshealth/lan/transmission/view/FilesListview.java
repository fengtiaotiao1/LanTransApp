package com.frogshealth.lan.transmission.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frogshealth.lan.transmission.R;
import com.frogshealth.lan.transmission.utils.Const;

import java.io.File;

/**********************************************************************
 *
 * 文件列表布局
 *
 * @类名 FilesListview
 * @包名 com.frogshealth.lan.transmission.view
 * @author hanchao@frogshealth.com
 * @创建日期 2018/8/13
 ***********************************************************************/
public class FilesListview extends LinearLayout {
    /**
     * 文件列表
     */
    private RecyclerView mFilesListView;
    /**
     * 文件集合
     */
    private File[] mFiles;
    /**
     * Adapter
     */
    private FileAdapter mAdapter;

    public FilesListview(Context context) {
        super(context);
    }

    public FilesListview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FilesListview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mFilesListView = (RecyclerView) getChildAt(0);
        initFiles();
        initRecycleView();
    }

    /**
     * 得到目录下所有文件
     */
    void initFiles() {
        File rootPath = new File(Const.PATH);
        mFiles = rootPath.listFiles();
    }

    /**
     * 初始化ListView
     */
    void initRecycleView() {
        mFilesListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mFilesListView.setAdapter(mAdapter = new FileAdapter());

    }

    class FileAdapter extends RecyclerView.Adapter<FileAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    FilesListview.this.getContext()).inflate(R.layout.item_home, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.mTv.setText(mFiles[position].getName());
        }

        @Override
        public int getItemCount() {
            return mFiles.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            /**
             * TextView
             */
            private TextView mTv;

            MyViewHolder(View view) {
                super(view);
                mTv = view.findViewById(R.id.id_num);
            }
        }
    }

}
