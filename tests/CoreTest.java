package vn.khoaikhoai.hocchu;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class CoreTest {
    private static void check(boolean yes, String message) { if (!yes) throw new AssertionError(message); }
    public static void main(String[] args) {
        check(LearningData.alphabet().size() == 29, "Bảng chữ cái phải có 29 chữ cái");
        List<LearningData.Item> letters = LearningData.items(0);
        check(letters.size() == 29, "Mỗi chữ cái có một bài học");
        check(LearningData.items(1).size() >= 10, "Có chữ ghép");
        check(LearningData.items(2).size() >= 10, "Có bài vần");
        Quiz quiz = new Quiz(letters, 5, new Random(1));
        int score = 0;
        for (int i=0; i<5; i++) {
            check(!quiz.finished(), "Chưa hết câu hỏi");
            List<LearningData.Item> options = quiz.options();
            check(options.size() == 4 && new HashSet<>(options).size() == 4, "Có 4 đáp án khác nhau");
            check(options.contains(quiz.answer()), "Có đáp án đúng");
            LearningData.Item pick = i == 0 ? options.get((options.indexOf(quiz.answer())+1)%4) : quiz.answer();
            boolean correct = quiz.submit(pick);
            check(correct == (i != 0), "Đánh giá câu trả lời");
            if (correct) score++;
            check(!quiz.submit(quiz.answer()), "Không chấm hai lần");
            quiz.next();
        }
        check(quiz.finished() && quiz.score() == score && score == 4, "Điểm và kết thúc");
        boolean rejected = false;
        try { new Quiz(Arrays.asList(letters.get(0)), 1, new Random(1)); } catch (IllegalArgumentException e) { rejected=true; }
        check(rejected, "Không tạo câu hỏi khi thiếu đáp án");
        System.out.println("PASS: học liệu, lựa chọn, chấm điểm và kết thúc");
    }
}
