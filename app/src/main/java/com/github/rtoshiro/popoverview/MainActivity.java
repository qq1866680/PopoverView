package com.github.rtoshiro.popoverview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.rtoshiro.poview.PopoverView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PopoverView popoverView;

    private android.widget.Button lefttop;
    private android.widget.Button top;
    private android.widget.Button righttop;
    private android.widget.Button right;
    private android.widget.Button rightbottom;
    private android.widget.Button bottom;
    private android.widget.Button leftbottom;
    private android.widget.Button left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.left = (Button) findViewById(R.id.left);
        this.leftbottom = (Button) findViewById(R.id.leftbottom);
        this.bottom = (Button) findViewById(R.id.bottom);
        this.rightbottom = (Button) findViewById(R.id.rightbottom);
        this.right = (Button) findViewById(R.id.right);
        this.righttop = (Button) findViewById(R.id.righttop);
        this.top = (Button) findViewById(R.id.top);
        this.lefttop = (Button) findViewById(R.id.lefttop);

        left.setOnClickListener(this);
        leftbottom.setOnClickListener(this);
        bottom.setOnClickListener(this);
        rightbottom.setOnClickListener(this);
        right.setOnClickListener(this);
        righttop.setOnClickListener(this);
        top.setOnClickListener(this);
        lefttop.setOnClickListener(this);

        popoverView = new PopoverView(this);
        popoverView.setContentView(R.layout.popoverview_layout);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left: {
                popoverView.show(left, PopoverView.PopoverViewPosition.Any);
                break;
            }
            case R.id.leftbottom: {
                popoverView.show(leftbottom, PopoverView.PopoverViewPosition.Any);
                break;
            }
            case R.id.bottom: {
                popoverView.show(bottom, PopoverView.PopoverViewPosition.Any);
                break;
            }
            case R.id.rightbottom: {
                popoverView.show(rightbottom, PopoverView.PopoverViewPosition.Any);
                break;
            }
            case R.id.right: {
                popoverView.show(right, PopoverView.PopoverViewPosition.Any);
                break;
            }
            case R.id.righttop: {
                popoverView.show(righttop, PopoverView.PopoverViewPosition.Any);
                break;
            }
            case R.id.top: {
                popoverView.show(top, PopoverView.PopoverViewPosition.Any);
                break;
            }
            case R.id.lefttop: {
                popoverView.show(lefttop, PopoverView.PopoverViewPosition.Any);
                break;
            }
        }
    }
}
