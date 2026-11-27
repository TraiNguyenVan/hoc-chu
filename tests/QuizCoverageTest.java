package vn.khoaikhoai.hocchu;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
public class QuizCoverageTest {
 public static void main(String[] args) {
  for(int category=0;category<3;category++) for(int seed=0;seed<50;seed++) {
   Quiz quiz=new Quiz(LearningData.items(category),5,new Random(seed));
   Set<String> asked=new HashSet<>();
   for(int i=0;i<5;i++) {
    if(!asked.add(quiz.answer().label)) throw new AssertionError("repeated question category="+category+" seed="+seed+" label="+quiz.answer().label);
    quiz.submit(quiz.answer()); quiz.next();
   }
  }
  System.out.println("PASS: 150 five-question sets without repeated questions");
 }
}
