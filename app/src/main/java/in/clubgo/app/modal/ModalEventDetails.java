package in.clubgo.app.modal;

/**
 * Created by Jitendra Soam on 1/5/16.
 */
public class ModalEventDetails {

    private int image;
    private String title, subTitle;

    public ModalEventDetails() {
    }

    public ModalEventDetails(int image, String title, String subTitle) {
        this.image = image;
        this.title = title;
        this.subTitle = subTitle;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}
