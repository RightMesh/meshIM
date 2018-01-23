package io.left.meshenger.Activities;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import io.left.meshenger.Models.User;
import io.left.meshenger.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ChooseAvatarActivity extends Activity {
    User user = new User();
    private int rows =10;
    private int col =3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_avatar);
      setupAvatars();
    }

    private void setupAvatars(){
        user.load(this);
        ScrollView scrollView = findViewById(R.id.avatarScrollView);
        TableLayout tableLayout = new TableLayout(this);


        for(int r = 0;r<rows;r++){
            final int curRow = r;

            //start a new row
            TableRow tableRow = new TableRow(this);

            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT
                    ,TableLayout.LayoutParams.MATCH_PARENT,1.0f));
           // tableLayout.addView(tableRow);
            //fill in the column
            for(int c = 0;c<col;c++){
                final ImageButton imageButton = new ImageButton(this);
                imageButton.setBackgroundResource(R.drawable.avatar);
                //figureout a way to do it by code
                //setup al the image icon
                imageButton.setImageResource(R.mipmap.rm_launcher);
                imageButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,1.0f));
                imageButton.setPadding(0,0,0,0);
                tableRow.addView(imageButton);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                      //  String rid = getResources().getResourceEntryName(v.getDra);
                      //  changeCurrentAvatar(rid);
                      //  String s= (String) imageButton.getTag();
                        //Toast.makeText(ChooseAvatarActivity.this,s,Toast.LENGTH_SHORT).show();
                       // user.setUserAvatar(getResources().getIdentifier(s,"mipmap",getPackageName()));
                    }
                });
            }

            tableLayout.addView(tableRow);

        }
        scrollView.addView(tableLayout);
    }

    private void changeCurrentAvatar(String id){
        ImageButton button = findViewById(R.id.selectedAvatar);
        button.setImageDrawable(Drawable.createFromPath(id));
    }
}
