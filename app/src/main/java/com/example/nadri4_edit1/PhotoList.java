package com.example.nadri4_edit1;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class PhotoList extends AppCompatActivity {

    //이미지의 uri를 담는 리스트
    ArrayList<Uri> uriList = new ArrayList<>();

    RecyclerView recyclerView;  //이미지를 보여주는 뷰
    MultiImageAdapter adapter;

    Button btnGetImage, btnSave;
    TextView tvPageDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_in_the_gallery);

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        //xml연결
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        btnGetImage = (Button) findViewById(R.id.btnGetImage);
        tvPageDate = (TextView) findViewById(R.id.tvPageDate);
        btnSave = (Button) findViewById(R.id.btnSave);

        Log.d("GOO", "well come");
        //서버테스트
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("GOO", "hwa");
                ReqServer.request(PhotoList.this, 0);

            }
        });

        //인텐트
        Intent intent = getIntent();
        int iDay = intent.getIntExtra("SelectedDATE",-1);

        //화면 설정
        setView(iDay);

        //이미지 가져오기 버튼
        btnGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

                /*Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(Intent.createChooser(intent,"다중 선택은 '포토'를 선택하세요."), 1);*/
                //intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(Intent.createChooser(intent, "다중선택"), 1);

                /*startActivityForResult(Intent.createChooser(intent, "Add images"), SELECT_MULTIPLE_IMAGES);*/
            }
        });
    }

    //앨범에서 액티비티로 돌아온 후 실행되는 메서드


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null){
            Toast.makeText(this, "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
        }//선택된 이미지 없음
        else{
            if(data.getClipData() == null){
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                uriList.add(imageUri);

                adapter = new MultiImageAdapter(uriList, getApplicationContext());

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
                try {
                    ExifInterface exif = new ExifInterface(getRealPathFromURI(imageUri));
                    Log.d("HWA", String.valueOf(getRealPathFromURI(imageUri)));
                    ReqServer.request(PhotoList.this, exif.getDateTime());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("HWA", e + "");
                }
            }//이미지를 하나만 선택
            else{
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                for(int i=0; i< clipData.getItemCount(); i++){
                    Uri imageUri = clipData.getItemAt(i).getUri();  //선택한 이미지들의 uri를 가져온다.
                    try {
                        uriList.add(imageUri);  //uri를 리스트에 담는다
                    }catch (Exception e){
                        Log.d("ERR", "file select error");
                    }
                }

                adapter = new MultiImageAdapter(uriList, getApplicationContext());
                recyclerView.setAdapter(adapter);

                //리사이클러뷰 수직 스크롤 적용
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
            }//이미지를 여러장 선택
        }//이미지 선택함
    }

    //파일경로 Uri를 String으로 바꿔주는 함수
    private String getRealPathFromURI(Uri contentUri) {
        if (contentUri.getPath().startsWith("/storage")) {
            return contentUri.getPath();
        }

        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = { MediaStore.Files.FileColumns.DATA };
        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
        try {
            int columnIndex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        } finally {
            cursor.close();
        }
        return null;
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
}