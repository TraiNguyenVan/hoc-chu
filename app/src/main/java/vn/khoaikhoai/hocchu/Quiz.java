package vn.khoaikhoai.hocchu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/** One answer per question; the score changes only on the first submission. */
public final class Quiz {
    private final List<LearningData.Item> pool;
    private final List<LearningData.Item> remaining = new ArrayList<>();
    private final Random random;
    private final int total;
    private int index, score;
    private boolean submitted;
    private LearningData.Item answer, selected;
    private List<LearningData.Item> choices;

    public static final class State implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        int total, index, score;
        String answer, selected;
        ArrayList<String> remaining, choices;
        Random random;
    }
    private static ArrayList<String> labels(List<LearningData.Item> items) {
        ArrayList<String> result = new ArrayList<>();
        for (LearningData.Item item : items) result.add(item.label);
        return result;
    }
    public State saveState() {
        State state = new State();
        state.total=total; state.index=index; state.score=score;
        state.answer=answer.label; state.selected=selected == null ? null : selected.label;
        state.remaining=labels(remaining); state.choices=labels(choices); state.random=random;
        return state;
    }
    private LearningData.Item byLabel(String label) {
        for (LearningData.Item item : pool) if (item.label.equals(label)) return item;
        throw new IllegalArgumentException("Unknown quiz label");
    }
    private Quiz(List<LearningData.Item> pool, State state) {
        this.pool = new ArrayList<>(pool);
        this.total=state.total; this.index=state.index; this.score=state.score; this.random=state.random;
        for (String label : state.remaining) remaining.add(byLabel(label));
        ArrayList<LearningData.Item> restoredChoices = new ArrayList<>();
        for (String label : state.choices) restoredChoices.add(byLabel(label));
        choices=Collections.unmodifiableList(restoredChoices);
        answer=byLabel(state.answer);
        selected=state.selected == null ? null : byLabel(state.selected);
        submitted=selected != null;
    }
    public static Quiz restore(List<LearningData.Item> pool, State state) { return new Quiz(pool, state); }
    public LearningData.Item selected() { return selected; }

    public Quiz(List<LearningData.Item> pool, int total, Random random) {
        if (pool == null || random == null || total < 1) throw new IllegalArgumentException("Invalid quiz");
        this.pool = new ArrayList<>();
        HashSet<String> seen = new HashSet<>();
        for (LearningData.Item item : pool) {
            if (item != null && seen.add(item.label)) this.pool.add(item);
        }
        if (this.pool.size() < 4) throw new IllegalArgumentException("Need four distinct labels");
        this.total = total;
        this.random = random;
        makeQuestion();
    }
    private void makeQuestion() {
        if (remaining.isEmpty()) {
            remaining.addAll(pool);
            Collections.shuffle(remaining, random);
        }
        answer = remaining.remove(remaining.size() - 1);
        List<LearningData.Item> shuffled = new ArrayList<>(pool);
        shuffled.remove(answer);
        Collections.shuffle(shuffled, random);
        List<LearningData.Item> options = new ArrayList<>(shuffled.subList(0, 3));
        options.add(answer);
        Collections.shuffle(options, random);
        choices = Collections.unmodifiableList(options);
        submitted = false;
        selected = null;
    }
    public List<LearningData.Item> options() { return choices; }
    public LearningData.Item answer() { return answer; }
    public boolean submit(LearningData.Item choice) {
        if (finished() || submitted || !choices.contains(choice)) return false;
        submitted = true;
        selected = choice;
        boolean correct = choice == answer;
        if (correct) score++;
        return correct;
    }
    public void next() {
        if (finished() || !submitted) return;
        index++;
        if (!finished()) makeQuestion();
    }
    public boolean finished() { return index >= total; }
    public boolean submitted() { return submitted; }
    public int score() { return score; }
    public int questionNumber() { return Math.min(index + 1, total); }
    public int total() { return total; }
}
