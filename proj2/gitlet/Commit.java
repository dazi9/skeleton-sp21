package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import static gitlet.Utils.*;
import static java.util.Collections.sort;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The SHA-1 hash of the Commit. */
    private String commitSHA1;

    /** The date of the Commit. */
    private Date date;

    /** The message of this Commit. */
    private String message;

    /** The parent of current commit. */
    private String parentSHA1;

    /** The set of reflection: filename → SHA1. */
    private HashMap<String, String> map;

    /* TODO: fill in the rest of this class. */

    /*The constructor*/
    public Commit(String message, String parentSHA1, HashMap<String, String> map) {
        this.message = message;
        this.parentSHA1 = parentSHA1;
        if (message.equals("initial commit")) {
            date = new Date(0);
        } else {
            date = new Date(System.currentTimeMillis());
        }
        this.map = new HashMap<>(map);
        TreeMap<String, String> m = new TreeMap<>(this.map);
        ArrayList<Object> list = new ArrayList<>(3 + 2 * m.size());
        list.add(message);
        if (parentSHA1 != null) {
            list.add(parentSHA1);
        }
        list.add(date.toString());
        for (String key : m.keySet()) {
            list.add(key);
            list.add(m.get(key));
        }
        this.commitSHA1 = sha1(list);
    }

    public String getCommitSHA1() {
        return commitSHA1;
    }

    public HashMap<String, String> getMap() {
        return map;
    }

    public String getParentSHA1() {
        return parentSHA1;
    }

    public Date getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    /** Make the initial commit. */
    public void initCommit() {
        date = new Date(0);
        message = "initial commit";
        map = new HashMap<>();
        commitSHA1 = sha1(date.toString(), message, map);
    }
}
