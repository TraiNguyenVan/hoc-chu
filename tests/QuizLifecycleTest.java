package vn.khoaikhoai.hocchu;

import java.lang.reflect.Method;
import java.io.*;
import java.util.*;

public class QuizLifecycleTest {
 static void check(boolean ok, String msg) { if (!ok) throw new AssertionError(msg); }
 static Quiz restore(Quiz q, int category) throws Exception {
  Method save;
  try { save = Quiz.class.getMethod("saveState"); }
  catch (NoSuchMethodException e) { throw new AssertionError("quiz cannot save lifecycle state"); }
  Object state = save.invoke(q);
  ByteArrayOutputStream bytes = new ByteArrayOutputStream();
  new ObjectOutputStream(bytes).writeObject(state);
  Object copy = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())).readObject();
  return (Quiz) Quiz.class.getMethod("restore", List.class, state.getClass()).invoke(null, LearningData.items(category), copy);
 }
 static String labels(List<LearningData.Item> items) { String s=""; for (LearningData.Item i:items) s+=i.label+"|"; return s; }
 public static void main(String[] args) throws Exception {
  for(int category=0;category<3;category++) {
   Quiz q=new Quiz(LearningData.items(category),5,new Random(17));
   q.submit(q.answer()); q.next();
   LearningData.Item wrong=q.options().get(0)==q.answer()?q.options().get(1):q.options().get(0);
   q.submit(wrong);
   Quiz r=restore(q,category);
   check(r.questionNumber()==2 && r.score()==1 && r.submitted(),"progress lost");
   check(r.answer().label.equals(q.answer().label) && labels(r.options()).equals(labels(q.options())),"question changed");
   check(((LearningData.Item) Quiz.class.getMethod("selected").invoke(r)).label.equals(wrong.label),"selection lost");
   check(!r.submit(r.answer()) && r.score()==1,"double submission changed score");
   while(!q.finished()) { q.next(); r.next(); check(q.finished()==r.finished(),"completion changed"); if(!q.finished()) {check(q.answer().label.equals(r.answer().label) && labels(q.options()).equals(labels(r.options())),"future question changed"); q.submit(q.answer()); r.submit(r.answer());} }
   Quiz done=restore(r,category); check(done.finished() && done.score()==r.score(),"result lost");
  }
  System.out.println("PASS: quiz lifecycle state and future questions");
 }
}
