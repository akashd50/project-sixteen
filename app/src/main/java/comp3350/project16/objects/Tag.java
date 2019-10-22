package comp3350.project16.objects;

public class Tag {
    private int ID;
    private String tag;

    public Tag(String t,int id){
        this.tag = t;
        this.ID = id;
    }

    public int getID() {
        return ID;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String toString(){return this.tag;}

}
