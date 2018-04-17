//
// DO NOT EDIT THIS FILE.Generated using AndroidAnnotations 3.3.2.
//  You can create a larger work that contains this file and distribute that work under terms of your choice.
//


package com.xtone.game87873.section.info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xtone.game87873.R.layout;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class RegisterByUserNameFragment_
    extends com.xtone.game87873.section.info.RegisterByUserNameFragment
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
            contentView_ = inflater.inflate(layout.fragment_register_by_user_name, container, false);
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

    public static RegisterByUserNameFragment_.FragmentBuilder_ builder() {
        return new RegisterByUserNameFragment_.FragmentBuilder_();
    }

    @Override
    public void onViewChanged(HasViews hasViews) {
        ll_pwd = ((LinearLayout) hasViews.findViewById(com.xtone.game87873.R.id.ll_pwd));
        tv_toLogin = ((TextView) hasViews.findViewById(com.xtone.game87873.R.id.tv_toLogin));
        ll_userName = ((LinearLayout) hasViews.findViewById(com.xtone.game87873.R.id.ll_userName));
        edt_userName = ((EditText) hasViews.findViewById(com.xtone.game87873.R.id.edt_userName));
        edt_password = ((EditText) hasViews.findViewById(com.xtone.game87873.R.id.edt_password));
        btn_register = ((Button) hasViews.findViewById(com.xtone.game87873.R.id.btn_register));
        if (tv_toLogin!= null) {
            tv_toLogin.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    RegisterByUserNameFragment_.this.toLoginClick();
                }

            }
            );
        }
        if (btn_register!= null) {
            btn_register.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    RegisterByUserNameFragment_.this.registerClick();
                }

            }
            );
        }
    }

    public static class FragmentBuilder_
        extends FragmentBuilder<RegisterByUserNameFragment_.FragmentBuilder_, com.xtone.game87873.section.info.RegisterByUserNameFragment>
    {


        @Override
        public com.xtone.game87873.section.info.RegisterByUserNameFragment build() {
            RegisterByUserNameFragment_ fragment_ = new RegisterByUserNameFragment_();
            fragment_.setArguments(args);
            return fragment_;
        }

    }

}