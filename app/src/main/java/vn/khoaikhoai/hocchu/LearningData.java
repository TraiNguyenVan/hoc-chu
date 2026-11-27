package vn.khoaikhoai.hocchu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Nội dung bảng học. Ví dụ chưa kiểm định được để trống. */
public final class LearningData {
    public static final class Item {
        public final String label, example, speak;
        Item(String label, String example, String speak) { this.label=label; this.example=example; this.speak=speak; }
    }
    private LearningData() {}
    public static List<String> alphabet() {
        return Arrays.asList("a", "ă", "â", "b", "c", "d", "đ", "e", "ê", "g", "h", "i", "k", "l", "m", "n", "o", "ô", "ơ", "p", "q", "r", "s", "t", "u", "ư", "v", "x", "y");
    }
    public static List<Item> items(int category) {
        List<Item> result = new ArrayList<>();
        if (category == 0) {
            String[] examples = {"áo","ăn","ấm","bà","cá","dê","đá","em","ếch","gà","hoa","in","kể","lá","mẹ","na","ong","ô tô","cờ","pin","quà","rổ","sáo","táo","ủ","thư","voi","xe","y tá"};
            String[] sounds = {"a","ă","â","bờ","cờ","dờ","đờ","e","ê","gờ","hờ","i","cờ","lờ","mờ","nờ","o","ô","ơ","pờ","quờ","rờ","sờ","tờ","u","ư","vờ","xờ","i"};
            for (int i=0;i<alphabet().size();i++) result.add(new Item(alphabet().get(i),examples[i],sounds[i]));
        } else if (category == 1) {
            String[][] rows={{"ch","chim","chờ"},{"gh","ghế","gờ"},{"gi","giỏ","giờ"},{"ng","ngô","ngờ"},{"nh","nhà","nhờ"},{"kh","khế","khờ"},{"ph","phố","phờ"},{"qu","quà","quờ"},{"th","thỏ","thờ"},{"tr","tre","trờ"},{"ngh","nghỉ","ngờ"}};
            for (String[] r:rows) result.add(new Item(r[0],r[1],r[2]));
            // The final three red chart cells are vowel sequences, not consonant digraphs.
            // Keep them here to preserve the photographed row; examples remain unreviewed.
            for (String s : new String[]{"ia", "ua", "ưa"}) result.add(new Item(s, "", s));
        } else if (category == 2) {
            String content="an at am ap ang ac anh ach ai ay ao au ăn " +
                "ăt ân ât en et ên êt in it on ot ôn ôt " +
                "ơn ơt un ut ưt iên iêt yên yêt uôn uôt ươn ươt " +
                "ăm ăp âm âp em ep êm êp im ip om op ôm " +
                "ôp ơm ơp um up uôm ươm ươp iêm iêp yêm ăng ăc " +
                "âng âc eng ec ong oc ông ôc ung uc ưng ưc iêng " +
                "iêc yêng uông uôc ương ươc ênh êch inh ich âu ây eo " +
                "êu iu ưu oi ôi ơi ui ưi uôi ươi iêu yêu ươu " +
                "oa oe uê uy uya oan oat oăn oăt oen oet uân uât " +
                "uyn uyt uyên uyêt oam oăm oap oang oac oăng oăc oanh oach " +
                "uynh uych oai oay uây oong ooc uơ uênh uêch uâng uyp";
            for (String s:content.split(" ")) result.add(new Item(s,"",s));
        } else throw new IllegalArgumentException("Unknown category");
        return result;
    }
}
