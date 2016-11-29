package com.huangjiazhong.youlian.utils;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huangjiazhong.youlian.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 底部按钮工具类
 * Created by Administrator on 2016/10/15.
 */

public class ToolBarUtils {

    private List<TextView> mTextViews = new ArrayList<TextView>();

    public void createToolBar(LinearLayout toolBarContainer,String[] toolBarTitleArr,int[] iconArr ){
        for (int i = 0;i<iconArr.length;i++) {
            TextView tv = (TextView) View.inflate(toolBarContainer.getContext(), R.layout.toolbar_button, null);
            tv.setText(toolBarTitleArr[i]);
            //动态设置DrawableTop的属性
            tv.setCompoundDrawablePadding(2);
            tv.setCompoundDrawablesWithIntrinsicBounds(0, iconArr[i], 0, 0);
            tv.setPadding(0,10,0,0);
            //想线性布局容器中添加组建
            int with = 0;
            int height = LinearLayout.LayoutParams.MATCH_PARENT;
            LinearLayout.LayoutParams params =new LinearLayout.LayoutParams(with, height);
            params.weight=1;//设置权重
            toolBarContainer.addView(tv,params);
            //保存TextView到集合中
            mTextViews.add(tv);
            //点击事件
            final int finalI=i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //不同模块之间传递值需要用接口回调
                    //需要传值的地方用接口对象调用此方法
                    mOnToolBarClickListener.OnToolBarClick(finalI);
                }
            });

        }
    }

    /**
     * 改变底部按键状态
     * @param position
     */
    public void changeColor(int position){
        /**
         * 还原按键状态
         */
        for (TextView tv:mTextViews){
            tv.setSelected(false);
        }
        //通过selected的属性设置  控制为选中状态
        mTextViews.get(position).setSelected(true);
    }
    /**
     * 创建接口
     */
    public interface OnToolBarClickListener{
        void OnToolBarClick(int position);
    }
    /**
     * 定义接口变量
     */
    private OnToolBarClickListener mOnToolBarClickListener;
    /**
     * 暴露公共方法
     */
    public void setOnToolBarClickListener(OnToolBarClickListener onToolBarClickListener){
        mOnToolBarClickListener=onToolBarClickListener;
    }
}
