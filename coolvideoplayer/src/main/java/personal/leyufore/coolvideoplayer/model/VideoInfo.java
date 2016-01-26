package personal.leyufore.coolvideoplayer.model;

import android.graphics.Bitmap;

public class VideoInfo {

    private String	name;
    private String	size;
    private String  path;
    private Bitmap	type;
    private Bitmap 	thumbnail;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public VideoInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Bitmap getType() {
        return type;
    }

    public void setType(Bitmap type) {
        this.type = type;
    }

}
