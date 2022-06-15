package com.example.nadri4_edit1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

public class PageList extends Activity {
    TextView tvPageDate;
    Button btnBack, btnCreate;
    GridView gridView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_of_the_day);

        //xml변수 연결
        tvPageDate = (TextView) findViewById(R.id.tvPageDate);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        gridView = (GridView) findViewById(R.id.gridView);
        MyGridAdapter gridAdapter = new MyGridAdapter(this);

        gridView.setAdapter(gridAdapter);

        //인텐트
        Intent intent = getIntent();
        int iDay = intent.getIntExtra("SelectedDATE",-1);

        //화면 설정
        setView(iDay);


        //화면전환
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PhotoList.class);

                //day값만 넘겨주기
                intent.putExtra("SelectedDATE", iDay);
                //전송
                startActivity(intent);
                finish();
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String dateFormat(Calendar calendar, int day){  //2022년 5월

        //년월 포맷
        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy   MM");
        return date.format(formatter);*/
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;

        String date = year + "년  " + month + "월  " + day + "일 ";

        return date;
    }

    //화면 설정
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setView(int day){
        tvPageDate.setText(dateFormat(CalendarUtil.selectedDate, day));
    }

    //그리드뷰 어댑터
    public class MyGridAdapter extends BaseAdapter {
        Context context;

        Integer[] pageID = {R.drawable.gomurea1, R.drawable.gomurea2, R.drawable.gomurea3, R.drawable.gomurea4, R.drawable.gomurea5};


        public MyGridAdapter(Context c) {
            context = c;
        }

        //이미지 개수 받기 (그 개수만큼 반복시키기 위해)
        @Override
        public int getCount() {
            return pageID.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        //getCount에서 받은 값만큼 반복된다.
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imgView = new ImageView(context);

            imgView.setLayoutParams(new GridView.LayoutParams(200, 300));
            imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imgView.setPadding(5,5,5,5);

            imgView.setImageResource(pageID[i]);

            return imgView;
        }
    }
}
