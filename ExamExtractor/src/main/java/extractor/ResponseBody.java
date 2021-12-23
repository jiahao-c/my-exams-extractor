package extractor;

import java.util.List;

public class ResponseBody {
    public List<ExamSession> exams;

    public ResponseBody(List<ExamSession> exams) {
        this.exams = exams;
    }
}
