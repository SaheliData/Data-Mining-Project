package leak;

/**
 * Created by Ethan on 12/6/16.
 */
public class LeakObj {
    int display_id;
    String uuid;
    int ad_id;
    int promoted_document_id;
    long timestamp;
    String platform;
    String geo_location;
    boolean clicked;

    public LeakObj(){
        clicked = false;
    }

    @Override
    public String toString() {
        return "LeakObj{" +
                "display_id=" + display_id +
                ", uuid='" + uuid + '\'' +
                ", ad_id=" + ad_id +
                ", promoted_document_id=" + promoted_document_id +
                ", timestamp=" + timestamp +
                ", platform='" + platform + '\'' +
                ", geo_location='" + geo_location + '\'' +
                ", clicked=" + clicked +
                '}';
    }
}
