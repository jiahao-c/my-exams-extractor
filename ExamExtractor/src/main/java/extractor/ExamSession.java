package extractor;

public class ExamSession {
    String term = "fall21";
    String course;
    String section;
    String title;
    String type;
    String start;
    String end;
    String building;
    String room;
    String row;
    String from;
    String to;

    public ExamSession(String course, String section, String title, String type, String start, String end,
            String building, String room, String row, String from, String to) {
        this.course = course;
        this.section = section;
        this.title = title;
        this.type = type;
        this.start = start;
        this.end = end;
        this.building = building;
        this.room = room;
        this.row = row;
        this.from = from;
        this.to = to;
    }

}
