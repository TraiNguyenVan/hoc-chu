package vn.khoaikhoai.hocchu;

import android.app.Activity;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;

import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

/** Tablet learning board with optional, reviewed offline clips. */
public class MainActivity extends Activity {
 private static final int INK=Color.rgb(42,60,66), MUTED=Color.rgb(108,128,128), TEAL=Color.rgb(22,125,118), RED=Color.rgb(194,70,74), BLUE=Color.rgb(33,115,157), CREAM=Color.rgb(255,251,243), LINE=Color.rgb(208,230,221), PINK=Color.rgb(253,234,232), GOLD=Color.rgb(202,130,42), WHITE=Color.WHITE;
 private LinearLayout root,content,nav; private ScrollView scroll; private int section=0,screen=0; private Quiz quiz; private TextView[] navItems=new TextView[2]; private ReviewedAudio reviewedAudio;
 /** Sizing in dp: each chart cell remains a child-friendly touch target. */
 static final class BoardLayout {
  static int columns(int widthDp){return Math.max(1,Math.min(16,widthDp/56));}
 }
 /** A simple painted floral frame; learning cells remain ordinary accessible views. */
 private final class FloralFrame extends LinearLayout {
  private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
  FloralFrame(){super(MainActivity.this);setOrientation(VERTICAL);setPadding(dp(16),dp(14),dp(16),dp(14));setWillNotDraw(false);}
  @Override protected void onDraw(Canvas c){
   super.onDraw(c);float w=getWidth(),h=getHeight(),in=dp(7);paint.setColor(Color.rgb(250,221,228));c.drawRect(0,0,w,h,paint);
   paint.setColor(WHITE);c.drawRect(dp(13),dp(13),w-dp(13),h-dp(13),paint);
   paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(dp(1));paint.setColor(Color.rgb(232,172,185));c.drawRect(in,in,w-in,h-in,paint);paint.setStyle(Paint.Style.FILL);
   for(float x=dp(20);x<w-dp(10);x+=dp(42)){flower(c,x,dp(7));flower(c,x,h-dp(7));}
   for(float y=dp(30);y<h-dp(20);y+=dp(42)){flower(c,dp(7),y);flower(c,w-dp(7),y);}
  }
  private void flower(Canvas c,float x,float y){
   paint.setColor(Color.rgb(255,250,252));float r=dp(2.5f),d=dp(3.2f);
   for(int i=0;i<5;i++){double a=i*Math.PI*2/5;c.drawCircle(x+(float)Math.cos(a)*d,y+(float)Math.sin(a)*d,r,paint);}
   paint.setColor(Color.rgb(224,139,157));c.drawCircle(x,y,dp(1.5f),paint);
  }
 }
 private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
 private GradientDrawable shape(int color,int stroke,float rad){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(rad));if(stroke!=0)g.setStroke(dp(1),stroke);return g;}
 private TextView label(String s,float size,int color,boolean bold){TextView v=new TextView(this);v.setText(s);v.setTextColor(color);v.setTextSize(size);v.setFontFeatureSettings("kern");v.setTypeface(Typeface.create("sans-serif-rounded",bold?Typeface.BOLD:Typeface.NORMAL));return v;}
 private TextView center(String s,float size,int color,boolean bold){TextView v=label(s,size,color,bold);v.setGravity(Gravity.CENTER);return v;}
 private LinearLayout col(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;}
 private LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.HORIZONTAL);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
 private void gap(LinearLayout l,int h){View v=new View(this);l.addView(v,new LinearLayout.LayoutParams(1,dp(h)));}
 private LinearLayout.LayoutParams lp(int w,int h){return new LinearLayout.LayoutParams(w<0?w:dp(w),h<0?h:dp(h));}
 private TextView action(String s,int color,int bg){TextView b=center(s,15,color,true);b.setBackground(shape(bg,0,16));b.setPadding(dp(16),0,dp(16),0);b.setMinHeight(dp(52));b.setClickable(true);b.setFocusable(true);return b;}
 @Override public void onCreate(Bundle b){super.onCreate(b);reviewedAudio=new ReviewedAudio(new AndroidAudioBackend(getAssets()));getWindow().setStatusBarColor(CREAM);getWindow().setNavigationBarColor(CREAM);getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);shell();
  if(b!=null && b.getInt("screen",0)==2 && b.getSerializable("quiz") instanceof Quiz.State){
   section=b.getInt("section",0);
   try{quiz=Quiz.restore(LearningData.items(section),(Quiz.State)b.getSerializable("quiz"));renderQuestion();return;}catch(RuntimeException ignored){quiz=null;}
  }
  if(b!=null && b.getInt("screen",0)==1)showQuizMenu();else showChart(0);
 }
 @Override protected void onSaveInstanceState(Bundle out){super.onSaveInstanceState(out);out.putInt("screen",screen);out.putInt("section",section);if(screen==2 && quiz!=null)out.putSerializable("quiz",quiz.saveState());}
 private void shell(){
  root=col();root.setBackgroundColor(WHITE);setContentView(root);
  nav=row();nav.setPadding(dp(12),dp(4),dp(12),dp(4));root.addView(nav,lp(-1,58));String[] names={"Bảng học","Luyện tập"};
  for(int i=0;i<2;i++){final int n=i;TextView t=center(names[i],14,TEAL,true);t.setClickable(true);t.setFocusable(true);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(48),1);if(i>0)p.leftMargin=dp(8);nav.addView(t,p);navItems[i]=t;t.setOnClickListener(v->{if(n==1)showQuizMenu();else showChart(0);});}
  scroll=new ScrollView(this);scroll.setFillViewport(false);scroll.setClipToPadding(false);scroll.setPadding(dp(8),0,dp(8),dp(16));root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));content=col();scroll.addView(content);
 }
 private void selected(int n){for(int i=0;i<2;i++){navItems[i].setSelected(i==n);navItems[i].setTextColor(i==n?WHITE:TEAL);navItems[i].setBackground(shape(i==n?TEAL:WHITE,i==n?0:LINE,10));}}
 private void reset(){content.removeAllViews();scroll.post(()->scroll.scrollTo(0,0));}
 private void hero(String eyebrow,String heading,String caption,int tint){
  LinearLayout h=col();h.setPadding(dp(20),dp(19),dp(20),dp(17));h.setBackground(shape(tint,0,24));content.addView(h,lp(-1,-2));
  LinearLayout top=row();h.addView(top,lp(-1,-2));TextView eye=label(eyebrow,11,TEAL,true);eye.setLetterSpacing(.10f);top.addView(eye,new LinearLayout.LayoutParams(0,-2,1));top.addView(center("✿  ✧",22,Color.rgb(222,121,130),true));
  gap(h,7);TextView title=label(heading,27,INK,true);title.setLineSpacing(dp(1),1f);h.addView(title);gap(h,5);h.addView(label(caption,14,MUTED,false));
 }
 private void showChart(int ignored){
  screen=0;quiz=null;selected(0);reset();
  FloralFrame board=new FloralFrame();content.addView(board,lp(-1,-2));
  TextView title=center("CHỮ CÁI CHỮ GHÉP VÀ VẦN",23,GOLD,true);title.setPadding(dp(4),dp(9),dp(4),dp(5));board.addView(title,lp(-1,-2));
  TextView hint=center("Chạm vào một ô để nghe",13,INK,false);hint.setPadding(0,0,0,dp(10));board.addView(hint,lp(-1,-2));
  // Reflow each photographed row separately so narrow screens keep the original reading order.
  int widthDp=(int)(getResources().getDisplayMetrics().widthPixels/getResources().getDisplayMetrics().density)-48;
  int availableColumns=BoardLayout.columns(widthDp);
  for(int category=0;category<3;category++){
   java.util.List<LearningData.Item> items=LearningData.items(category);
   int start=0;
   while(start<items.size()){
    int originalRow=category==0?(start==0?16:13):category==1?14:13;
    int end=Math.min(items.size(),start+originalRow);
    int columns=Math.min(availableColumns,category==2?13:16);
    GridLayout grid=new GridLayout(this);grid.setColumnCount(columns);board.addView(grid,lp(-1,-2));
    for(int n=0;n<end-start;n++){
     final LearningData.Item item=items.get(start+n);final int itemCategory=category;
     TextView cell=center(item.label,23,category==2?BLUE:RED,true);
     cell.setContentDescription((category==0?"Chữ cái ":category==1?"Chữ ghép ":"Vần ")+item.label);
     cell.setBackground(shape(WHITE,TEAL,0));cell.setClickable(true);cell.setFocusable(true);
     GridLayout.LayoutParams p=new GridLayout.LayoutParams(GridLayout.spec(n/columns),GridLayout.spec(n%columns,1f));p.width=0;p.height=dp(56);grid.addView(cell,p);
     cell.setOnClickListener(v->playCell(cell,itemCategory,item.label));
    }
    // Blank cells preserve alignment without creating inactive touch targets.
    int remainder=(end-start)%columns;
    if(remainder!=0)for(int n=remainder;n<columns;n++){
     View blank=new View(this);blank.setBackground(shape(WHITE,TEAL,0));
     GridLayout.LayoutParams p=new GridLayout.LayoutParams(GridLayout.spec((end-start)/columns),GridLayout.spec(n,1f));p.width=0;p.height=dp(56);grid.addView(blank,p);
    }
    start=end;
   }
  }
  gap(board,10);
 }
 private void playCell(TextView cell,int category,String text){
  cell.animate().cancel();cell.setScaleX(1f);cell.setScaleY(1f);
  cell.animate().scaleX(.92f).scaleY(.92f).setDuration(90).withEndAction(()->cell.animate().scaleX(1f).scaleY(1f).setDuration(120).start()).start();
  Runnable error=()->runOnUiThread(()->Toast.makeText(this,"Âm đọc không phát được",Toast.LENGTH_SHORT).show());
  if(!reviewedAudio.play(category,text,error))Toast.makeText(this,"Âm đọc chưa có hoặc không phát được",Toast.LENGTH_SHORT).show();
 }
 private void showQuizMenu(){screen=1;quiz=null;selected(1);reset();hero("GÓC LUYỆN TẬP  ✦","Bé thử sức nhé!","Chọn một nhóm để bắt đầu trò chơi.",Color.rgb(255,240,219));gap(content,20);String[] names={"Chữ cái","Chữ ghép","Vần"};String[] subs={"Làm quen với 29 chữ cái","Cùng nhận biết chữ ghép","Khám phá các vần"};String[] icons={"a","ch","an"};for(int i=0;i<3;i++){final int cat=i;LinearLayout card=row();card.setPadding(dp(14),dp(12),dp(14),dp(12));card.setBackground(shape(WHITE,LINE,20));card.setClickable(true);card.setFocusable(true);content.addView(card,lp(-1,92));TextView ico=center(icons[i],27,i==2?BLUE:RED,true);ico.setBackground(shape(i==2?Color.rgb(235,247,248):PINK,0,16));card.addView(ico,lp(62,62));LinearLayout details=col();LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1);p.leftMargin=dp(15);card.addView(details,p);details.addView(label(names[i],18,INK,true));gap(details,3);details.addView(label(subs[i],13,MUTED,false));card.addView(center("›",27,TEAL,true),lp(34,48));card.setOnClickListener(v->{section=cat;quiz=new Quiz(LearningData.items(cat),5,new Random());renderQuestion();});gap(content,10);}}
 private void updateQuizOptions(GridLayout grid){
  for(int i=0;i<grid.getChildCount();i++){
   TextView b=(TextView)grid.getChildAt(i);LearningData.Item option=quiz.options().get(i);
   boolean chosen=option==quiz.selected(),correct=option==quiz.answer(),locked=quiz.submitted();
   b.setEnabled(!quiz.submitted());
   b.setTextColor(locked&&!chosen?Color.rgb(84,100,100):INK);
   b.setBackground(shape(chosen?(correct?Color.rgb(223,245,232):Color.rgb(255,233,228)):locked?Color.rgb(245,247,245):WHITE,chosen?(correct?TEAL:RED):LINE,18));
  }
 }
 private void renderQuestion(){screen=2;selected(1);reset();if(quiz.finished()){hero("GIỎI LẮM  ✦","Bé đã hoàn thành!","Mỗi lần luyện tập là một bước tiến nhỏ.",Color.rgb(227,244,235));gap(content,20);TextView score=center(quiz.score()+" / "+quiz.total(),48,TEAL,true);score.setBackground(shape(WHITE,LINE,22));content.addView(score,lp(-1,150));gap(content,16);TextView again=action("Chơi thêm một lượt  →",WHITE,TEAL);content.addView(again,lp(-1,56));again.setOnClickListener(v->showQuizMenu());return;}
  hero("CÂU "+quiz.questionNumber()+" / "+quiz.total(),"Tìm chữ giống mẫu","Nhìn thật kỹ rồi chọn đáp án nhé.",Color.rgb(255,240,219));gap(content,16);TextView target=center(quiz.answer().label,64,section==2?BLUE:RED,true);target.setBackground(shape(WHITE,LINE,22));content.addView(target,lp(-1,126));gap(content,14);GridLayout grid=new GridLayout(this);grid.setColumnCount(2);content.addView(grid,lp(-1,-2));TextView feedback=center("Chọn một ô để trả lời",14,MUTED,false);
  if(quiz.submitted()){boolean correct=quiz.selected()==quiz.answer();feedback.setText(correct?"Đúng rồi, giỏi quá!":"Chưa đúng. Đáp án là: "+quiz.answer().label);feedback.setTextColor(correct?TEAL:RED);}
  int i=0;for(LearningData.Item option:quiz.options()){TextView b=center(option.label,27,INK,true);b.setClickable(true);b.setFocusable(true);GridLayout.LayoutParams p=new GridLayout.LayoutParams(GridLayout.spec(i/2),GridLayout.spec(i%2,1f));p.width=0;p.height=dp(72);p.setMargins(dp(4),dp(4),dp(4),dp(4));grid.addView(b,p);b.setOnClickListener(v->{if(quiz.submitted())return;boolean correct=quiz.submit(option);updateQuizOptions(grid);feedback.setText(correct?"Đúng rồi, giỏi quá!":"Chưa đúng. Đáp án là: "+quiz.answer().label);feedback.setTextColor(correct?TEAL:RED);feedback.announceForAccessibility(feedback.getText());});i++;}updateQuizOptions(grid);
  gap(content,6);content.addView(feedback,lp(-1,42));gap(content,4);TextView next=action("Tiếp theo  →",WHITE,TEAL);content.addView(next,lp(-1,56));next.setOnClickListener(v->{if(quiz.submitted()){quiz.next();renderQuestion();}else feedback.setText("Con chọn một đáp án nhé!");});gap(content,8);TextView exit=action("Thoát luyện tập",TEAL,Color.rgb(230,245,238));content.addView(exit,lp(-1,52));exit.setOnClickListener(v->showQuizMenu());
 }
 @Override protected void onStop(){reviewedAudio.stop();super.onStop();}
 @Override protected void onDestroy(){reviewedAudio.stop();super.onDestroy();}
 @Override public void onBackPressed(){if(screen==2)showQuizMenu();else if(screen==1)showChart(section);else super.onBackPressed();}
}
