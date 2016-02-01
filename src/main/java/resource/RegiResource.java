package resource;

import org.mongodb.morphia.annotations.Embedded;

/**
 * Created by jilongsun on 7/15/15.
 */
@Embedded
public class RegiResource {
    private int recourse_id;

    public int getRecourse_id() {
        return recourse_id;
    }

    @Override
    public String toString() {
        return "RegiResource{" +
                "recourse_id=" + recourse_id +
                '}';
    }

    public void setRecourse_id(int recourse_id) {
        this.recourse_id = recourse_id;
    }
}
