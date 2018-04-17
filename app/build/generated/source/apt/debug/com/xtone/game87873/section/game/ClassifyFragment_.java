//
// DO NOT EDIT THIS FILE.Generated using AndroidAnnotations 3.3.2.
//  You can create a larger work that contains this file and distribute that work under terms of your choice.
//


package com.xtone.game87873.section.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.xtone.game87873.R.layout;
import com.xtone.game87873.general.widget.xlistview.XListView;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ClassifyFragment_
    extends com.xtone.game87873.section.game.ClassifyFragment
    implements HasViews, OnViewChangedListener
{

    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();
    private View contentView_;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    @Override
    public View findViewById(int id) {
        if (contentView_ == null) {
            return null;
        }
        return contentView_.findViewById(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView_ = super.onCreateView(inflater, container, savedInstanceState);
        if (contentView_ == null) {
            contentView_ = inflater.inflate(layout.fragment_list, container, false);
        }
        return contentView_;
    }

    @Override
    public void onDestroyView() {
        contentView_ = null;
        super.onDestroyView();
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static ClassifyFragment_.FragmentBuilder_ builder() {
        return new ClassifyFragment_.FragmentBuilder_();
    }

    @Override
    public void onViewChanged(HasViews hasViews) {
        il_loadFailure = ((LinearLayout) hasViews.findViewById(com.xtone.game87873.R.id.il_loadFailure));
        btn_refresh = ((Button) hasViews.findViewById(com.xtone.game87873.R.id.btn_refresh));
        lvContent = ((XListView) hasViews.findViewById(com.xtone.game87873.R.id.lvContent));
        if (btn_refresh!= null) {
            btn_refresh.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ClassifyFragment_.this.refreshClick();
                }

            }
            );
        }
        afterViews();
    }

    public static class FragmentBuilder_
        extends FragmentBuilder<ClassifyFragment_.FragmentBuilder_, com.xtone.game87873.section.game.ClassifyFragment>
    {


        @Override
        public com.xtone.game87873.section.game.ClassifyFragment build() {
            ClassifyFragment_ fragment_ = new ClassifyFragment_();
            fragment_.setArguments(args);
            return fragment_;
        }

    }

}
