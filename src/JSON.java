import org.json.simple.*;

public class JSON {
    // class name to be used as tag in JSON representation
    private static final String _class = JSON.class.getSimpleName();

    // private fields
    private String Username;
    private String title;
    private String Post;

    // Constructor; throws NullPointerException if any arguments are null.
    public JSON(String username, String post, String title) {
        // check for nulls
        if (username == null || post == null || title == null)
            throw new NullPointerException();
        this.Username = username;
        this.Post = post;
        this.title = title;
    }

    // getters
    public String getEmployee() {
        return Username;
    }

    public String getPost() {
        return Post;
    }

    public String GetTitle() {
        return title;
    }

    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        // serialize this as a JSONObject
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("username", Username);
        obj.put("title", title);
        obj.put("post", Post);
        return obj;
    }

    // Returns null if deserialization was not successful (e.g. because
    // the JSONObject represents an object of a different class).
    public static JSON fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject) val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize username and post
            String username = (String) obj.get("Username");
            String title = (String) obj.get("title");
            String post = (String) obj.get("Post");

            // construct the object to return (checking for nulls)
            return new JSON(username, post, title);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}