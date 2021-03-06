//
// DO NOT EDIT THIS FILE.Generated using AndroidAnnotations 3.3.2.
//  You can create a larger work that contains this file and distribute that work under terms of your choice.
//


package com.xtone.game87873.section.game;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.xtone.game87873.R.layout;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class RankFragment_
    extends com.xtone.game87873.section.game.RankFragment
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
            contentView_ = inflater.inflate(layout.fragment_rank, container, false);
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

    public static RankFragment_.FragmentBuilder_ builder() {
        return new RankFragment_.FragmentBuilder_();
    }

    @Override
    public void onViewChanged(HasViews hasViews) {
        rbDanji = ((RadioButton) hasViews.findViewById(com.xtone.game87873.R.id.rbDanji));
        iv2 = ((ImageView) hasViews.findViewById(com.xtone.game87873.R.id.iv2));
        rbWangyou = ((RadioButton) hasViews.findViewById(com.xtone.game87873.R.id.rbWangyou));
        vpRank = ((ViewPager) hasViews.findViewById(com.xtone.game87873.R.id.vpRank));
        iv1 = ((ImageView) hasViews.findViewById(com.xtone.game87873.R.id.iv1));
        rgRank = ((RadioGroup) hasViews.findViewById(com.xtone.game87873.R.id.rgRank));
        afterViews();
    }

    public static class FragmentBuilder_
        extends FragmentBuilder<RankFragment_.FragmentBuilder_, com.xtone.game87873.section.game.RankFragment>
    {


        @Override
        public com.xtone.game87873.section.game.RankFragment build() {
            RankFragment_ fragment_ = new RankFragment_();
            fragment_.setArguments(args);
            return fragment_;
        }

    }

}
