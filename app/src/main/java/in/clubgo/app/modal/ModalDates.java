package in.clubgo.app.modal;

/**
 * Created by Jitendra Soam on 1/5/16.
 */
public class ModalDates {

    private String date;
    private String day;

    public ModalDates() {
    }

    public ModalDates(String date, String day) {
        this.date = date;
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
